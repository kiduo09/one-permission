package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 角色用户查询DTO
 * 
 * @author zhangyu
 */
@Data
public class RoleUserQueryDTO implements Serializable {
    
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
     * AD域账号（模糊查询）
     */
    private String adAccount;
    
    /**
     * 工号（模糊查询）
     */
    private String workNo;
    
    /**
     * 手机号（模糊查询）
     */
    private String mobile;
}

