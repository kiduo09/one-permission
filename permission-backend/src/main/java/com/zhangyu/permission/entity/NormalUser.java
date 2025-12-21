package com.zhangyu.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 普通用户实体类（业务用户）
 * 
 * @author zhangyu
 */
@Data
@TableName("normal_users")
public class NormalUser implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 工号
     */
    private String workNo;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 主部门ID
     */
    private Long departmentId;
    
    /**
     * AD域账号
     */
    private String adAccount;
    
    /**
     * 手机号码
     */
    private String mobile;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 职级
     */
    private String level;
    
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

