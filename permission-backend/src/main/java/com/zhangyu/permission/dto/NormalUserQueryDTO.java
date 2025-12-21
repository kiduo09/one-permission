package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 普通用户查询DTO
 * 
 * @author zhangyu
 */
@Data
public class NormalUserQueryDTO implements Serializable {
    
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
     * 工号（模糊查询）
     */
    private String workNo;
    
    /**
     * 姓名（模糊查询）
     */
    private String name;
    
    /**
     * 部门ID（精确查询）
     */
    private Long departmentId;
    
    /**
     * 状态（精确查询：正常/禁用）
     */
    private String status;
}

