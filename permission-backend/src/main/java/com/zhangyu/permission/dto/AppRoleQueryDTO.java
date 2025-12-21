package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 应用角色查询DTO
 * 
 * @author zhangyu
 */
@Data
public class AppRoleQueryDTO implements Serializable {
    
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
     * 系统ID（模糊查询）
     */
    private String systemId;
    
    /**
     * 角色名称（模糊查询）
     */
    private String name;
    
    /**
     * 状态（精确查询：正常/禁用）
     */
    private String status;
}

