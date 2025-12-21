package com.zhangyu.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.entity.Department;
import com.zhangyu.permission.mapper.DepartmentMapper;
import com.zhangyu.permission.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门服务实现类
 * 
 * @author zhangyu
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    
    @Override
    public List<Long> getDepartmentAndChildrenIds(Long departmentId) {
        List<Long> result = new ArrayList<>();
        if (departmentId == null) {
            return result;
        }
        
        // 添加自身
        result.add(departmentId);
        
        // 递归查询所有子部门
        collectChildrenIds(departmentId, result);
        
        return result;
    }
    
    /**
     * 递归收集子部门ID
     */
    private void collectChildrenIds(Long parentId, List<Long> result) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getParentId, parentId);
        List<Department> children = this.list(wrapper);
        
        for (Department child : children) {
            result.add(child.getId());
            // 递归查询子部门的子部门
            collectChildrenIds(child.getId(), result);
        }
    }
}

