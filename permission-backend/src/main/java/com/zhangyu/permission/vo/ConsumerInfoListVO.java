package com.zhangyu.permission.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消费者信息列表VO
 * 
 * @author zhangyu
 */
@Data
public class ConsumerInfoListVO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 消费者名称
     */
    private String consumerName;
    
    /**
     * clientId
     */
    private String clientId;
    
    /**
     * clientSecret
     */
    private String clientSecret;
    
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
    
    /**
     * 状态文本
     */
    private String statusText;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

