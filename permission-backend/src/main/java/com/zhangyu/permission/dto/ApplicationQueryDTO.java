package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 应用查询DTO
 * 
 * @author zhangyu
 */
@Data
public class ApplicationQueryDTO implements Serializable {
    
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
     * 应用名称（模糊查询）
     */
    private String name;
    
    /**
     * 应用Key（模糊查询）
     */
    private String appKey;
    
    /**
     * 状态（精确查询：正常/禁用）
     */
    private String status;
}

