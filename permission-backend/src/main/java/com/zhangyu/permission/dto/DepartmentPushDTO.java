package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 部门推送DTO（用于外部接口推送部门数据）
 * 
 * @author zhangyu
 */
@Data
public class DepartmentPushDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 部门ID（可选，如果提供则使用该ID，否则由数据库自动生成）
     */
    private Long id;
    
    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String name;
    
    /**
     * 父部门ID（NULL表示顶级部门）
     */
    private Long parentId;
    
    /**
     * 部门编码
     */
    @NotBlank(message = "部门code不能为空")
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
    private String status = "正常";
    
    /**
     * 备注
     */
    private String remark;
}

