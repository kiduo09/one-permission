package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 部门树形视图对象
 * 
 * @author zhangyu
 */
@Data
public class DepartmentTreeVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 部门ID
     */
    private Long id;
    
    /**
     * 部门名称
     */
    private String name;
    
    /**
     * 父部门ID
     */
    private Long parentId;
    
    /**
     * 部门编码
     */
    private String code;
    
    /**
     * 部门层级
     */
    private Integer level;
    
    /**
     * 部门人数（当前部门下的用户数量，不含子部门）
     */
    private Integer userCount;
    
    /**
     * 显示排序
     */
    private Integer sort;
    
    /**
     * 状态：正常/禁用
     */
    private String status;
    
    /**
     * 子部门列表
     */
    private List<DepartmentTreeVO> children;
}

