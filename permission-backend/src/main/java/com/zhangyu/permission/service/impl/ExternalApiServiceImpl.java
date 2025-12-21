package com.zhangyu.permission.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.MenuPermissionQueryDTO;
import com.zhangyu.permission.dto.TokenRequestDTO;
import com.zhangyu.permission.entity.*;
import com.zhangyu.permission.mapper.*;
import com.zhangyu.permission.service.ConsumerInfoService;
import com.zhangyu.permission.service.ExternalApiService;
import com.zhangyu.permission.vo.MenuPermissionVO;
import com.zhangyu.permission.vo.TokenResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 对外接口服务实现类
 * 
 * @author zhangyu
 */
@Slf4j
@Service
public class ExternalApiServiceImpl implements ExternalApiService {
    
    @Autowired
    private ConsumerInfoService consumerInfoService;
    
    @Autowired
    private AppRoleUserMapper appRoleUserMapper;
    
    @Autowired
    private AppRoleMapper appRoleMapper;
    
    @Autowired
    private AppRoleMenuMapper appRoleMenuMapper;
    
    @Autowired
    private AppMenuMapper appMenuMapper;
    
    @Autowired
    private ApplicationMapper applicationMapper;
    
    @Value("${sa-token.timeout:7200}")
    private Long tokenTimeout;
    
    @Override
    public TokenResponseVO getToken(TokenRequestDTO tokenRequest) {
        // 1. 验证clientId和clientSecret
        ConsumerInfo consumer = consumerInfoService.getByClientId(tokenRequest.getClientId());
        if (consumer == null) {
            throw new BusinessException("clientId不存在或已禁用");
        }
        
        if (!consumer.getClientSecret().equals(tokenRequest.getClientSecret())) {
            throw new BusinessException("clientSecret错误");
        }
        
        // 2. 使用consumerId作为登录标识，生成token
        // 注意：这里使用consumerId作为token的loginId，与普通用户登录区分开
        // 使用前缀"consumer_"来区分外部API token和普通用户token
        String loginId = "consumer_" + consumer.getId();
        StpUtil.login(loginId);
        String token = StpUtil.getTokenValue();
        
        // 设置token的额外信息（可选）
        StpUtil.getSessionByLoginId(loginId).set("consumerId", consumer.getId());
        StpUtil.getSessionByLoginId(loginId).set("consumerName", consumer.getConsumerName());
        
        // 3. 构建响应
        TokenResponseVO response = new TokenResponseVO();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(tokenTimeout);
        response.setConsumerName(consumer.getConsumerName());
        
        log.info("消费者 {} 获取token成功", consumer.getConsumerName());
        return response;
    }
    
    @Override
    public List<MenuPermissionVO> getMenuPermissions(MenuPermissionQueryDTO queryDTO) {
        // 1. 验证应用是否存在
        Application application = applicationMapper.selectById(queryDTO.getAppId());
        if (application == null) {
            throw new BusinessException("应用不存在");
        }
        
        if (!"正常".equals(application.getStatus())) {
            throw new BusinessException("应用已禁用");
        }
        
        // 2. 查询用户在该应用下的所有角色
        LambdaQueryWrapper<AppRoleUser> roleUserWrapper = new LambdaQueryWrapper<>();
        roleUserWrapper.eq(AppRoleUser::getUserWorkNo, queryDTO.getWorkNo());
        
        List<AppRoleUser> roleUsers = appRoleUserMapper.selectList(roleUserWrapper);
        if (roleUsers.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 3. 获取所有角色ID
        Set<Long> roleIds = roleUsers.stream()
                .map(AppRoleUser::getRoleId)
                .collect(Collectors.toSet());
        
        // 4. 验证角色是否属于该应用，并过滤出有效的角色
        LambdaQueryWrapper<AppRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(AppRole::getId, roleIds);
        roleWrapper.eq(AppRole::getAppId, queryDTO.getAppId());
        roleWrapper.eq(AppRole::getStatus, "正常");
        
        List<AppRole> validRoles = appRoleMapper.selectList(roleWrapper);
        if (validRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<Long> validRoleIds = validRoles.stream()
                .map(AppRole::getId)
                .collect(Collectors.toSet());
        
        // 5. 查询这些角色关联的所有菜单ID
        LambdaQueryWrapper<AppRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.in(AppRoleMenu::getRoleId, validRoleIds);
        
        List<AppRoleMenu> roleMenus = appRoleMenuMapper.selectList(roleMenuWrapper);
        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<Long> menuIds = roleMenus.stream()
                .map(AppRoleMenu::getMenuId)
                .collect(Collectors.toSet());
        
        // 6. 查询菜单详情
        LambdaQueryWrapper<AppMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(AppMenu::getId, menuIds);
        menuWrapper.eq(AppMenu::getAppId, queryDTO.getAppId());
        menuWrapper.eq(AppMenu::getStatus, "正常");
        menuWrapper.eq(AppMenu::getDisplayStatus, "显示");
        menuWrapper.orderByAsc(AppMenu::getSort);
        
        List<AppMenu> menus = appMenuMapper.selectList(menuWrapper);
        
        // 7. 转换为VO并构建树形结构
        List<MenuPermissionVO> menuVOs = menus.stream()
                .map(menu -> {
                    MenuPermissionVO vo = new MenuPermissionVO();
                    BeanUtil.copyProperties(menu, vo);
                    vo.setIsExternal(menu.getIsExternal() == 1);
                    return vo;
                })
                .collect(Collectors.toList());
        
        // 8. 构建树形结构
        return buildMenuTree(menuVOs);
    }
    
    /**
     * 构建菜单树形结构
     */
    private List<MenuPermissionVO> buildMenuTree(List<MenuPermissionVO> menuList) {
        // 创建菜单映射
        Map<Long, MenuPermissionVO> menuMap = new HashMap<>();
        for (MenuPermissionVO menu : menuList) {
            menuMap.put(menu.getId(), menu);
        }
        
        // 构建树形结构
        List<MenuPermissionVO> rootMenus = new ArrayList<>();
        for (MenuPermissionVO menu : menuList) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                // 根菜单
                rootMenus.add(menu);
            } else {
                // 子菜单
                MenuPermissionVO parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(menu);
                }
            }
        }
        
        // 递归排序子菜单
        for (MenuPermissionVO menu : rootMenus) {
            sortMenuChildren(menu);
        }
        
        return rootMenus;
    }
    
    /**
     * 递归排序菜单子节点
     */
    private void sortMenuChildren(MenuPermissionVO menu) {
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            menu.getChildren().sort(Comparator.comparing(MenuPermissionVO::getSort));
            for (MenuPermissionVO child : menu.getChildren()) {
                sortMenuChildren(child);
            }
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            // 从token中获取loginId
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                return false;
            }
            
            // 验证是否为外部API token（以"consumer_"开头）
            String loginIdStr = String.valueOf(loginId);
            if (!loginIdStr.startsWith("consumer_")) {
                return false;
            }
            
            // 验证token是否有效（检查是否登录）
            return StpUtil.isLogin(loginId);
        } catch (Exception e) {
            log.warn("验证token失败: {}", e.getMessage());
            return false;
        }
    }
}

