package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用角色列表视图对象
 * 
 * @author zhangyu
 */
@Data
public class AppRoleListVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 角色ID
     */
    private Long id;
    
    /**
     * 应用ID
     */
    private Long appId;
    
    /**
     * 系统ID
     */
    private String systemId;
    
    /**
     * 关联应用名称
     */
    private String appName;
    
    /**
     * 角色名称
     */
    private String name;
    
    /**
     * 显示顺序
     */
    private Integer sort;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

