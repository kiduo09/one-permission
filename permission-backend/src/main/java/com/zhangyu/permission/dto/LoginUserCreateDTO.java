package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 创建登录用户DTO
 * 
 * @author zhangyu
 */
@Data
public class LoginUserCreateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 登录账户
     */
    @NotBlank(message = "登录账户不能为空")
    private String loginAccount;
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 管理员类型：1-普通管理员，2-系统管理员
     */
    private Integer adminType = 1;
    
    /**
     * 状态：正常/禁用
     */
    private String status = "正常";
    
    /**
     * 备注
     */
    private String remark;
}

