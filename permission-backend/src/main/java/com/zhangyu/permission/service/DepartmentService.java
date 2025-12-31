package com.zhangyu.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyu.permission.entity.Department;
import com.zhangyu.permission.vo.DepartmentTreeVO;

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
    
    /**
     * 根据部门ID计算并更新该部门的 ancestors 字段
     * 一般在新增或修改部门父级关系时调用
     * 
     * @param departmentId 部门ID
     */
    void refreshAncestors(Long departmentId);
    
    /**
     * 从顶级部门开始，递归更新所有部门的 ancestors 字段
     * 用于批量插入部门后统一更新 ancestors
     */
    void refreshAllAncestors();
    
    /**
     * 获取部门树形结构
     * 
     * @return 部门树列表
     */
    List<DepartmentTreeVO> getDepartmentTree();
}

