package com.zhangyu.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.AppMenuCreateDTO;
import com.zhangyu.permission.dto.AppMenuQueryDTO;
import com.zhangyu.permission.dto.AppMenuUpdateDTO;
import com.zhangyu.permission.entity.AppMenu;
import com.zhangyu.permission.mapper.AppMenuMapper;
import com.zhangyu.permission.service.AppMenuService;
import com.zhangyu.permission.vo.AppMenuListVO;
import com.zhangyu.permission.vo.AppMenuTreeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应用菜单服务实现类
 * 
 * @author zhangyu
 */
@Service
public class AppMenuServiceImpl extends ServiceImpl<AppMenuMapper, AppMenu> implements AppMenuService {
    
    @Override
    public List<AppMenuTreeVO> getMenuTree(Long appId, String status) {
        // 构建查询条件
        LambdaQueryWrapper<AppMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppMenu::getAppId, appId);
        
        // 状态过滤
        if (StringUtils.hasText(status) && !"undefined".equals(status)) {
            wrapper.eq(AppMenu::getStatus, status.trim());
        }
        
        // 按排序和ID排序
        wrapper.orderByAsc(AppMenu::getSort);
        wrapper.orderByAsc(AppMenu::getId);
        
        // 查询所有菜单
        List<AppMenu> allMenus = this.list(wrapper);
        
        // 如果查询结果为空，返回空列表
        if (allMenus == null || allMenus.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为VO
        List<AppMenuTreeVO> menuVOs = allMenus.stream()
                .map(menu -> {
                    AppMenuTreeVO vo = new AppMenuTreeVO();
                    BeanUtil.copyProperties(menu, vo);
                    vo.setChildren(new ArrayList<>());
                    return vo;
                })
                .collect(Collectors.toList());
        
        // 构建树形结构
        Map<Long, AppMenuTreeVO> menuMap = menuVOs.stream()
                .collect(Collectors.toMap(AppMenuTreeVO::getId, vo -> vo));
        
        List<AppMenuTreeVO> rootMenus = new ArrayList<>();
        for (AppMenuTreeVO menu : menuVOs) {
            if (menu.getParentId() == null) {
                rootMenus.add(menu);
            } else {
                AppMenuTreeVO parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                } else {
                    // 如果父菜单不存在，作为根菜单处理（防止数据不一致）
                    rootMenus.add(menu);
                }
            }
        }
        
        return rootMenus;
    }
    
    @Override
    public PageResult<AppMenuListVO> pageQuery(Long appId, AppMenuQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<AppMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppMenu::getAppId, appId);
        
        // 菜单名称模糊查询
        String name = queryDTO.getName();
        if (StringUtils.hasText(name) && !"undefined".equals(name)) {
            wrapper.like(AppMenu::getName, name.trim());
        }
        
        // 菜单Key模糊查询
        String menuKey = queryDTO.getMenuKey();
        if (StringUtils.hasText(menuKey) && !"undefined".equals(menuKey)) {
            wrapper.like(AppMenu::getMenuKey, menuKey.trim());
        }
        
        // 菜单类型精确查询
        String menuType = queryDTO.getMenuType();
        if (StringUtils.hasText(menuType) && !"undefined".equals(menuType)) {
            wrapper.eq(AppMenu::getMenuType, menuType.trim());
        }
        
        // 状态精确查询
        String status = queryDTO.getStatus();
        if (StringUtils.hasText(status) && !"undefined".equals(status)) {
            wrapper.eq(AppMenu::getStatus, status.trim());
        }
        
        // 按排序和创建时间倒序
        wrapper.orderByAsc(AppMenu::getSort);
        wrapper.orderByDesc(AppMenu::getCreateTime);
        
        // 分页查询
        Page<AppMenu> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        IPage<AppMenu> pageResult = this.page(page, wrapper);
        
        // 转换为VO列表
        List<AppMenuListVO> voList = pageResult.getRecords().stream()
                .map(menu -> {
                    AppMenuListVO vo = new AppMenuListVO();
                    BeanUtil.copyProperties(menu, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        return PageResult.of(voList, pageResult.getTotal(), queryDTO.getPage(), queryDTO.getPageSize());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppMenu create(Long appId, AppMenuCreateDTO createDTO) {
        // 检查应用是否存在（这里简化处理，实际应该调用ApplicationService）
        
        // 如果指定了父菜单，验证父菜单是否存在
        if (createDTO.getParentId() != null) {
            AppMenu parentMenu = this.getById(createDTO.getParentId());
            if (parentMenu == null || !parentMenu.getAppId().equals(appId)) {
                throw new BusinessException("父菜单不存在或不属于当前应用");
            }
        }
        
        // 自动生成菜单Key（格式：menu_key + 32位UUID）
        String menuKey = generateMenuKey(appId);
        
        // 创建菜单实体
        AppMenu menu = new AppMenu();
        BeanUtil.copyProperties(createDTO, menu);
        menu.setAppId(appId);
        menu.setMenuKey(menuKey); // 设置自动生成的menuKey
        
        // 保存
        this.save(menu);
        
        return menu;
    }
    
    /**
     * 生成菜单Key
     * 格式：menu_key + 32位UUID（去掉横线）
     * 示例：menu_key550e8400e29b41d4a716446655440000
     * 
     * @param appId 应用ID（用于检查唯一性）
     * @return 菜单Key
     */
    private String generateMenuKey(Long appId) {
        String menuKey;
        int maxRetries = 10; // 最多重试10次
        int retryCount = 0;
        
        do {
            // 生成32位UUID（去掉横线）
            String uuid = UUID.randomUUID().toString().replace("-", "");
            menuKey = "menu_key" + uuid;
            
            // 检查是否已存在
            LambdaQueryWrapper<AppMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AppMenu::getAppId, appId);
            wrapper.eq(AppMenu::getMenuKey, menuKey);
            AppMenu existMenu = this.getOne(wrapper);
            
            if (existMenu == null) {
                // 不存在，可以使用
                return menuKey;
            }
            
            retryCount++;
        } while (retryCount < maxRetries);
        
        // 如果重试10次还是重复，抛出异常
        throw new BusinessException("生成菜单Key失败，请重试");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppMenu update(Long appId, Long id, AppMenuUpdateDTO updateDTO) {
        // 查询菜单
        AppMenu menu = this.getById(id);
        if (menu == null || !menu.getAppId().equals(appId)) {
            throw new BusinessException("菜单不存在或不属于当前应用");
        }
        
        // 如果指定了父菜单，验证父菜单是否存在且不能是自己
        if (updateDTO.getParentId() != null) {
            if (updateDTO.getParentId().equals(id)) {
                throw new BusinessException("不能将自己设置为父菜单");
            }
            AppMenu parentMenu = this.getById(updateDTO.getParentId());
            if (parentMenu == null || !parentMenu.getAppId().equals(appId)) {
                throw new BusinessException("父菜单不存在或不属于当前应用");
            }
            // 检查是否会形成循环引用
            checkCircularReference(id, updateDTO.getParentId());
        }
        
        // 更新字段（不更新menuKey，因为它是自动生成的，不允许修改）
        BeanUtil.copyProperties(updateDTO, menu, "menuKey");
        
        // 保存
        this.updateById(menu);
        
        return menu;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long appId, Long id) {
        // 检查菜单是否存在
        AppMenu menu = this.getById(id);
        if (menu == null || !menu.getAppId().equals(appId)) {
            throw new BusinessException("菜单不存在或不属于当前应用");
        }
        
        // 检查是否有子菜单
        LambdaQueryWrapper<AppMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppMenu::getAppId, appId);
        wrapper.eq(AppMenu::getParentId, id);
        long childCount = this.count(wrapper);
        if (childCount > 0) {
            throw new BusinessException("该菜单下存在子菜单，无法删除");
        }
        
        // TODO: 检查是否有关联的角色权限，如果有则不允许删除或提示
        
        // 删除菜单
        this.removeById(id);
    }
    
    /**
     * 检查是否会形成循环引用
     */
    private void checkCircularReference(Long menuId, Long parentId) {
        Long currentParentId = parentId;
        int depth = 0;
        while (currentParentId != null && depth < 100) {
            if (currentParentId.equals(menuId)) {
                throw new BusinessException("不能将菜单设置为其子菜单的父菜单，会形成循环引用");
            }
            AppMenu parent = this.getById(currentParentId);
            if (parent == null) {
                break;
            }
            currentParentId = parent.getParentId();
            depth++;
        }
    }
}

