package com.zhangyu.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyu.permission.entity.Department;

import java.util.List;

/**
 * 部门服务接口
 * 
 * @author zhangyu
 */
public interface DepartmentService extends IService<Department> {
    
    /**
     * 递归查询部门及其所有子部门的ID列表
     * 
     * @param departmentId 部门ID
     * @return 部门ID列表（包含自身和所有子部门）
     */
    List<Long> getDepartmentAndChildrenIds(Long departmentId);
}

