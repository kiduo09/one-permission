package com.zhangyu.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.AssignDepartmentsDTO;
import com.zhangyu.permission.dto.RoleDepartmentQueryDTO;
import com.zhangyu.permission.entity.AppRole;
import com.zhangyu.permission.entity.AppRoleDepartment;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.entity.Department;
import com.zhangyu.permission.entity.NormalUser;
import com.zhangyu.permission.mapper.AppRoleMapper;
import com.zhangyu.permission.mapper.AppRoleDepartmentMapper;
import com.zhangyu.permission.mapper.ApplicationMapper;
import com.zhangyu.permission.mapper.DepartmentMapper;
import com.zhangyu.permission.mapper.NormalUserMapper;
import com.zhangyu.permission.service.AppRoleDepartmentService;
import com.zhangyu.permission.service.DepartmentService;
import com.zhangyu.permission.vo.RoleDepartmentListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用角色部门服务实现类
 * 
 * @author zhangyu
 */
@Service
public class AppRoleDepartmentServiceImpl extends ServiceImpl<AppRoleDepartmentMapper, AppRoleDepartment> implements AppRoleDepartmentService {
    
    @Autowired
    private AppRoleMapper appRoleMapper;
    
    @Autowired
    private ApplicationMapper applicationMapper;
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    @Autowired
    private NormalUserMapper normalUserMapper;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Override
    public PageResult<RoleDepartmentListVO> pageQueryDepartments(Long appId, Long roleId, RoleDepartmentQueryDTO queryDTO) {
        // 验证角色是否存在且属于该应用
        AppRole role = appRoleMapper.selectById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        // 查询角色部门关联
        LambdaQueryWrapper<AppRoleDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppRoleDepartment::getRoleId, roleId);
        
        // 分页查询
        Page<AppRoleDepartment> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        IPage<AppRoleDepartment> pageResult = this.page(page, wrapper);
        
        // 获取所有部门ID
        Set<Long> departmentIds = pageResult.getRecords().stream()
                .map(AppRoleDepartment::getDepartmentId)
                .collect(Collectors.toSet());
        
        if (departmentIds.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0L, queryDTO.getPage(), queryDTO.getPageSize());
        }
        
        // 查询部门信息
        LambdaQueryWrapper<Department> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.in(Department::getId, departmentIds);
        
        // 部门名称模糊查询
        String name = queryDTO.getName();
        if (StringUtils.hasText(name) && !"undefined".equals(name)) {
            deptWrapper.like(Department::getName, name.trim());
        }
        
        List<Department> departments = departmentMapper.selectList(deptWrapper);
        
        // 获取应用信息
        Application app = applicationMapper.selectById(appId);
        String appName = app != null ? app.getName() : "";
        
        // 构建VO列表（需要先过滤，再分页）
        List<RoleDepartmentListVO> allVoList = new ArrayList<>();
        for (AppRoleDepartment roleDept : pageResult.getRecords()) {
            Department dept = departments.stream()
                    .filter(d -> d.getId().equals(roleDept.getDepartmentId()))
                    .findFirst()
                    .orElse(null);
            
            if (dept != null) {
                // 应用查询条件过滤（由于数据库查询已过滤，这里只需要检查是否匹配）
                boolean match = true;
                
                if (match && StringUtils.hasText(name) && !"undefined".equals(name)) {
                    match = dept.getName() != null && dept.getName().contains(name.trim());
                }
                
                if (match) {
                    RoleDepartmentListVO vo = new RoleDepartmentListVO();
                    vo.setId(roleDept.getId());
                    vo.setAppName(appName);
                    vo.setRoleName(role.getName());
                    vo.setDepartmentId(dept.getId());
                    vo.setDepartmentName(dept.getName());
                    
                    // TODO: 暂时注释掉部门人数统计，因为接口太慢，后续优化
                    // 查询部门及其所有子部门下的用户数量
                    // List<Long> deptAndChildrenIds = departmentService.getDepartmentAndChildrenIds(dept.getId());
                    // LambdaQueryWrapper<NormalUser> userWrapper = new LambdaQueryWrapper<>();
                    // userWrapper.in(NormalUser::getDepartmentId, deptAndChildrenIds);
                    // userWrapper.eq(NormalUser::getStatus, "正常");
                    // Long userCount = normalUserMapper.selectCount(userWrapper);
                    // vo.setUserCount(userCount.intValue());
                    vo.setUserCount(0); // 暂时设置为0，后续优化
                    
                    vo.setCreateTime(roleDept.getCreateTime());
                    allVoList.add(vo);
                }
            }
        }
        
        // 手动分页
        int start = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        int end = Math.min(start + queryDTO.getPageSize(), allVoList.size());
        List<RoleDepartmentListVO> voList = start < allVoList.size() ? allVoList.subList(start, end) : new ArrayList<>();
        
        return PageResult.of(voList, (long) allVoList.size(), queryDTO.getPage(), queryDTO.getPageSize());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignDepartments(Long appId, Long roleId, AssignDepartmentsDTO assignDTO) {
        // 验证角色是否存在且属于该应用
        AppRole role = appRoleMapper.selectById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        if (assignDTO.getDepartmentIds() == null || assignDTO.getDepartmentIds().isEmpty()) {
            throw new BusinessException("部门ID列表不能为空");
        }
        
        // 验证部门是否存在
        LambdaQueryWrapper<Department> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.in(Department::getId, assignDTO.getDepartmentIds());
        List<Department> departments = departmentMapper.selectList(deptWrapper);
        
        if (departments.size() != assignDTO.getDepartmentIds().size()) {
            throw new BusinessException("部分部门不存在");
        }
        
        // 查询已分配的部门
        LambdaQueryWrapper<AppRoleDepartment> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(AppRoleDepartment::getRoleId, roleId);
        existWrapper.in(AppRoleDepartment::getDepartmentId, assignDTO.getDepartmentIds());
        List<AppRoleDepartment> existDepts = this.list(existWrapper);
        
        Set<Long> existDeptIds = existDepts.stream()
                .map(AppRoleDepartment::getDepartmentId)
                .collect(Collectors.toSet());
        
        // 批量插入新分配的部门
        List<AppRoleDepartment> toInsert = new ArrayList<>();
        for (Long deptId : assignDTO.getDepartmentIds()) {
            if (!existDeptIds.contains(deptId)) {
                AppRoleDepartment roleDept = new AppRoleDepartment();
                roleDept.setRoleId(roleId);
                roleDept.setDepartmentId(deptId);
                toInsert.add(roleDept);
            }
        }
        
        if (!toInsert.isEmpty()) {
            this.saveBatch(toInsert);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeDepartment(Long appId, Long roleId, Long departmentId) {
        // 验证角色是否存在且属于该应用
        AppRole role = appRoleMapper.selectById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        // 验证关联记录是否存在且属于该角色
        AppRoleDepartment roleDept = this.getById(departmentId);
        if (roleDept == null || !roleDept.getRoleId().equals(roleId)) {
            throw new BusinessException("关联记录不存在或不属于该角色");
        }
        
        // 删除
        this.removeById(departmentId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRevokeDepartments(Long appId, Long roleId, List<Long> departmentIds) {
        // 验证角色是否存在且属于该应用
        AppRole role = appRoleMapper.selectById(roleId);
        if (role == null || !role.getAppId().equals(appId)) {
            throw new BusinessException("角色不存在或不属于当前应用");
        }
        
        if (departmentIds == null || departmentIds.isEmpty()) {
            throw new BusinessException("部门ID列表不能为空");
        }
        
        // 验证关联记录是否属于该角色
        LambdaQueryWrapper<AppRoleDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppRoleDepartment::getRoleId, roleId);
        wrapper.in(AppRoleDepartment::getId, departmentIds);
        List<AppRoleDepartment> roleDepts = this.list(wrapper);
        
        if (roleDepts.size() != departmentIds.size()) {
            throw new BusinessException("部分关联记录不存在或不属于该角色");
        }
        
        // 批量删除
        this.removeByIds(departmentIds);
    }
}

