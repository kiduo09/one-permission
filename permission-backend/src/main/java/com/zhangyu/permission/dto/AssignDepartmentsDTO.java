package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分配部门DTO
 * 
 * @author zhangyu
 */
@Data
public class AssignDepartmentsDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 部门ID列表
     */
    private List<Long> departmentIds;
}

