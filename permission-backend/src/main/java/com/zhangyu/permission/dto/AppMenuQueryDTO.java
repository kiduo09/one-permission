package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 应用菜单查询DTO
 * 
 * @author zhangyu
 */
@Data
public class AppMenuQueryDTO implements Serializable {
    
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
     * 菜单名称（模糊查询）
     */
    private String name;
    
    /**
     * 菜单Key（模糊查询）
     */
    private String menuKey;
    
    /**
     * 菜单类型（精确查询：目录/菜单/按钮）
     */
    private String menuType;
    
    /**
     * 状态（精确查询：正常/停用）
     */
    private String status;
}

