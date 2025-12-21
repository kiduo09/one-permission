package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 创建消费者信息DTO
 * 
 * @author zhangyu
 */
@Data
public class ConsumerInfoCreateDTO {
    
    /**
     * 消费者名称
     */
    @NotBlank(message = "消费者名称不能为空")
    @Size(max = 50, message = "消费者名称长度不能超过50")
    private String consumerName;
    
    /**
     * clientId（自动生成，不需要传入）
     */
    private String clientId;
    
    /**
     * clientSecret（自动生成，不需要传入）
     */
    private String clientSecret;
    
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status = 1;
}

