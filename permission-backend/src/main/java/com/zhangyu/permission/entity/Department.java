package com.zhangyu.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部门实体类
 * 
 * @author zhangyu
 */
@Data
@TableName("departments")
public class Department implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 部门名称
     */
    private String name;
    
    /**
     * 父部门ID（NULL表示顶级部门）
     */
    private Long parentId;
    
    /**
     * 祖级列表（从顶级到当前部门的ID路径，逗号分隔）
     * 例如：顶级部门 ancestors = ""；子部门 ancestors = "1,3"
     */
    private String ancestors;
    
    /**
     * 部门编码
     */
    private String code;
    
    /**
     * 部门层级（1为顶级）
     */
    private Integer level;
    
    /**
     * 显示排序
     */
    private Integer sort;
    
    /**
     * 部门负责人工号
     */
    private String leaderWorkNo;
    
    /**
     * 状态：正常/禁用
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

