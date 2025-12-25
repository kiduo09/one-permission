package com.zhangyu.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.AppRoleCreateDTO;
import com.zhangyu.permission.dto.AppRoleQueryDTO;
import com.zhangyu.permission.dto.AppRoleUpdateDTO;
import com.zhangyu.permission.entity.AppRole;
import com.zhangyu.permission.entity.AppRoleMenu;
import com.zhangyu.permission.entity.AppMenu;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.mapper.AppRoleMapper;
import com.zhangyu.permission.mapper.AppRoleMenuMapper;
import com.zhangyu.permission.mapper.AppMenuMapper;
import com.zhangyu.permission.mapper.ApplicationMapper;
import com.zhangyu.permission.service.AppRoleService;
import com.zhangyu.permission.vo.AppRoleListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 应用角色服务实现类
 * 
 * @author zhangyu
 */
@Slf4j
@Service
public class AppRoleServiceImpl extends ServiceImpl<AppRoleMapper, AppRole> implements AppRoleService {
    
    @Autowired
    private ApplicationMapper applicationMapper;
    
    @Autowired
    private AppRoleMenuMapper appRoleMenuMapper;
    
    @Autowired
    private AppMenuMapper appMenuMapper;
    
    @Override
    public PageResult<AppRoleListVO> pageQuery(Long appId, AppRoleQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<AppRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppRole::getAppId, appId);
        
        // 系统ID模糊查询
        String systemId = queryDTO.getSystemId();
        if (StringUtils.hasText(systemId) && !"undefined".equals(systemId)) {
            wrapper.like(AppRole::getSystemId, systemId.trim());
        }
        
        // 角色名称模糊查询
        String name = queryDTO.getName();
        if (StringUtils.hasText(name) && !"undefined".equals(name)) {
            wrapper.like(AppRole::getName, name.trim());
        }
        
        // 状态精确查询
        String status = queryDTO.getStatus();
        if (StringUtils.hasText(status) && !"undefined".equals(status)) {
            wrapper.eq(AppRole::getStatus, status.trim());
        }
        
        // 按排序和ID排序
        wrapper.orderByAsc(AppRole::getSort);
        wrapper.orderByAsc(AppRole::getId);
        
        // 分页查询
        Page<AppRole> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        IPage<AppRole> pageResult = this.page(page, wrapper);
        
        // 转换为VO
        List<AppRoleListVO> voList = pageResult.getRecords().stream().map(role -> {
            AppRoleListVO vo = new AppRoleListVO();
            BeanUtil.copyProperties(role, vo);
            
            // 查询应用名称
            Application app = applicationMapper.selectById(appId);
            if (app != null) {
                vo.setAppName(app.getName());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(voList, pageResult.getTotal(), queryDTO.getPage(), queryDTO.getPageSize());
    }
    
    @Override
    public AppRole getById(Long appId, Long id) {
        AppRole role = super.getById(id);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        return role;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppRole create(Long appId, AppRoleCreateDTO createDTO) {
        // 检查应用是否存在
        Application app = applicationMapper.selectById(appId);
        if (app == null) {
            throw new BusinessException("应用不存在");
        }
        
        // 检查同一应用下角色名称是否已存在
        LambdaQueryWrapper<AppRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppRole::getAppId, appId);
        wrapper.eq(AppRole::getName, createDTO.getName());
        AppRole existRole = this.getOne(wrapper);
        if (existRole != null) {
            throw new BusinessException("该应用下已存在同名角色");
        }
        
        // 创建角色
        AppRole role = new AppRole();
        BeanUtil.copyProperties(createDTO, role);
        role.setAppId(appId);
        this.save(role);
        
        // 分配菜单权限
        if (createDTO.getMenuIds() != null && !createDTO.getMenuIds().isEmpty()) {
            assignMenus(appId, role.getId(), createDTO.getMenuIds());
        }
        
        return role;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppRole update(Long appId, Long id, AppRoleUpdateDTO updateDTO) {
        // 查询角色
        AppRole role = super.getById(id);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        // 检查同一应用下角色名称是否被其他角色使用
        LambdaQueryWrapper<AppRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppRole::getAppId, appId);
        wrapper.eq(AppRole::getName, updateDTO.getName());
        AppRole existRole = this.getOne(wrapper);
        if (existRole != null && !existRole.getId().equals(id)) {
            throw new BusinessException("该应用下已存在同名角色");
        }
        
        // 更新角色
        BeanUtil.copyProperties(updateDTO, role);
        this.updateById(role);
        
        // 更新菜单权限
        if (updateDTO.getMenuIds() != null) {
            assignMenus(appId, role.getId(), updateDTO.getMenuIds());
        }
        
        return role;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long appId, Long id) {
        // 查询角色
        AppRole role = super.getById(id);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        // TODO: 检查是否有用户关联、部门关联、菜单权限关联
        // 如果有，需要先删除关联关系或提示用户
        
        // 删除角色菜单关联
        LambdaQueryWrapper<AppRoleMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(AppRoleMenu::getRoleId, id);
        appRoleMenuMapper.delete(menuWrapper);
        
        // 删除角色
        this.removeById(id);
    }
    
    /**
     * 分配菜单权限给角色
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表（前端已确保包含所有父菜单ID）
     */
    private void assignMenus(Long appId, Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            // 如果没有菜单ID，只删除原有关联
            LambdaQueryWrapper<AppRoleMenu> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(AppRoleMenu::getRoleId, roleId);
            appRoleMenuMapper.delete(deleteWrapper);
            return;
        }
        
        // 去重菜单ID
        List<Long> uniqueMenuIds = menuIds.stream().distinct().collect(Collectors.toList());
        
        // 验证菜单是否属于当前应用
        LambdaQueryWrapper<AppMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(AppMenu::getAppId, appId);
        menuWrapper.in(AppMenu::getId, uniqueMenuIds);
        List<AppMenu> menus = appMenuMapper.selectList(menuWrapper);
        
        if (menus.size() != uniqueMenuIds.size()) {
            // 找出不存在的菜单ID
            Set<Long> foundMenuIds = menus.stream()
                    .map(AppMenu::getId)
                    .collect(Collectors.toSet());
            List<Long> notFoundMenuIds = uniqueMenuIds.stream()
                    .filter(id -> !foundMenuIds.contains(id))
                    .collect(Collectors.toList());
            throw new BusinessException("部分菜单不存在或不属于当前应用，菜单ID: " + notFoundMenuIds);
        }
        
        // 删除原有菜单关联
        LambdaQueryWrapper<AppRoleMenu> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(AppRoleMenu::getRoleId, roleId);
        appRoleMenuMapper.delete(deleteWrapper);
        
        // 批量插入新的菜单关联（确保所有菜单ID都被保存）
        List<AppRoleMenu> roleMenus = new ArrayList<>();
        for (Long menuId : uniqueMenuIds) {
            AppRoleMenu roleMenu = new AppRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenus.add(roleMenu);
        }
        
        // 批量插入
        for (AppRoleMenu roleMenu : roleMenus) {
            appRoleMenuMapper.insert(roleMenu);
        }
        
        log.info("角色菜单权限分配完成，角色ID: {}, 菜单数量: {}, 菜单ID列表: {}", 
                roleId, uniqueMenuIds.size(), uniqueMenuIds);
    }
    
    /**
     * 查询角色的菜单权限
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    public List<Long> getRoleMenuIds(Long appId, Long roleId) {
        // 验证角色是否属于当前应用
        AppRole role = super.getById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        // 查询角色菜单关联
        LambdaQueryWrapper<AppRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppRoleMenu::getRoleId, roleId);
        List<AppRoleMenu> roleMenus = appRoleMenuMapper.selectList(wrapper);
        
        return roleMenus.stream()
                .map(AppRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }
}

