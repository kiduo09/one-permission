package com.zhangyu.permission.service;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.AssignDepartmentsDTO;
import com.zhangyu.permission.dto.RoleDepartmentQueryDTO;
import com.zhangyu.permission.vo.RoleDepartmentListVO;

import java.util.List;

/**
 * 应用角色部门服务接口
 * 
 * @author zhangyu
 */
public interface AppRoleDepartmentService {
    
    /**
     * 分页查询角色的部门分配列表
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<RoleDepartmentListVO> pageQueryDepartments(Long appId, Long roleId, RoleDepartmentQueryDTO queryDTO);
    
    /**
     * 分配部门给角色
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param assignDTO 分配信息
     */
    void assignDepartments(Long appId, Long roleId, AssignDepartmentsDTO assignDTO);
    
    /**
     * 取消部门授权
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param departmentId 部门ID（关联表ID）
     */
    void revokeDepartment(Long appId, Long roleId, Long departmentId);
    
    /**
     * 批量取消部门授权
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param departmentIds 部门ID列表（关联表ID）
     */
    void batchRevokeDepartments(Long appId, Long roleId, List<Long> departmentIds);
}

