package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用列表视图对象
 * 
 * @author zhangyu
 */
@Data
public class ApplicationListVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 应用ID
     */
    private Long id;
    
    /**
     * 应用名称
     */
    private String name;
    
    /**
     * 应用Key
     */
    private String appKey;
    
    /**
     * 客户端ID（用于对外接口认证）
     */
    private String clientId;
    
    /**
     * 客户端密钥（用于对外接口认证）
     */
    private String clientSecret;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 状态
     */
    private String status;
}

