package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录结果视图对象
 * 
 * @author zhangyu
 */
@Data
public class LoginResultVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Token
     */
    private String token;
    
    /**
     * 用户信息
     */
    private LoginUserVO userInfo;
}

