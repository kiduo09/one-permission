package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * Token请求DTO
 * 
 * @author zhangyu
 */
@Data
public class TokenRequestDTO {
    
    /**
     * clientId
     */
    @NotBlank(message = "clientId不能为空")
    private String clientId;
    
    /**
     * clientSecret
     */
    @NotBlank(message = "clientSecret不能为空")
    private String clientSecret;
}

