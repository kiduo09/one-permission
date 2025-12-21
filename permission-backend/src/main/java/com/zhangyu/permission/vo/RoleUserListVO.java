package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色用户列表视图对象
 * 
 * @author zhangyu
 */
@Data
public class RoleUserListVO implements Serializable {
    
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
     * 工号
     */
    private String workNo;
    
    /**
     * AD域账号
     */
    private String adAccount;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 手机号
     */
    private String mobile;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

