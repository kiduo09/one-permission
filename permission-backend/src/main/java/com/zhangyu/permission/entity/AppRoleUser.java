package com.zhangyu.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用角色用户关联实体类
 * 
 * @author zhangyu
 */
@Data
@TableName("app_role_users")
public class AppRoleUser implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 应用角色ID
     */
    private Long roleId;
    
    /**
     * 用户工号
     */
    private String userWorkNo;
    
    /**
     * 分配类型：个人/部门
     */
    private String assignType;
    
    /**
     * 部门名称（当分配类型为部门时使用）
     */
    private String department;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

