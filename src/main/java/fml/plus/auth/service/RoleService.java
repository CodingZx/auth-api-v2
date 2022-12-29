package fml.plus.auth.service;

import com.fasterxml.uuid.Generators;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fml.plus.auth.common.constants.GlobalConstants;
import fml.plus.auth.common.constants.RedisConstants;
import fml.plus.auth.common.context.UserThreadInfo;
import fml.plus.auth.common.exception.BusinessException;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.Pager;
import fml.plus.auth.common.model.Selector;
import fml.plus.auth.common.transaction.IAfterCommitExecutor;
import fml.plus.auth.common.util.SQLUtils;
import fml.plus.auth.dto.req.RoleReq;
import fml.plus.auth.dto.resp.RoleResp;
import fml.plus.auth.entity.AccountEntity;
import fml.plus.auth.entity.RoleEntity;
import fml.plus.auth.entity.RoleMenuEntity;
import fml.plus.auth.mapper.AdminMapper;
import fml.plus.auth.mapper.MenuMapper;
import fml.plus.auth.mapper.RoleMapper;
import fml.plus.auth.mapper.RoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService {
    @Autowired
    private IAfterCommitExecutor afterCommit;
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private AdminMapper adminMapper;

    public List<Selector> all() {
        return roleMapper.wrapper().orderByDesc(RoleEntity::getCreateTime).list()
                .stream()
                .filter(f -> {
                    if(!UserThreadInfo.get().isSuperAdmin()) {
                        // 非超级管理员. 不允许分配超级管理员权限
                        return !f.getId().equals(GlobalConstants.DEFAULT_ID);
                    }
                    return true;
                })
                .map(r -> new Selector(r.getId(), r.getRoleName()))
                .collect(Collectors.toList());
    }

    public List<UUID> findMenuIdsById(UUID roleId) {
        // 校验
        var role = roleMapper.findById(roleId);
        if(role == null) throw new BusinessException("角色ID不正确");

        var menus = roleMenuMapper.wrapper().eq(RoleMenuEntity::getRoleId, roleId).list();
        return menus.stream().map(RoleMenuEntity::getMenuId).collect(Collectors.toList());
    }

    public Pager<RoleResp> list(Page page, String roleName) {
        var superAdmin = UserThreadInfo.get().isSuperAdmin();

        var query = roleMapper.wrapper().page(page.toRow());
        if(!Strings.isNullOrEmpty(roleName)) query.like(RoleEntity::getRoleName, SQLUtils.fuzzyAll(roleName));
        if(!superAdmin) query.ne(RoleEntity::getId, GlobalConstants.DEFAULT_ID);
        query.orderByDesc(RoleEntity::getCreateTime);

        var list = query.list();
        var count = query.count();
        return Pager.of(list.stream().map(RoleResp::new).collect(Collectors.toList()), count);
    }

    public void save(RoleReq req) {
        RoleEntity role = new RoleEntity();
        role.setId(req.getId());
        role.setRoleName(req.getRoleName());

        if (role.getId() == null) {
            role.setId(Generators.timeBasedGenerator().generate());
            role.setCreateTime(LocalDateTime.now());
            roleMapper.insert(role);
        } else {
            roleMapper.updateSelectiveById(role);
            // 删除之前的关联
            roleMenuMapper.wrapper().eq(RoleMenuEntity::getRoleId, req.getId()).delete();
        }

        // 添加关联
        var menus = new HashSet<UUID>();
        for(var menuId : req.getMenus()) {
            var menu = menuMapper.findById(menuId);
            if(menu == null) throw new BusinessException("菜单ID"+menuId+"不存在");
            // 校验
            switch (menu.getMenuType()) {
                case Dir -> menus.add(menu.getId());
                case Menu -> {
                    // 如果上级Dir没加进去, 则自动添加
                    var dirParent = Optional.ofNullable(menuMapper.findById(menu.getParentId())).orElseThrow(() -> new BusinessException("菜单ID"+menu.getParentId()+"不存在"));
                    menus.add(dirParent.getId());

                    menus.add(menu.getId());
                }
                case Button -> {
                    // 校验上级Menu 如果不存在, 则自动添加
                    var menuParent = Optional.ofNullable(menuMapper.findById(menu.getParentId())).orElseThrow(() -> new BusinessException("菜单ID"+menu.getParentId()+"不存在"));
                    menus.add(menuParent.getId());

                    // 再去校验上级Dir
                    var dirParent = Optional.ofNullable(menuMapper.findById(menuParent.getParentId())).orElseThrow(() -> new BusinessException("菜单ID"+menuParent.getParentId()+"不存在"));
                    menus.add(dirParent.getId());

                    menus.add(menu.getId());
                }
            }
        }

        for(var menuId : menus) {
            var roleMenu = new RoleMenuEntity();
            roleMenu.setId(Generators.timeBasedGenerator().generate());
            roleMenu.setRoleId(role.getId());
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }

        // 删除相关缓存
        afterCommit.run(() -> redis.delete(List.of(
                RedisConstants.MenuKeys.getCurrentKey(req.getId()),
                RedisConstants.MenuKeys.getCurrentPermissionKey(req.getId()),
                RedisConstants.RoleKeys.roleNameKey(req.getId())
        )));
    }

    public void remove(List<UUID> ids) {
        List<String> resultList = Lists.newLinkedList();
        for (UUID id : ids) {
            var role = roleMapper.findById(id);
            if(role == null) {
                continue;
            }
            var count = adminMapper.wrapper().eq(AccountEntity::getRoleId, id).count();
            if (count > 0) { // 角色正在被使用
                resultList.add(role.getRoleName());
            } else {
                roleMapper.deleteById(id);

                roleMenuMapper.wrapper().eq(RoleMenuEntity::getRoleId, id).delete();

                afterCommit.run(() -> redis.delete(Arrays.asList(
                        RedisConstants.MenuKeys.getCurrentKey(id),
                        RedisConstants.MenuKeys.getCurrentPermissionKey(id),
                        RedisConstants.RoleKeys.roleNameKey(id)
                )));
            }
        }
        if (!resultList.isEmpty()) {
            throw new BusinessException(Joiner.on(",").join(resultList) + " 已被使用!");
        }
    }

    /**
     * 获得角色名称
     */
    public String getRoleName(UUID roleId) {
        var ops = redis.boundValueOps(RedisConstants.RoleKeys.roleNameKey(roleId));
        var val = ops.get();
        if(Strings.isNullOrEmpty(val)) {
            var role = roleMapper.findById(roleId);
            if(role == null) {
                ops.set(RedisConstants.NULL_VAL, 1, TimeUnit.DAYS);
                return "<未知角色>";
            }
            ops.set(role.getRoleName(), 1, TimeUnit.DAYS);
            return role.getRoleName();
        }
        ops.expire(1, TimeUnit.DAYS);
        if(val.equals(RedisConstants.NULL_VAL)) {
            return "<未知角色>";
        }
        return val;
    }
}
