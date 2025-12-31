package com.zhangyu.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.AssignUsersDTO;
import com.zhangyu.permission.dto.AvailableUserQueryDTO;
import com.zhangyu.permission.dto.RoleUserQueryDTO;
import com.zhangyu.permission.entity.AppRole;
import com.zhangyu.permission.entity.AppRoleUser;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.entity.Department;
import com.zhangyu.permission.entity.NormalUser;
import com.zhangyu.permission.mapper.AppRoleMapper;
import com.zhangyu.permission.mapper.AppRoleUserMapper;
import com.zhangyu.permission.mapper.ApplicationMapper;
import com.zhangyu.permission.mapper.DepartmentMapper;
import com.zhangyu.permission.mapper.NormalUserMapper;
import com.zhangyu.permission.service.AppRoleUserService;
import com.zhangyu.permission.service.DepartmentService;
import com.zhangyu.permission.vo.NormalUserListVO;
import com.zhangyu.permission.vo.RoleUserListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用角色用户服务实现类
 * 
 * @author zhangyu
 */
@Slf4j
@Service
public class AppRoleUserServiceImpl extends ServiceImpl<AppRoleUserMapper, AppRoleUser> implements AppRoleUserService {
    
    @Autowired
    private AppRoleMapper appRoleMapper;
    
    @Autowired
    private ApplicationMapper applicationMapper;
    
    @Autowired
    private NormalUserMapper normalUserMapper;
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Override
    public PageResult<RoleUserListVO> pageQueryUsers(Long appId, Long roleId, RoleUserQueryDTO queryDTO) {
        // 验证角色是否存在且属于该应用
        AppRole role = appRoleMapper.selectById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        // 查询角色用户关联
        LambdaQueryWrapper<AppRoleUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppRoleUser::getRoleId, roleId);
        
        // 分页查询
        Page<AppRoleUser> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        IPage<AppRoleUser> pageResult = this.page(page, wrapper);
        
        // 获取所有工号
        Set<String> workNos = pageResult.getRecords().stream()
                .map(AppRoleUser::getUserWorkNo)
                .collect(Collectors.toSet());
        
        if (workNos.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0L, queryDTO.getPage(), queryDTO.getPageSize());
        }
        
        // 查询用户信息
        LambdaQueryWrapper<NormalUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(NormalUser::getWorkNo, workNos);
        
        // 注意：不过滤用户状态，已分配的用户列表中会显示禁用的用户
        
        // 应用查询条件
        String adAccount = queryDTO.getAdAccount();
        if (StringUtils.hasText(adAccount) && !"undefined".equals(adAccount)) {
            userWrapper.like(NormalUser::getAdAccount, adAccount.trim());
        }
        
        String workNo = queryDTO.getWorkNo();
        if (StringUtils.hasText(workNo) && !"undefined".equals(workNo)) {
            userWrapper.like(NormalUser::getWorkNo, workNo.trim());
        }
        
        String mobile = queryDTO.getMobile();
        if (StringUtils.hasText(mobile) && !"undefined".equals(mobile)) {
            userWrapper.like(NormalUser::getMobile, mobile.trim());
        }
        
        List<NormalUser> users = normalUserMapper.selectList(userWrapper);
        
        // 获取应用信息
        Application app = applicationMapper.selectById(appId);
        String appName = app != null ? app.getName() : "";
        
        // 构建VO列表（需要先过滤，再分页）
        List<RoleUserListVO> allVoList = new ArrayList<>();
        for (AppRoleUser roleUser : pageResult.getRecords()) {
            NormalUser user = users.stream()
                    .filter(u -> u.getWorkNo().equals(roleUser.getUserWorkNo()))
                    .findFirst()
                    .orElse(null);
            
            if (user != null) {
                // 应用查询条件过滤（由于数据库查询已过滤，这里只需要检查是否匹配）
                boolean match = true;
                
                if (match && StringUtils.hasText(adAccount) && !"undefined".equals(adAccount)) {
                    match = user.getAdAccount() != null && user.getAdAccount().contains(adAccount.trim());
                }
                
                if (match && StringUtils.hasText(workNo) && !"undefined".equals(workNo)) {
                    match = user.getWorkNo() != null && user.getWorkNo().contains(workNo.trim());
                }
                
                if (match && StringUtils.hasText(mobile) && !"undefined".equals(mobile)) {
                    match = user.getMobile() != null && user.getMobile().contains(mobile.trim());
                }
                
                if (match) {
                    RoleUserListVO vo = new RoleUserListVO();
                    vo.setId(roleUser.getId());
                    vo.setAppName(appName);
                    vo.setRoleName(role.getName());
                    vo.setWorkNo(user.getWorkNo());
                    vo.setAdAccount(user.getAdAccount());
                    vo.setUserName(user.getName());
                    vo.setMobile(user.getMobile());
                    vo.setStatus(user.getStatus());
                    vo.setCreateTime(roleUser.getCreateTime());
                    allVoList.add(vo);
                }
            }
        }
        
        // 手动分页
        int start = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        int end = Math.min(start + queryDTO.getPageSize(), allVoList.size());
        List<RoleUserListVO> voList = start < allVoList.size() ? allVoList.subList(start, end) : new ArrayList<>();
        
        return PageResult.of(voList, (long) allVoList.size(), queryDTO.getPage(), queryDTO.getPageSize());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUsers(Long appId, Long roleId, AssignUsersDTO assignDTO) {
        // 验证角色是否存在且属于该应用
        AppRole role = appRoleMapper.selectById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        if (assignDTO.getWorkNos() == null || assignDTO.getWorkNos().isEmpty()) {
            throw new BusinessException("用户工号列表不能为空");
        }
        
        // 验证用户是否存在
        LambdaQueryWrapper<NormalUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(NormalUser::getWorkNo, assignDTO.getWorkNos());
        List<NormalUser> users = normalUserMapper.selectList(userWrapper);
        
        if (users.size() != assignDTO.getWorkNos().size()) {
            throw new BusinessException("部分用户不存在");
        }
        
        // 查询已分配的用户
        LambdaQueryWrapper<AppRoleUser> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(AppRoleUser::getRoleId, roleId);
        existWrapper.in(AppRoleUser::getUserWorkNo, assignDTO.getWorkNos());
        List<AppRoleUser> existUsers = this.list(existWrapper);
        
        Set<String> existWorkNos = existUsers.stream()
                .map(AppRoleUser::getUserWorkNo)
                .collect(Collectors.toSet());
        
        // 批量插入新分配的用户
        List<AppRoleUser> toInsert = new ArrayList<>();
        for (String workNo : assignDTO.getWorkNos()) {
            if (!existWorkNos.contains(workNo)) {
                AppRoleUser roleUser = new AppRoleUser();
                roleUser.setRoleId(roleId);
                roleUser.setUserWorkNo(workNo);
                roleUser.setAssignType("个人");
                // createTime 和 updateTime 由数据库自动设置（DEFAULT CURRENT_TIMESTAMP）
                toInsert.add(roleUser);
            }
        }
        
        if (!toInsert.isEmpty()) {
            log.info("准备插入 {} 条用户分配记录到角色 {}，用户工号：{}", 
                    toInsert.size(), roleId, 
                    toInsert.stream().map(AppRoleUser::getUserWorkNo).collect(Collectors.toList()));
            
            // 打印每条要插入的记录详情
            for (AppRoleUser item : toInsert) {
                log.info("准备插入记录：roleId={}, userWorkNo={}, assignType={}", 
                        item.getRoleId(), item.getUserWorkNo(), item.getAssignType());
            }
            
            try {
                // MyBatis-Plus 的 saveBatch 默认批量大小为1000，这里显式指定批量大小
                // 如果数据量小，也可以逐个插入以确保数据正确保存
                boolean result = this.saveBatch(toInsert, 1000);
                log.info("saveBatch返回结果：{}", result);
                
                // 验证插入是否成功：查询刚插入的数据
                LambdaQueryWrapper<AppRoleUser> verifyWrapper = new LambdaQueryWrapper<>();
                verifyWrapper.eq(AppRoleUser::getRoleId, roleId);
                verifyWrapper.in(AppRoleUser::getUserWorkNo, 
                        toInsert.stream().map(AppRoleUser::getUserWorkNo).collect(Collectors.toList()));
                List<AppRoleUser> insertedRecords = this.list(verifyWrapper);
                log.info("验证插入结果：期望插入 {} 条，实际查询到 {} 条", toInsert.size(), insertedRecords.size());
                
                if (insertedRecords.size() < toInsert.size()) {
                    log.warn("插入记录数不匹配！期望：{}，实际：{}", toInsert.size(), insertedRecords.size());
                    throw new BusinessException("部分用户分配保存失败，请检查数据");
                }
                
                log.info("成功分配 {} 个用户到角色 {}，用户工号：{}", 
                        toInsert.size(), roleId,
                        toInsert.stream().map(AppRoleUser::getUserWorkNo).collect(Collectors.toList()));
            } catch (Exception e) {
                log.error("保存用户分配失败，角色ID：{}，用户工号：{}，错误信息：{}", 
                        roleId, 
                        toInsert.stream().map(AppRoleUser::getUserWorkNo).collect(Collectors.toList()),
                        e.getMessage(), e);
                // 打印异常堆栈
                e.printStackTrace();
                throw new BusinessException("保存用户分配失败：" + e.getMessage());
            }
        } else {
            log.info("所有用户已分配，无需重复分配，角色ID：{}，用户工号：{}", 
                    roleId, assignDTO.getWorkNos());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRevokeUsers(Long appId, Long roleId, List<Long> userIds) {
        // 验证角色是否存在且属于该应用
        AppRole role = appRoleMapper.selectById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException("用户ID列表不能为空");
        }
        
        // 验证关联记录是否属于该角色
        LambdaQueryWrapper<AppRoleUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppRoleUser::getRoleId, roleId);
        wrapper.in(AppRoleUser::getId, userIds);
        List<AppRoleUser> roleUsers = this.list(wrapper);
        
        if (roleUsers.size() != userIds.size()) {
            throw new BusinessException("部分关联记录不存在或不属于该角色");
        }
        
        // 批量删除
        this.removeByIds(userIds);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeUser(Long appId, Long roleId, Long userId) {
        // 验证角色是否存在且属于该应用
        AppRole role = appRoleMapper.selectById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        // 验证关联记录是否存在且属于该角色
        AppRoleUser roleUser = this.getById(userId);
        if (roleUser == null || !roleUser.getRoleId().equals(roleId)) {
            throw new BusinessException("关联记录不存在或不属于该角色");
        }
        
        // 删除
        this.removeById(userId);
    }
    
    @Override
    public PageResult<NormalUserListVO> getAvailableUsers(Long appId, Long roleId, AvailableUserQueryDTO queryDTO) {
        // 验证角色是否存在且属于该应用
        AppRole role = appRoleMapper.selectById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        // 查询已分配的用户工号
        LambdaQueryWrapper<AppRoleUser> roleUserWrapper = new LambdaQueryWrapper<>();
        roleUserWrapper.eq(AppRoleUser::getRoleId, roleId);
        List<AppRoleUser> roleUsers = this.list(roleUserWrapper);
        Set<String> assignedWorkNos = roleUsers.stream()
                .map(AppRoleUser::getUserWorkNo)
                .collect(Collectors.toSet());
        
        // 构建用户查询条件
        LambdaQueryWrapper<NormalUser> userWrapper = new LambdaQueryWrapper<>();
        
        // 只显示状态为"正常"的用户，禁用的用户不显示在待分配列表中
        userWrapper.eq(NormalUser::getStatus, "正常");
        
        // 排除已分配的用户
        if (!assignedWorkNos.isEmpty()) {
            userWrapper.notIn(NormalUser::getWorkNo, assignedWorkNos);
        }
        
        // 部门ID筛选（包含该部门及其所有子部门的用户）
        Long departmentId = queryDTO.getDepartmentId();
        if (departmentId != null) {
            // 获取该部门及其所有子部门的ID列表
            List<Long> departmentIds = departmentService.getDepartmentAndChildrenIds(departmentId);
            if (!departmentIds.isEmpty()) {
                userWrapper.in(NormalUser::getDepartmentId, departmentIds);
            } else {
                // 如果没有找到部门，返回空结果
                userWrapper.eq(NormalUser::getDepartmentId, -1L); // 不存在的ID，确保查询结果为空
            }
        }
        
        // AD域账号模糊查询
        String adAccount = queryDTO.getAdAccount();
        if (StringUtils.hasText(adAccount) && !"undefined".equals(adAccount)) {
            userWrapper.like(NormalUser::getAdAccount, adAccount.trim());
        }
        
        // 用户昵称模糊查询
        String nickname = queryDTO.getNickname();
        if (StringUtils.hasText(nickname) && !"undefined".equals(nickname)) {
            userWrapper.like(NormalUser::getNickname, nickname.trim());
        }
        
        // 工号模糊查询
        String workNo = queryDTO.getWorkNo();
        if (StringUtils.hasText(workNo) && !"undefined".equals(workNo)) {
            userWrapper.like(NormalUser::getWorkNo, workNo.trim());
        }
        
        // 分页查询
        Page<NormalUser> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        IPage<NormalUser> pageResult = normalUserMapper.selectPage(page, userWrapper);
        
        // 转换为VO
        List<NormalUserListVO> voList = pageResult.getRecords().stream().map(user -> {
            NormalUserListVO vo = new NormalUserListVO();
            BeanUtil.copyProperties(user, vo);
            
            // 查询部门名称
            if (user.getDepartmentId() != null) {
                Department dept = departmentMapper.selectById(user.getDepartmentId());
                if (dept != null) {
                    vo.setDepartmentName(dept.getName());
                }
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return PageResult.of(voList, pageResult.getTotal(), queryDTO.getPage(), queryDTO.getPageSize());
    }
}

