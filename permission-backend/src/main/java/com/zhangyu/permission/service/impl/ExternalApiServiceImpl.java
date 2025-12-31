package com.zhangyu.permission.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.DepartmentPushDTO;
import com.zhangyu.permission.dto.MenuPermissionQueryDTO;
import com.zhangyu.permission.dto.NormalUserPushDTO;
import com.zhangyu.permission.dto.TokenRequestDTO;
import com.zhangyu.permission.entity.*;
import com.zhangyu.permission.mapper.*;
import com.zhangyu.permission.service.ConsumerInfoService;
import com.zhangyu.permission.service.DepartmentService;
import com.zhangyu.permission.service.ExternalApiService;
import com.zhangyu.permission.vo.DepartmentPushError;
import com.zhangyu.permission.vo.DepartmentPushResult;
import com.zhangyu.permission.vo.MenuPermissionVO;
import com.zhangyu.permission.vo.NormalUserPushError;
import com.zhangyu.permission.vo.NormalUserPushResult;
import com.zhangyu.permission.vo.TokenResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private DepartmentMapper departmentMapper;
    
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
    
    @Autowired
    private ConsumerInfoService consumerInfoService;
    
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
    public TokenResponseVO getConsumerToken(TokenRequestDTO tokenRequest) {
        // 1. 验证clientId和clientSecret（从consumer_info表查询）
        ConsumerInfo consumer = consumerInfoService.getByClientId(tokenRequest.getClientId());
        if (consumer == null) {
            throw new BusinessException("clientId不存在或已禁用");
        }
        
        if (consumer.getStatus() == null || consumer.getStatus() != 1) {
            throw new BusinessException("消费者已禁用");
        }
        
        if (consumer.getDeleted() != null && consumer.getDeleted() == 1) {
            throw new BusinessException("消费者已删除");
        }
        
        if (!consumer.getClientSecret().equals(tokenRequest.getClientSecret())) {
            throw new BusinessException("clientSecret错误");
        }
        
        // 2. 使用consumerId作为登录标识，生成token
        // 注意：这里使用consumerId作为token的loginId，与普通用户登录和应用登录区分开
        // 使用前缀"consumer_"来区分外部API consumer token和普通用户token
        String loginId = "consumer_" + consumer.getId();
        StpUtil.login(loginId);
        String token = StpUtil.getTokenValue();
        
        // 设置token的额外信息（可选）
        StpUtil.getSessionByLoginId(loginId).set("consumerId", consumer.getId());
        StpUtil.getSessionByLoginId(loginId).set("consumerName", consumer.getConsumerName());
        StpUtil.getSessionByLoginId(loginId).set("clientId", consumer.getClientId());
        
        // 3. 构建响应
        TokenResponseVO response = new TokenResponseVO();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(tokenTimeout);
        response.setConsumerName(consumer.getConsumerName());
        
        log.info("消费者 {} (clientId: {}) 获取token成功", consumer.getConsumerName(), consumer.getClientId());
        return response;
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
    
    @Override
    public boolean validateConsumerToken(String token) {
        try {
            // 从token中获取loginId
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                return false;
            }
            
            // 验证是否为consumer token（以"consumer_"开头）
            String loginIdStr = String.valueOf(loginId);
            if (!loginIdStr.startsWith("consumer_")) {
                return false;
            }
            
            // 验证token是否有效（检查是否登录）
            return StpUtil.isLogin(loginId);
        } catch (Exception e) {
            log.warn("验证consumer token失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DepartmentPushResult pushDepartments(List<DepartmentPushDTO> departmentList) {
        DepartmentPushResult result = new DepartmentPushResult();
        result.setTotalCount(departmentList != null ? departmentList.size() : 0);
        result.setSuccessCount(0);
        result.setFailCount(0);
        result.setSuccessIds(new ArrayList<>());
        result.setErrors(new ArrayList<>());
        
        if (departmentList == null || departmentList.isEmpty()) {
            return result;
        }
        
        // 构建部门名称到DTO的映射（用于查找父部门）
        Map<String, DepartmentPushDTO> nameToDtoMap = new HashMap<>();
        for (DepartmentPushDTO dto : departmentList) {
            nameToDtoMap.put(dto.getName(), dto);
        }
        
        // 构建部门名称到ID的映射（用于本次推送中已创建的部门）
        Map<String, Long> nameToIdMap = new HashMap<>();
        
        // 按层级排序，先处理顶级部门，再处理子部门
        // 这样可以确保父部门先创建，子部门可以正确关联
        List<DepartmentPushDTO> sortedList = new ArrayList<>(departmentList);
        sortedList.sort((a, b) -> {
            // 先按层级排序（层级小的在前）
            int levelA = a.getLevel() != null ? a.getLevel() : (a.getParentId() == null ? 1 : 999);
            int levelB = b.getLevel() != null ? b.getLevel() : (b.getParentId() == null ? 1 : 999);
            int levelCompare = Integer.compare(levelA, levelB);
            if (levelCompare != 0) {
                return levelCompare;
            }
            // 同层级按名称排序
            return a.getName().compareTo(b.getName());
        });
        
        // 批量处理部门数据（只插入/更新，不更新 ancestors）
        for (DepartmentPushDTO departmentDTO : sortedList) {
            try {
                Long departmentId = pushSingleDepartment(departmentDTO, nameToIdMap);
                nameToIdMap.put(departmentDTO.getName(), departmentId);
                result.getSuccessIds().add(departmentId);
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                DepartmentPushError error = new DepartmentPushError();
                error.setDepartmentName(departmentDTO.getName());
                error.setErrorMessage(e.getMessage());
                result.getErrors().add(error);
                result.setFailCount(result.getFailCount() + 1);
                log.warn("推送部门数据失败，部门名称: {}，错误: {}", departmentDTO.getName(), e.getMessage());
            }
        }
        
        // 所有部门插入完成后，统一更新 ancestors
        // 从顶级部门（parentId=null）开始，递归更新所有部门的 ancestors
        if (result.getSuccessCount() > 0) {
            try {
                departmentService.refreshAllAncestors();
                log.info("批量推送部门数据后，统一更新 ancestors 完成");
            } catch (Exception e) {
                log.error("批量推送部门数据后，统一更新 ancestors 失败: {}", e.getMessage(), e);
                // 不抛出异常，因为部门数据已经插入成功
            }
        }
        
        log.info("批量推送部门数据完成，总数: {}，成功: {}，失败: {}", 
            result.getTotalCount(), result.getSuccessCount(), result.getFailCount());
        return result;
    }
    
    /**
     * 推送单个部门数据（内部方法）
     * 
     * @param departmentDTO 部门数据
     * @param nameToIdMap 本次推送中已创建的部门名称到ID的映射（用于查找父部门）
     * @return 部门ID
     */
    private Long pushSingleDepartment(DepartmentPushDTO departmentDTO, Map<String, Long> nameToIdMap) {
        Department existDept = null;
        boolean isUpdate = false;
        Long currentDeptId = null;
        
        // 1. 优先根据 code 查找（如果提供了 code）
        if (departmentDTO.getCode() != null && !departmentDTO.getCode().trim().isEmpty()) {
            LambdaQueryWrapper<Department> codeWrapper = new LambdaQueryWrapper<>();
            codeWrapper.eq(Department::getCode, departmentDTO.getCode());
            existDept = departmentMapper.selectOne(codeWrapper);
            if (existDept != null) {
                isUpdate = true;
                currentDeptId = existDept.getId();
                log.debug("根据 code {} 找到已存在的部门，将进行更新，部门ID: {}，部门名称: {}", 
                        departmentDTO.getCode(), existDept.getId(), existDept.getName());
            }
        }
        
        // 2. 如果未通过 code 找到，则根据ID查找（如果提供了ID）
        if (!isUpdate && departmentDTO.getId() != null) {
            existDept = departmentMapper.selectById(departmentDTO.getId());
            if (existDept != null) {
                isUpdate = true;
                currentDeptId = existDept.getId();
                // 检查名称是否匹配（如果名称不一致，给出警告）
                if (!existDept.getName().equals(departmentDTO.getName())) {
                    log.warn("部门ID {} 已存在，但名称不匹配（数据库: {}，推送: {}），将更新名称", 
                            departmentDTO.getId(), existDept.getName(), departmentDTO.getName());
                }
            } else {
                // ID不存在，检查该ID是否已被其他部门使用（理论上不应该发生，因为ID是主键）
                // 这里主要是为了完整性检查
                log.debug("使用指定的部门ID {} 创建新部门: {}", departmentDTO.getId(), departmentDTO.getName());
            }
        }
        
        // 3. 如果都未找到，则根据名称查找（作为兜底）
        /*if (!isUpdate) {
            LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Department::getName, departmentDTO.getName());
            existDept = departmentMapper.selectOne(wrapper);
            if (existDept != null) {
                isUpdate = true;
                currentDeptId = existDept.getId();
                // 如果提供了ID但与名称查找到的ID不一致，给出警告并使用名称查找到的ID
                if (departmentDTO.getId() != null && !departmentDTO.getId().equals(existDept.getId())) {
                    log.warn("部门名称 {} 已存在，但ID不匹配（推送ID: {}，数据库ID: {}），将使用数据库ID进行更新", 
                            departmentDTO.getName(), departmentDTO.getId(), existDept.getId());
                }
            }
        }*/
        
        Department department;
        if (isUpdate) {
            // 如果存在，则更新
            department = existDept;
            // 更新时保留原有的 id 和 createTime
            BeanUtil.copyProperties(departmentDTO, department, "id", "createTime");
        } else {
            // 如果不存在，则创建
            department = new Department();
            BeanUtil.copyProperties(departmentDTO, department);
            // 如果提供了ID，使用该ID；否则由数据库自动生成
            if (departmentDTO.getId() != null) {
                department.setId(departmentDTO.getId());
                // 检查ID是否在本次推送中已被使用
                if (nameToIdMap.containsValue(departmentDTO.getId())) {
                    throw new BusinessException("部门ID " + departmentDTO.getId() + " 在本次推送中已被使用");
                }
            }
        }
        
        // 2. 处理父部门关系
        // 注意：插入/更新时不检查父部门是否存在，因为插入顺序可能不一样
        // 所有的验证（循环引用、层级限制等）都在 refreshAncestors 时进行
        if (departmentDTO.getParentId() != null) {
            // 2.1 基本检查：不能将自己设为父部门（更新时）
            if (isUpdate && currentDeptId != null && currentDeptId.equals(departmentDTO.getParentId())) {
                throw new BusinessException("不能将部门设置为自己的父部门，会造成循环引用");
            }
            
            // 直接设置 parentId，不检查父部门是否存在
            // 如果父部门不存在，refreshAncestors 会将其处理为顶级部门
            department.setParentId(departmentDTO.getParentId());
            
            // 如果提供了层级，则使用；否则在 refreshAncestors 时自动计算
            if (departmentDTO.getLevel() != null) {
                department.setLevel(departmentDTO.getLevel());
            }
        } else {
            // 顶级部门
            department.setParentId(null);
            if (departmentDTO.getLevel() == null) {
                department.setLevel(1);
            } else {
                department.setLevel(departmentDTO.getLevel());
            }
        }
        
        // 3. 设置默认值
        if (departmentDTO.getSort() == null) {
            department.setSort(0);
        }
        if (departmentDTO.getStatus() == null) {
            department.setStatus("正常");
        }
        
        // 4. 保存或更新（不更新 ancestors，等批量插入完成后统一更新）
        if (isUpdate) {
            departmentMapper.updateById(department);
        } else {
            // 如果是新插入且提供了ID，需要使用原生SQL插入以确保ID被使用
            // MyBatis-Plus 的 insert 方法在 IdType.AUTO 模式下会忽略手动设置的ID
            if (department.getId() != null) {
                // 设置创建时间和更新时间
                if (department.getCreateTime() == null) {
                    department.setCreateTime(LocalDateTime.now());
                }
                if (department.getUpdateTime() == null) {
                    department.setUpdateTime(LocalDateTime.now());
                }
                // 使用原生SQL插入，确保ID被使用
                departmentMapper.insertWithId(department);
            } else {
                // 如果没有提供ID，使用MyBatis-Plus的insert方法，由数据库自动生成
                departmentMapper.insert(department);
            }
        }
        
        Long finalDeptId = department.getId();
        log.debug("推送部门数据成功，部门ID: {}，部门名称: {}（ancestors 将在批量插入完成后统一更新）", 
                finalDeptId, department.getName());
        return finalDeptId;
    }
    
    /**
     * 检查循环引用：检查父部门的祖先链中是否包含当前部门
     * 
     * @param currentDeptId 当前部门ID
     * @param parentDeptId 父部门ID
     * @return 如果存在循环引用返回true，否则返回false
     */
    private boolean checkCircularReference(Long currentDeptId, Long parentDeptId) {
        if (currentDeptId == null || parentDeptId == null) {
            return false;
        }
        
        // 如果父部门ID就是当前部门ID，直接返回true
        if (parentDeptId.equals(currentDeptId)) {
            return true;
        }
        
        // 获取父部门的祖先链
        Department parent = departmentMapper.selectById(parentDeptId);
        if (parent == null) {
            return false;
        }
        
        // 检查祖先链中是否包含当前部门ID
        String ancestors = parent.getAncestors();
        if (ancestors == null || ancestors.trim().isEmpty()) {
            return false;
        }
        
        // 将祖先链按逗号分割，检查是否包含当前部门ID
        String[] ancestorIds = ancestors.split(",");
        for (String ancestorId : ancestorIds) {
            try {
                Long ancestorIdLong = Long.parseLong(ancestorId.trim());
                if (ancestorIdLong.equals(currentDeptId)) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // 忽略格式错误
            }
        }
        
        // 递归检查父部门的父部门
        if (parent.getParentId() != null) {
            return checkCircularReference(currentDeptId, parent.getParentId());
        }
        
        return false;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public NormalUserPushResult pushNormalUsers(List<NormalUserPushDTO> userList) {
        NormalUserPushResult result = new NormalUserPushResult();
        result.setTotalCount(userList != null ? userList.size() : 0);
        result.setSuccessCount(0);
        result.setFailCount(0);
        result.setSuccessIds(new ArrayList<>());
        result.setErrors(new ArrayList<>());
        
        if (userList == null || userList.isEmpty()) {
            return result;
        }
        
        // 批量处理用户数据
        for (NormalUserPushDTO userDTO : userList) {
            try {
                Long userId = pushSingleNormalUser(userDTO);
                result.getSuccessIds().add(userId);
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                NormalUserPushError error = new NormalUserPushError();
                error.setWorkNo(userDTO.getWorkNo());
                error.setErrorMessage(e.getMessage());
                result.getErrors().add(error);
                result.setFailCount(result.getFailCount() + 1);
                log.warn("推送普通用户数据失败，工号: {}，错误: {}", userDTO.getWorkNo(), e.getMessage());
            }
        }
        
        log.info("批量推送普通用户数据完成，总数: {}，成功: {}，失败: {}", 
            result.getTotalCount(), result.getSuccessCount(), result.getFailCount());
        return result;
    }
    
    /**
     * 推送单个普通用户数据（内部方法）
     * 根据工号查询，存在则更新，不存在则插入
     * 
     * @param userDTO 用户数据
     * @return 用户ID
     */
    private Long pushSingleNormalUser(NormalUserPushDTO userDTO) {
        // 1. 验证工号不能为空
        if (userDTO.getWorkNo() == null || userDTO.getWorkNo().trim().isEmpty()) {
            throw new BusinessException("工号不能为空");
        }
        
        // 2. 根据工号查询用户是否存在
        LambdaQueryWrapper<NormalUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NormalUser::getWorkNo, userDTO.getWorkNo().trim());
        NormalUser existUser = normalUserMapper.selectOne(wrapper);
        
        boolean isUpdate = existUser != null;
        NormalUser normalUser;
        
        if (isUpdate) {
            // 3. 如果存在，则更新
            normalUser = existUser;
            // 更新时保留原有的 id 和 createTime
            BeanUtil.copyProperties(userDTO, normalUser, "id", "createTime");
            log.debug("根据工号 {} 找到已存在的用户，将进行更新", userDTO.getWorkNo());
        } else {
            // 4. 如果不存在，则创建新用户
            normalUser = new NormalUser();
            BeanUtil.copyProperties(userDTO, normalUser);
            log.debug("根据工号 {} 未找到用户，将创建新用户", userDTO.getWorkNo());
        }
        
        // 5. 验证部门是否存在（如果提供了部门ID）
        /*if (userDTO.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(userDTO.getDepartmentId());
            if (department == null) {
                throw new BusinessException("部门ID " + userDTO.getDepartmentId() + " 不存在");
            }
            normalUser.setDepartmentId(userDTO.getDepartmentId());
        }*/
        normalUser.setDepartmentId(userDTO.getDepartmentId());
        // 6. 设置默认值
        if (userDTO.getStatus() == null || userDTO.getStatus().trim().isEmpty()) {
            normalUser.setStatus("正常");
        } else {
            normalUser.setStatus(userDTO.getStatus());
        }
        
        // 7. 保存或更新
        if (isUpdate) {
            normalUserMapper.updateById(normalUser);
            log.debug("更新普通用户数据成功，用户ID: {}，工号: {}", normalUser.getId(), normalUser.getWorkNo());
        } else {
            normalUserMapper.insert(normalUser);
            log.debug("创建普通用户数据成功，用户ID: {}，工号: {}", normalUser.getId(), normalUser.getWorkNo());
        }
        
        return normalUser.getId();
    }
}

