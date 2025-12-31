package com.zhangyu.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.entity.Department;
import com.zhangyu.permission.entity.NormalUser;
import com.zhangyu.permission.mapper.DepartmentMapper;
import com.zhangyu.permission.mapper.NormalUserMapper;
import com.zhangyu.permission.service.DepartmentService;
import com.zhangyu.permission.vo.DepartmentTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    
    @Autowired
    private NormalUserMapper normalUserMapper;

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
        refreshAncestors(departmentId, 0);
    }
    
    /**
     * 刷新单个部门的ancestors字段
     * 使用递归向上查找父部门的方式构建 ancestors
     * 
     * @param departmentId 部门ID
     */
    private void refreshAncestors(Long departmentId, int depth) {
        if (departmentId == null) {
            return;
        }
        Department dept = this.getById(departmentId);
        if (dept == null) {
            return;
        }
        
        // 构建部门ID到部门的映射（只包含当前部门及其父部门链）
        Map<Long, Department> deptMap = new HashMap<>();
        buildDeptMap(dept, deptMap, 0);
        
        // 递归向上查找父部门，构建 ancestors
        StringBuilder ancestorsBuilder = new StringBuilder();
        int level = findParentDept(dept, deptMap, ancestorsBuilder, 0);
        
        // 去掉最后一个逗号
        String ancestors = ancestorsBuilder.length() > 0 
            ? ancestorsBuilder.substring(0, ancestorsBuilder.length() - 1) 
            : "";
        
        // 更新部门信息
        dept.setAncestors(ancestors);
        dept.setLevel(level);
        this.updateById(dept);
        
        log.debug("更新部门 ancestors 成功，部门ID: {}，部门名称: {}，ancestors: {}，level: {}", 
                departmentId, dept.getName(), ancestors, level);
    }
    
    /**
     * 构建部门映射（包含当前部门及其所有父部门）
     * 
     * @param dept 当前部门
     * @param deptMap 部门映射
     * @param depth 当前递归深度（防止死循环）
     */
    private void buildDeptMap(Department dept, Map<Long, Department> deptMap, int depth) {
        // 保险机制：最多递归10次，防止死循环
        if (depth > 10) {
            return;
        }
        
        if (dept == null || dept.getId() == null) {
            return;
        }
        
        // 如果已经添加过，跳过（防止循环引用）
        if (deptMap.containsKey(dept.getId())) {
            return;
        }
        
        deptMap.put(dept.getId(), dept);
        
        // 递归添加父部门
        if (dept.getParentId() != null) {
            Department parent = this.getById(dept.getParentId());
            if (parent != null) {
                buildDeptMap(parent, deptMap, depth + 1);
            }
        }
    }
    
    @Override
    public void refreshAllAncestors() {
        // 1. 查询所有部门
        List<Department> allDepartments = this.list();
        
        if (allDepartments == null || allDepartments.isEmpty()) {
            log.info("没有部门需要更新 ancestors");
            return;
        }
        
        log.info("开始统一更新所有部门的 ancestors，共 {} 个部门", allDepartments.size());
        
        // 2. 构建部门ID到部门的映射，方便查找
        Map<Long, Department> deptMap = new HashMap<>();
        for (Department dept : allDepartments) {
            deptMap.put(dept.getId(), dept);
        }
        
        // 3. 遍历所有部门，递归向上查找父部门，构建 ancestors
        int totalProcessed = 0;
        int totalFailed = 0;
        
        for (Department dept : allDepartments) {
            try {
                // 递归向上查找所有父部门，构建 ancestors
                StringBuilder ancestorsBuilder = new StringBuilder();
                int level = findParentDept(dept, deptMap, ancestorsBuilder, 0);
                
                // 去掉最后一个逗号
                String ancestors = ancestorsBuilder.length() > 0 
                    ? ancestorsBuilder.substring(0, ancestorsBuilder.length() - 1) 
                    : "";
                
                // 更新部门信息
                dept.setAncestors(ancestors);
                dept.setLevel(level);
                this.updateById(dept);
                
                totalProcessed++;
                log.debug("更新部门 ancestors 成功，部门ID: {}，部门名称: {}，ancestors: {}，level: {}", 
                        dept.getId(), dept.getName(), ancestors, level);
            } catch (Exception e) {
                totalFailed++;
                log.error("更新部门 ancestors 失败，部门ID: {}，部门名称: {}，错误: {}", 
                        dept.getId(), dept.getName(), e.getMessage(), e);
                // 继续处理其他部门，不中断整个流程
            }
        }
        
        log.info("统一更新所有部门的 ancestors 完成，共处理 {} 个部门，成功: {}，失败: {}", 
                allDepartments.size(), totalProcessed, totalFailed);
    }
    
    /**
     * 递归向上查找父部门，构建 ancestors 字符串
     * 参考：通过递归向上查找，将父部门ID按顺序（从顶级到直接父部门）追加到 ancestors 中
     * 
     * @param dept 当前部门
     * @param deptMap 部门ID到部门的映射
     * @param ancestorsBuilder ancestors 字符串构建器
     * @param depth 当前递归深度（防止死循环，最多递归10次）
     * @return 部门层级
     */
    private int findParentDept(Department dept, Map<Long, Department> deptMap, 
                               StringBuilder ancestorsBuilder, int depth) {
        // 保险机制：最多递归10次，防止死循环
        if (depth > 10) {
            log.warn("查找父部门时检测到可能的循环引用，部门ID: {}，返回层级1", dept.getId());
            return 1;
        }
        
        // 检查循环引用：如果父部门ID是当前部门ID，则报错
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getId())) {
            throw new RuntimeException("检测到循环引用：部门ID " + dept.getId() + " 的父部门是自己");
        }
        
        // 顶级部门：没有父ID，ancestors 为空，层级为1
        if (dept.getParentId() == null) {
            return 1;
        }
        
        // 检查父部门是否存在
        Department parent = deptMap.get(dept.getParentId());
        if (parent == null) {
            log.warn("部门ID {} 的父部门不存在（ID: {}），设置为顶级部门", dept.getId(), dept.getParentId());
            return 1;
        }
        
        // 检查父部门的 ancestors 中是否包含当前部门ID（防止循环引用）
        String parentAncestors = parent.getAncestors();
        if (parentAncestors != null && !parentAncestors.trim().isEmpty()) {
            String[] ancestorIds = parentAncestors.split(",");
            for (String ancestorId : ancestorIds) {
                try {
                    Long ancestorIdLong = Long.parseLong(ancestorId.trim());
                    if (ancestorIdLong.equals(dept.getId())) {
                        throw new RuntimeException("检测到循环引用：部门ID " + dept.getId() + " 的祖先链中包含自己");
                    }
                } catch (NumberFormatException e) {
                    // 忽略格式错误
                }
            }
        }
        
        // 递归向上查找父部门的父部门
        int parentLevel = findParentDept(parent, deptMap, ancestorsBuilder, depth + 1);
        
        // 将父部门ID追加到 ancestors 中（从顶级到直接父部门的顺序）
        ancestorsBuilder.append(dept.getParentId()).append(",");
        
        // 当前部门层级 = 父部门层级 + 1
        int currentLevel = parentLevel + 1;
        if (currentLevel > 10) {
            throw new RuntimeException("部门层级不能超过10级，当前层级: " + currentLevel + "，部门ID: " + dept.getId());
        }
        
        return currentLevel;
    }
    
    @Override
    public List<DepartmentTreeVO> getDepartmentTree() {
        // 查询所有正常状态的部门
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getStatus, "正常");
        wrapper.orderByAsc(Department::getSort);
        List<Department> allDepartments = this.list(wrapper);
        
        // 查询每个部门下的用户数量（当前部门，不含子部门）
        LambdaQueryWrapper<NormalUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(NormalUser::getStatus, "正常");
        List<NormalUser> allUsers = normalUserMapper.selectList(userWrapper);
        Map<Long, Long> userCountMap = allUsers.stream()
                .filter(user -> user.getDepartmentId() != null)
                .collect(Collectors.groupingBy(NormalUser::getDepartmentId, Collectors.counting()));
        
        // 转换为VO
        List<DepartmentTreeVO> voList = allDepartments.stream().map(dept -> {
            DepartmentTreeVO vo = new DepartmentTreeVO();
            BeanUtils.copyProperties(dept, vo);
            Long count = userCountMap.get(dept.getId());
            vo.setUserCount(count != null ? count.intValue() : 0);
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

