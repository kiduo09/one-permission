package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录用户视图对象
 * 
 * @author zhangyu
 */
@Data
public class LoginUserVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 登录账户
     */
    private String loginAccount;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 管理员类型：1-普通管理员，2-系统管理员
     */
    private Integer adminType;
}

