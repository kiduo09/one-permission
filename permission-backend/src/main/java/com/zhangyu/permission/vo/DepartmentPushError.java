package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 部门推送错误信息
 * 
 * @author zhangyu
 */
@Data
public class DepartmentPushError implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 部门名称
     */
    private String departmentName;
    
    /**
     * 错误信息
     */
    private String errorMessage;
}

