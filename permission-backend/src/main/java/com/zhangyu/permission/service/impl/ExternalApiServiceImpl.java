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
import com.zhangyu.permission.service.DepartmentService;
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
    private DepartmentService departmentService;
    
    @Autowired
    private NormalUserMapper normalUserMapper;
    
    @Autowired
    private AppRoleDepartmentMapper appRoleDepartmentMapper;
    
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
        // 1. 验证clientId和clientSecret（从applications表查询）
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getClientId, tokenRequest.getClientId());
        wrapper.eq(Application::getStatus, "正常");
        Application application = applicationMapper.selectOne(wrapper);
        
        if (application == null) {
            throw new BusinessException("clientId不存在或应用已禁用");
        }
        
        if (!application.getClientSecret().equals(tokenRequest.getClientSecret())) {
            throw new BusinessException("clientSecret错误");
        }
        
        // 2. 使用应用ID作为登录标识，生成token
        // 注意：这里使用应用ID作为token的loginId，与普通用户登录区分开
        // 使用前缀"app_"来区分外部API token和普通用户token
        String loginId = "app_" + application.getId();
        StpUtil.login(loginId);
        String token = StpUtil.getTokenValue();
        
        // 设置token的额外信息（可选）
        StpUtil.getSessionByLoginId(loginId).set("appId", application.getId());
        StpUtil.getSessionByLoginId(loginId).set("appName", application.getName());
        StpUtil.getSessionByLoginId(loginId).set("clientId", application.getClientId());
        
        // 3. 构建响应
        TokenResponseVO response = new TokenResponseVO();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(tokenTimeout);
        response.setConsumerName(application.getName());
        
        log.info("应用 {} (clientId: {}) 获取token成功", application.getName(), application.getClientId());
        return response;
    }
    
    @Override
    public List<MenuPermissionVO> getMenuPermissions(String token, MenuPermissionQueryDTO queryDTO) {
        // 1. 验证应用是否存在
        Application application = applicationMapper.selectById(queryDTO.getAppId());
        if (application == null) {
            throw new BusinessException("应用不存在");
        }
        
        if (!"正常".equals(application.getStatus())) {
            throw new BusinessException("应用已禁用");
        }
        
        // 1.1 从token中获取clientId，验证应用ID和clientId的对应关系
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException("Token无效，请重新获取token");
        }
        
        // 从token中获取loginId（格式：app_应用ID）
        Object loginIdObj = StpUtil.getLoginIdByToken(token);
        if (loginIdObj == null) {
            throw new BusinessException("Token无效，请重新获取token");
        }
        
        String loginId = loginIdObj.toString();
        if (!loginId.startsWith("app_")) {
            throw new BusinessException("Token类型错误，请使用应用token");
        }
        
        // 提取应用ID
        Long tokenAppId = Long.parseLong(loginId.substring(4));
        
        // 验证token中的应用ID与查询参数中的应用ID是否一致
        if (!tokenAppId.equals(queryDTO.getAppId())) {
            throw new BusinessException("无权调用此应用权限，应用ID不匹配");
        }
        
        // 从session中获取clientId，再次验证
        String tokenClientId = (String) StpUtil.getSessionByLoginId(loginId).get("clientId");
        if (tokenClientId == null || !tokenClientId.equals(application.getClientId())) {
            throw new BusinessException("无权调用此应用权限，clientId不匹配");
        }
        
        // 2. 查询用户在该应用下的所有角色（包含：按用户分配 + 按部门分配）
        Set<Long> roleIds = new HashSet<>();
        
        // 2.1 按用户分配的角色
        LambdaQueryWrapper<AppRoleUser> roleUserWrapper = new LambdaQueryWrapper<>();
        roleUserWrapper.eq(AppRoleUser::getUserWorkNo, queryDTO.getWorkNo());
        List<AppRoleUser> roleUsers = appRoleUserMapper.selectList(roleUserWrapper);
        if (!roleUsers.isEmpty()) {
            roleIds.addAll(
                    roleUsers.stream()
                            .map(AppRoleUser::getRoleId)
                            .collect(Collectors.toSet())
            );
        }
        
        // 2.2 按部门分配的角色
        // 先根据工号查询普通用户，获取其主部门ID
        LambdaQueryWrapper<NormalUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(NormalUser::getWorkNo, queryDTO.getWorkNo());
        userWrapper.eq(NormalUser::getStatus, "正常");
        NormalUser normalUser = normalUserMapper.selectOne(userWrapper);
        if (normalUser != null && normalUser.getDepartmentId() != null) {
            // 获取该部门及其所有子部门ID
            List<Long> deptIds = departmentService.getDepartmentAndChildrenIds(normalUser.getDepartmentId());
            if (deptIds != null && !deptIds.isEmpty()) {
                LambdaQueryWrapper<AppRoleDepartment> roleDeptWrapper = new LambdaQueryWrapper<>();
                roleDeptWrapper.in(AppRoleDepartment::getDepartmentId, deptIds);
                List<AppRoleDepartment> roleDepts = appRoleDepartmentMapper.selectList(roleDeptWrapper);
                if (roleDepts != null && !roleDepts.isEmpty()) {
                    roleIds.addAll(
                            roleDepts.stream()
                                    .map(AppRoleDepartment::getRoleId)
                                    .collect(Collectors.toSet())
                    );
                }
            }
        }
        
        if (roleIds.isEmpty()) {
            // 用户既没有按用户分配的角色，也没有按部门分配的角色，直接返回空
            return new ArrayList<>();
        }
        
        // 3. 验证角色是否属于该应用，并过滤出有效的角色
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
            
            // 验证是否为外部API token（以"app_"开头）
            String loginIdStr = String.valueOf(loginId);
            if (!loginIdStr.startsWith("app_")) {
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

