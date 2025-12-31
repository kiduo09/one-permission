package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 普通用户推送DTO（用于外部接口推送用户数据）
 * 
 * @author zhangyu
 */
@Data
public class NormalUserPushDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空")
    private String workNo;
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
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
    private String status = "正常";
}

