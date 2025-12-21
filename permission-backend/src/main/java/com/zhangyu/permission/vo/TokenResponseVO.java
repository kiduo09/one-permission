package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * Token响应VO
 * 
 * @author zhangyu
 */
@Data
public class TokenResponseVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 过期时间（秒）
     */
    private Long expiresIn;
    
    /**
     * 消费者名称
     */
    private String consumerName;
}

