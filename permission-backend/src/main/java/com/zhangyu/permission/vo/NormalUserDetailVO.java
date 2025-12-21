package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 普通用户详情视图对象
 * 
 * @author zhangyu
 */
@Data
public class NormalUserDetailVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 工号
     */
    private String workNo;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 部门ID
     */
    private Long departmentId;
    
    /**
     * 部门名称
     */
    private String departmentName;
    
    /**
     * AD域账号
     */
    private String adAccount;
    
    /**
     * 手机号码
     */
    private String mobile;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 职级
     */
    private String level;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

