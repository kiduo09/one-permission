package com.zhangyu.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录用户实体类
 * 
 * @author zhangyu
 */
@Data
@TableName("login_users")
public class LoginUser implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 登录账户
     */
    private String loginAccount;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 管理员类型：1-普通管理员，2-系统管理员
     */
    private Integer adminType;
    
    /**
     * 状态：正常/禁用
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

