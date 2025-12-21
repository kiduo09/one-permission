package com.zhangyu.permission.dto;

import lombok.Data;

/**
 * 查询消费者信息DTO
 * 
 * @author zhangyu
 */
@Data
public class ConsumerInfoQueryDTO {
    
    /**
     * 消费者名称（模糊查询）
     */
    private String consumerName;
    
    /**
     * clientId（精确查询）
     */
    private String clientId;
    
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
    
    /**
     * 当前页码
     */
    private Integer current = 1;
    
    /**
     * 每页数量
     */
    private Integer size = 10;
}

