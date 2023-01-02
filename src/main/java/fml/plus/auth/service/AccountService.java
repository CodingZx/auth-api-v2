package fml.plus.auth.service;

import com.fasterxml.uuid.Generators;
import com.google.common.base.Strings;
import fml.plus.auth.common.constants.GlobalConstants;
import fml.plus.auth.common.constants.RedisConstants;
import fml.plus.auth.common.context.UserThreadInfo;
import fml.plus.auth.common.exception.BusinessException;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.Pager;
import fml.plus.auth.common.transaction.IAfterCommitExecutor;
import fml.plus.auth.common.util.PasswordUtils;
import fml.plus.auth.common.util.SQLUtils;
import fml.plus.auth.dto.req.AdminReq;
import fml.plus.auth.dto.req.ResetPwdReq;
import fml.plus.auth.dto.resp.AdminResp;
import fml.plus.auth.entity.AccountEntity;
import fml.plus.auth.mapper.AdminMapper;
import fml.plus.auth.mapper.RoleMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(rollbackFor = Exception.class)
public class AccountService {
    private StringRedisTemplate redis;
    private IAfterCommitExecutor afterCommitExecutor;
    private RoleMapper roleMapper;
    private AdminMapper adminMapper;

    public Pager<AdminResp> list(Page page, String userName, String realName){
        var superAdmin = UserThreadInfo.get().isSuperAdmin();

        var query = adminMapper.wrapper().page(page.toRow()).orderByDesc(AccountEntity::getCreateTime);
        if(!Strings.isNullOrEmpty(userName)) query.like(AccountEntity::getUserName, SQLUtils.fuzzyAll(userName));
        if(!Strings.isNullOrEmpty(realName)) query.like(AccountEntity::getRealName, SQLUtils.fuzzyAll(realName));
        if(!superAdmin) query.ne(AccountEntity::getId, GlobalConstants.DEFAULT_ID);

        var list = query.list();
        var count = query.count();

        if(list.isEmpty()) {
            return Pager.ofEmpty(count);
        }

        return Pager.of(list.stream().map(AdminResp::new).collect(Collectors.toList()), count);
    }

    public void save(AdminReq admin){
        var check = adminMapper.findByUserName(admin.getUserName().trim());
        if(admin.getId() == null && check != null){
            throw new BusinessException("用户名重复");
        }
        if(admin.getId() != null && check != null && !check.getId().equals(admin.getId())){
            throw new BusinessException("用户名重复");
        }

        // 校验角色
        var role = roleMapper.findById(admin.getRoleId());
        if(role == null) throw new BusinessException("角色不存在");

        var entity = new AccountEntity();
        entity.setId(admin.getId());
        entity.setUserName(admin.getUserName().trim());
        if(!Strings.isNullOrEmpty(admin.getPassword().trim())){
            entity.setPassword(PasswordUtils.generate(admin.getPassword().trim()));
        } else {
            entity.setPassword(null);
        }
        entity.setRealName(admin.getRealName().trim());
        entity.setRoleId(role.getId());

        if(entity.getId() == null) {
            entity.setId(Generators.timeBasedGenerator().generate());
            entity.setCreateTime(LocalDateTime.now());
            entity.setStatus(true);
            adminMapper.insert(entity);
        } else {
            // 不允许修改超级管理员角色
            if(entity.getId().equals(GlobalConstants.DEFAULT_ID) && !entity.getRoleId().equals(GlobalConstants.DEFAULT_ID)) {
                throw new BusinessException("无法修改超级管理员的角色");
            }
            adminMapper.updateSelectiveById(entity);
        }

        afterCommitExecutor.run(() -> redis.delete(RedisConstants.AdminKeys.adminRealNameKey(entity.getId())));
    }

    public void resetPwd(ResetPwdReq req) {
        var entity = new AccountEntity();
        entity.setId(req.getAdminId());
        entity.setPassword(PasswordUtils.generate(req.getPassword().trim()));
        adminMapper.updateSelectiveById(entity);
    }

    public void remove(List<UUID> ids){
        if(ids == null || ids.isEmpty()) return;

        for(UUID id : ids) {
            adminMapper.deleteById(id);
        }
        afterCommitExecutor.run(() -> redis.delete(ids.stream().map(RedisConstants.AdminKeys::adminRealNameKey).toList()));
    }

    public void updateStatus(UUID id, boolean status) {
        var update = new AccountEntity();
        update.setId(id);
        update.setStatus(status);

        adminMapper.updateSelectiveById(update);

        if(!status) {
            afterCommitExecutor.run(() -> {
                var key1 = RedisConstants.AdminKeys.adminRealNameKey(id);
                var key2 = RedisConstants.getTokenKey(id);
                redis.delete(List.of(key1, key2));
            });
        }
    }

    /**
     * 获得账号名称
     */
    public String getRealName(UUID id) {
        if(id == null) return "";
        var ops = redis.boundValueOps(RedisConstants.AdminKeys.adminRealNameKey(id));
        var value = ops.get();
        if(!Strings.isNullOrEmpty(value)) {
            ops.expire(1, TimeUnit.DAYS);
            return value;
        }
        value = Optional.ofNullable(adminMapper.findById(id)).map(AccountEntity::getRealName).orElse("");
        ops.set(value, 1, TimeUnit.DAYS);
        return value;
    }
}
