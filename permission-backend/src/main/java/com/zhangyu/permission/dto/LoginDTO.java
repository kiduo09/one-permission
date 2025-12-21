package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录请求DTO
 * 
 * @author zhangyu
 */
@Data
public class LoginDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 登录账户
     */
    @NotBlank(message = "登录账户不能为空")
    private String loginAccount;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}

