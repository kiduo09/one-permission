package com.zhangyu.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用实体类
 * 
 * @author zhangyu
 */
@Data
@TableName("applications")
public class Application implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 应用名称
     */
    private String name;
    
    /**
     * 应用Key（唯一标识）
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
     * 状态：正常/禁用
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

