package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录用户查询DTO
 * 
 * @author zhangyu
 */
@Data
public class LoginUserQueryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 页码（默认1）
     */
    private Integer page = 1;
    
    /**
     * 每页数量（默认10）
     */
    private Integer pageSize = 10;
    
    /**
     * 登录账户（模糊查询）
     */
    private String loginAccount;
    
    /**
     * 姓名（模糊查询）
     */
    private String name;
    
    /**
     * 状态（精确查询：正常/禁用）
     */
    private String status;
}

