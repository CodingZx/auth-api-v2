package fml.plus.auth.service;

import com.fasterxml.uuid.Generators;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;
import fml.plus.auth.common.constants.GlobalConstants;
import fml.plus.auth.common.constants.RedisConstants;
import fml.plus.auth.common.context.UserThreadInfo;
import fml.plus.auth.common.exception.BusinessException;
import fml.plus.auth.common.model.Token;
import fml.plus.auth.common.redisson.lock.LockService;
import fml.plus.auth.common.util.GsonUtils;
import fml.plus.auth.common.util.PasswordUtils;
import fml.plus.auth.common.util.RSAUtils;
import fml.plus.auth.common.util.SpringUtils;
import fml.plus.auth.common.util.time.LocalDateTimeUtils;
import fml.plus.auth.dto.cache.LoginRsaCache;
import fml.plus.auth.dto.req.UpdatePwdReq;
import fml.plus.auth.dto.resp.CurrentMenuResp;
import fml.plus.auth.dto.resp.LoginResp;
import fml.plus.auth.entity.AccountEntity;
import fml.plus.auth.entity.IPLimitEntity;
import fml.plus.auth.entity.MenuEntity;
import fml.plus.auth.entity.RoleMenuEntity;
import fml.plus.auth.mapper.AdminMapper;
import fml.plus.auth.mapper.IPLimitMapper;
import fml.plus.auth.mapper.MenuMapper;
import fml.plus.auth.mapper.RoleMenuMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static fml.plus.auth.common.constants.RedisConstants.NULL_VAL;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(rollbackFor = Exception.class)
public class LoginService {
    private StringRedisTemplate redis;
    private AdminMapper adminMapper;
    private RoleMenuMapper roleMenuMapper;
    private MenuMapper menuMapper;
    private LockService lockService;
    private IPLimitMapper ipLimitMapper;

    public String getKey() {
        var ops = redis.boundValueOps(RedisConstants.LoginKeys.rsaKey());
        var val = ops.get();
        if(Strings.isNullOrEmpty(val)) {
            var pair = RSAUtils.generateKeyPair(2048);
            var cache = new LoginRsaCache();
            cache.setPublicKey(RSAUtils.getEncodedKey(pair.getPublic()));
            cache.setPrivateKey(RSAUtils.getEncodedKey(pair.getPrivate()));
            ops.set(GsonUtils.gson().toJson(cache), 5, TimeUnit.MINUTES);
            return cache.getPublicKey();
        }
        return GsonUtils.gson().fromJson(val, LoginRsaCache.class).getPublicKey();
    }

    public LoginResp login(String userName, String password, String ip){
        ipLimitCheck(ip);

        var ops = redis.boundValueOps(RedisConstants.LoginKeys.rsaKey());
        var val = ops.get();
        if(Strings.isNullOrEmpty(val)) {
            throw new BusinessException("账号密码不正确");
        }
        password = RSAUtils.decrypt(password, GsonUtils.gson().fromJson(val, LoginRsaCache.class).getPrivateKey());

        var admin = adminMapper.findByUserName(userName.trim());
        if(admin == null || !PasswordUtils.verify(password, admin.getPassword())) {
            setIpLimit(ip, userName, Strings.nullToEmpty(password));
            throw new BusinessException("账号密码不正确");
        }

        String token = Token.make(admin).str();

        // 保存登录用户token
        redis.opsForValue().set(RedisConstants.getTokenKey(admin.getId()), token, GlobalConstants.Token.EXPIRE_DAYS, TimeUnit.DAYS);

        LoginResp response = new LoginResp();
        response.setToken(token);
        response.setPermissions(getPermissions(admin.getRoleId()));
        return response;
    }

    public void logout(UUID loginId) {
        redis.delete(RedisConstants.getTokenKey(loginId));
    }

    public boolean checkPermissions(String[] needPerms, UUID roleId) {
        if(needPerms == null || needPerms.length == 0) return false;
        var permissions = getPermissions(roleId);
        return Arrays.stream(needPerms).filter(f -> !Strings.isNullOrEmpty(f)).anyMatch(perm -> permissions.stream().anyMatch(code -> code.equals(perm)));
    }

    private List<String> getPermissions(UUID roleId) {
        var ops = redis.boundValueOps(RedisConstants.MenuKeys.getCurrentPermissionKey(roleId));
        var value = ops.get();
        if(!Strings.isNullOrEmpty(value)) {
            ops.expire(1, TimeUnit.DAYS);
            if(value.equals(NULL_VAL)) return new ArrayList<>();
            return GsonUtils.gson().fromJson(value, new TypeToken<List<String>>(){}.getType());
        }

        // 放入缓存
        var roleMenus = roleMenuMapper.wrapper().eq(RoleMenuEntity::getRoleId, roleId).list();
        if(roleMenus.isEmpty()) {
            ops.set(NULL_VAL, 1, TimeUnit.DAYS);
            return new ArrayList<>();
        }
        var menus = menuMapper.wrapper().in(MenuEntity::getId, roleMenus.stream().map(RoleMenuEntity::getMenuId).collect(Collectors.toList())).list();
        var perms = menus.stream().map(MenuEntity::getPermission).filter(permission -> !Strings.isNullOrEmpty(permission)).collect(Collectors.toList());

        ops.set(GsonUtils.gson().toJson(perms), 1, TimeUnit.DAYS);
        return perms;
    }

    public List<CurrentMenuResp> getCurrentMenu(UUID roleId) {
        var ops = redis.boundValueOps(RedisConstants.MenuKeys.getCurrentKey(roleId));
        var value = ops.get();
        if(!Strings.isNullOrEmpty(value)) {
            ops.expire(1, TimeUnit.DAYS);
            if(value.equals(NULL_VAL)) return new ArrayList<>();
            return GsonUtils.gson().fromJson(value, new TypeToken<List<CurrentMenuResp>>(){}.getType());
        }

        // 放入缓存
        var roleMenus = roleMenuMapper.wrapper().eq(RoleMenuEntity::getRoleId, roleId).list();
        if(roleMenus.isEmpty()) {
            ops.set(NULL_VAL, 1, TimeUnit.DAYS);
            return new ArrayList<>();
        }
        var menus = menuMapper.wrapper().in(MenuEntity::getId, roleMenus.stream().map(RoleMenuEntity::getMenuId).collect(Collectors.toList())).list();
        var respList = menus.stream().filter(menu -> menu.getMenuType() == MenuEntity.MenuType.Dir).sorted(Comparator.comparingInt(MenuEntity::getSortBy)).map(f -> {
            var resp = new CurrentMenuResp(f);
            var children = menus.stream().filter(m -> m.getMenuType() == MenuEntity.MenuType.Menu)
                    .filter(sub -> sub.getParentId().equals(f.getId()))
                    .filter(sub -> {
                        if(sub.getId().equals(GlobalConstants.MENU_ID)) {
                            return UserThreadInfo.get().isSuperAdmin();
                        }
                        return true;
                    })
                    .sorted(Comparator.comparingInt(MenuEntity::getSortBy))
                    .map(CurrentMenuResp::new)
                    .collect(Collectors.toList());
            if(!children.isEmpty()) {
                resp.setChildren(children);
            }
            return resp;
        }).collect(Collectors.toList());

        // 缓存
        ops.set(GsonUtils.gson().toJson(respList), 1, TimeUnit.DAYS);
        return respList;
    }

    public void updatePwd(UUID id, UpdatePwdReq req) {
        var account = adminMapper.findById(id);
        if(account == null) throw new BusinessException("当前账号发生错误, 请重新登录");

        if(!PasswordUtils.verify(req.getOldPassword().trim(), account.getPassword())) {
            throw new BusinessException("当前密码不正确");
        }

        var update = new AccountEntity();
        update.setId(id);
        update.setPassword(PasswordUtils.generate(req.getNewPassword().trim()));
        adminMapper.updateSelectiveById(update);
    }

    private void setIpLimit(String ip, String userName, String password) {
        var finalUserName = userName.length() > 10 ? userName.substring(0, 10) : userName;
        var finalPassword = password.length() > 20 ? password.substring(0, 20) : password;

        lockService.lock(RedisConstants.LockKeys.ipLimitLockKey(ip), 5, 10, () -> {
            var ops = redis.boundValueOps(RedisConstants.LoginKeys.ipLimitKey(ip));
            var limitStr = ops.get();
            IPLimit ipLimit = null;
            if(!Strings.isNullOrEmpty(limitStr)) {
                ipLimit = GsonUtils.gson().fromJson(limitStr, IPLimit.class);
            }
            var accountInfo = IPLimitEntity.IPLimitAccount.builder().account(finalUserName).password(finalPassword).build();
            if(ipLimit == null) {
                ipLimit = new IPLimit();
                ipLimit.setCount(1);
                ipLimit.setLastTime(new Date().getTime());
                var account = new ArrayList<IPLimitEntity.IPLimitAccount>();

                account.add(accountInfo);
                ipLimit.setAccounts(account);
            } else {
                ipLimit.setCount(ipLimit.getCount() + 1);
                ipLimit.getAccounts().add(accountInfo);
            }
            if(ipLimit.getCount() >= 5) {
                // 保存起来
                SpringUtils.getBean(LoginService.class).saveIPLimitRecord(ip, ipLimit.getAccounts());
                ipLimit.setAccounts(new ArrayList<>());
            }
            ops.set(GsonUtils.gson().toJson(ipLimit), 1, TimeUnit.HOURS);
        });
    }

    private void ipLimitCheck(String ip) {
        var ops = redis.boundValueOps(RedisConstants.LoginKeys.ipLimitKey(ip));
        var limitStr = ops.get();
        if(!Strings.isNullOrEmpty(limitStr)) {
            var ipLimit = GsonUtils.gson().fromJson(limitStr, IPLimit.class);
            var btw = Duration.between(LocalDateTime.now(),LocalDateTimeUtils.toLocalDateTime(new Date(ipLimit.getLastTime())).plusMinutes(30));
            var seconds = btw.toSeconds();
            if(ipLimit.getCount() >= 5 && btw.toSeconds() > 0){
                var minutes = btw.toMinutes();
                throw new BusinessException("账号密码错误次数过多, 请等待" + (minutes > 0 ?( minutes + "分钟") : (seconds + "秒")) + "后再试");
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void saveIPLimitRecord(String ip, List<IPLimitEntity.IPLimitAccount> accounts) {
        var limit = new IPLimitEntity();
        limit.setId(Generators.timeBasedGenerator().generate());
        limit.setIpAddr(ip);
        limit.setRequestAccount(GsonUtils.gson().toJson(accounts));
        limit.setCreateTime(LocalDateTime.now());
        ipLimitMapper.insert(limit);
    }

    @Data
    public static class IPLimit {
        private int count;
        private long lastTime;
        private List<IPLimitEntity.IPLimitAccount> accounts;
    }

}
