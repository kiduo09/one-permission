package com.zhangyu.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用角色实体类
 * 
 * @author zhangyu
 */
@Data
@TableName("app_roles")
public class AppRole implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 应用ID
     */
    private Long appId;
    
    /**
     * 系统ID
     */
    private String systemId;
    
    /**
     * 角色名称
     */
    private String name;
    
    /**
     * 显示顺序
     */
    private Integer sort;
    
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

