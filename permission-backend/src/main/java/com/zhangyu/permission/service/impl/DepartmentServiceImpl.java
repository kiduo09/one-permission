package com.zhangyu.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.entity.Department;
import com.zhangyu.permission.mapper.DepartmentMapper;
import com.zhangyu.permission.service.DepartmentService;
import com.zhangyu.permission.vo.DepartmentTreeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 * 
 * @author zhangyu
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    /**
     * 使用 ancestors 字段获取当前部门及其所有子部门ID
     * 说明：
     * - 当前部门：id = departmentId
     * - 子部门：ancestors 字段中包含当前部门ID（精确匹配，避免误匹配）
     */
    @Override
    public List<Long> getDepartmentAndChildrenIds(Long departmentId) {
        List<Long> result = new ArrayList<>();
        if (departmentId == null) {
            return result;
        }
        
        // 添加自身
        result.add(departmentId);

        // 查询所有 ancestors 中包含当前部门ID 的子部门
        // 使用精确匹配，避免误匹配（例如：1 和 11）
        // ancestors 格式：空字符串 或 "1" 或 "1,2" 或 "1,2,3"
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        String deptIdStr = String.valueOf(departmentId);
        // 匹配规则（使用 FIND_IN_SET 函数，MySQL 支持）：
        // FIND_IN_SET(deptIdStr, ancestors) > 0
        // 这样可以精确匹配逗号分隔的ID，避免误匹配
        wrapper.apply("FIND_IN_SET({0}, ancestors) > 0", deptIdStr);
        List<Department> children = this.list(wrapper);
        for (Department dept : children) {
            result.add(dept.getId());
        }
        
        return result;
    }

    @Override
    public void refreshAncestors(Long departmentId) {
        if (departmentId == null) {
            return;
        }
        Department dept = this.getById(departmentId);
        if (dept == null) {
            return;
        }

        // 顶级部门：没有父ID，ancestors 置为""
        if (dept.getParentId() == null) {
            dept.setAncestors("");
            this.updateById(dept);
            return;
        }

        // 非顶级部门：父级的 ancestors + 父ID 组成新的 ancestors
        Department parent = this.getById(dept.getParentId());
        String parentAncestors = parent != null ? parent.getAncestors() : "";
        String newAncestors;
        if (parentAncestors == null || parentAncestors.trim().isEmpty()) {
            newAncestors = String.valueOf(dept.getParentId());
        } else {
            newAncestors = parentAncestors + "," + dept.getParentId();
        }
        dept.setAncestors(newAncestors);
        this.updateById(dept);
    }
    
    @Override
    public List<DepartmentTreeVO> getDepartmentTree() {
        // 查询所有正常状态的部门
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getStatus, "正常");
        wrapper.orderByAsc(Department::getSort);
        List<Department> allDepartments = this.list(wrapper);
        
        // 转换为VO
        List<DepartmentTreeVO> voList = allDepartments.stream().map(dept -> {
            DepartmentTreeVO vo = new DepartmentTreeVO();
            BeanUtils.copyProperties(dept, vo);
            return vo;
        }).collect(Collectors.toList());
        
        // 构建树形结构
        Map<Long, DepartmentTreeVO> voMap = new HashMap<>();
        for (DepartmentTreeVO vo : voList) {
            voMap.put(vo.getId(), vo);
        }
        
        List<DepartmentTreeVO> rootList = new ArrayList<>();
        for (DepartmentTreeVO vo : voList) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                // 顶级部门
                rootList.add(vo);
            } else {
                // 子部门
                DepartmentTreeVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }
        
        // 递归排序
        for (DepartmentTreeVO root : rootList) {
            sortChildren(root);
        }
        
        return rootList;
    }
    
    /**
     * 递归排序子部门
     */
    private void sortChildren(DepartmentTreeVO dept) {
        if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
            dept.getChildren().sort(Comparator.comparing(DepartmentTreeVO::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
            for (DepartmentTreeVO child : dept.getChildren()) {
                sortChildren(child);
            }
        }
    }
}

