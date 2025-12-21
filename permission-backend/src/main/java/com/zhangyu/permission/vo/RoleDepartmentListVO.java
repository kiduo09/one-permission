package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色部门列表视图对象
 * 
 * @author zhangyu
 */
@Data
public class RoleDepartmentListVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 关联ID
     */
    private Long id;
    
    /**
     * 应用系统名称
     */
    private String appName;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 部门ID
     */
    private Long departmentId;
    
    /**
     * 部门名称
     */
    private String departmentName;
    
    /**
     * 用户数量
     */
    private Integer userCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

