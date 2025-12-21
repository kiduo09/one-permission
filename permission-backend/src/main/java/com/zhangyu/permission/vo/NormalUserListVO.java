package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 普通用户列表视图对象
 * 
 * @author zhangyu
 */
@Data
public class NormalUserListVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
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
     * AD域账号
     */
    private String adAccount;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String mobile;
    
    /**
     * 部门名称（通过departmentId关联查询）
     */
    private String departmentName;
    
    /**
     * 状态
     */
    private String status;
}

