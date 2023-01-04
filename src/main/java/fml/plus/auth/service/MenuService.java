package fml.plus.auth.service;

import com.fasterxml.uuid.Generators;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import fml.plus.auth.common.constants.RedisConstants;
import fml.plus.auth.common.exception.BusinessException;
import fml.plus.auth.common.transaction.IAfterCommitExecutor;
import fml.plus.auth.common.util.GsonUtils;
import fml.plus.auth.dto.req.MenuReq;
import fml.plus.auth.dto.resp.MenuTreeResp;
import fml.plus.auth.entity.MenuEntity;
import fml.plus.auth.entity.RoleMenuEntity;
import fml.plus.auth.mapper.MenuMapper;
import fml.plus.auth.mapper.RoleMenuMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(rollbackFor = Exception.class)
public class MenuService {
    private IAfterCommitExecutor afterCommitExecutor;
    private StringRedisTemplate redis;
    private MenuMapper menuMapper;
    private RoleMenuMapper roleMenuMapper;

    public List<MenuTreeResp> list(){
        var ops = redis.boundValueOps(RedisConstants.MenuKeys.getAllMenus());
        var val = ops.get();
        if(!Strings.isNullOrEmpty(val)) {
            ops.expire(1, TimeUnit.DAYS);
            return GsonUtils.gson().fromJson(val, new TypeToken<List<MenuTreeResp>>(){}.getType());
        }

        // 放入缓存
        var menus = menuMapper.wrapper().list();
        var tree = convertToTree(menus, null);
        ops.set(GsonUtils.gson().toJson(tree), 1, TimeUnit.DAYS);
        return tree;
    }

    private List<MenuTreeResp> convertToTree(List<MenuEntity> menus, UUID parentId) {
        return menus.stream().filter(menu -> Objects.equals(menu.getParentId(), parentId)).map(menu -> {
            var menuResp = new MenuTreeResp(menu);
            menuResp.setChildren(convertToTree(menus, menu.getId()));
            if(menuResp.getChildren().isEmpty()) menuResp.setChildren(null);
            return menuResp;
        }).sorted(Comparator.comparingInt(MenuTreeResp::getSortBy)).collect(Collectors.toList());
    }

    public void save(MenuReq menu) {
        var parentId = menu.getParentId();
        // 校验父级是否存在
        if(parentId != null && menuMapper.findById(parentId) == null) {
            throw new BusinessException("parentId不正确, 无此项菜单");
        }

        var model = new MenuEntity();
        model.setIcon(menu.getIcon());
        model.setId(menu.getId());
        model.setMenuName(menu.getTitle());
        model.setMenuType(MenuEntity.MenuType.of(menu.getMenuType()));
        model.setParentId(parentId);
        model.setPermission(menu.getResourceCode());
        model.setSortBy(menu.getSortBy());
        model.setMenuPath(menu.getPath());
        if(model.getId() == null){
            model.setId(Generators.timeBasedGenerator().generate());
            model.setCreateTime(LocalDateTime.now());
            menuMapper.insert(model);
        } else {
            menuMapper.updateSelectiveById(model);
        }

        // 删除相关的缓存
        afterCommitExecutor.run(() -> {
            var deleteKeys = new ArrayList<String>();
            deleteKeys.add(RedisConstants.MenuKeys.getAllMenus());
            var joinRoles = roleMenuMapper.wrapper().eq(RoleMenuEntity::getMenuId, model.getId()).list();
            for(var roleMenu : joinRoles) {
                deleteKeys.add(RedisConstants.MenuKeys.getCurrentKey(roleMenu.getRoleId()));
                deleteKeys.add(RedisConstants.MenuKeys.getCurrentPermissionKey(roleMenu.getRoleId()));
            }
            redis.delete(deleteKeys);
        });
    }

    public void remove(List<UUID> ids){
        var keys = new HashSet<String>();
        Queue<UUID> queue = Lists.newLinkedList(ids);
        while(!queue.isEmpty()) {
            UUID menuId = queue.remove();
            var list = menuMapper.findByParentId(menuId);
            queue.addAll(list.stream().map(MenuEntity::getId).filter(Objects::nonNull).toList());
            menuMapper.deleteById(menuId);

            var joinRoles = roleMenuMapper.wrapper().eq(RoleMenuEntity::getMenuId, menuId).list();
            for(var roleMenu : joinRoles) {
                keys.add(RedisConstants.MenuKeys.getCurrentKey(roleMenu.getRoleId()));
                keys.add(RedisConstants.MenuKeys.getCurrentPermissionKey(roleMenu.getRoleId()));
            }

            // 删除关联
            roleMenuMapper.wrapper().eq(RoleMenuEntity::getMenuId, menuId).delete();
        }

        keys.add(RedisConstants.MenuKeys.getAllMenus());
        afterCommitExecutor.run(() -> redis.delete(keys));
    }
}
