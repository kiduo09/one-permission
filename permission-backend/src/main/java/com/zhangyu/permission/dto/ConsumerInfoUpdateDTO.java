package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 更新消费者信息DTO
 * 
 * @author zhangyu
 */
@Data
public class ConsumerInfoUpdateDTO {
    
    /**
     * 主键ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;
    
    /**
     * 消费者名称
     */
    @NotBlank(message = "消费者名称不能为空")
    @Size(max = 50, message = "消费者名称长度不能超过50")
    private String consumerName;
    
    /**
     * clientSecret（可选，为空则不修改）
     */
    @Size(max = 100, message = "clientSecret长度不能超过100")
    private String clientSecret;
    
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}

