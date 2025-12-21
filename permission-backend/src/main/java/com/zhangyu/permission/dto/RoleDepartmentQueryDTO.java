package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 角色部门查询DTO
 * 
 * @author zhangyu
 */
@Data
public class RoleDepartmentQueryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 页码（默认1）
     */
    private Integer page = 1;
    
    /**
     * 每页数量（默认10）
     */
    private Integer pageSize = 10;
    
    /**
     * 部门名称（模糊查询）
     */
    private String name;
}

