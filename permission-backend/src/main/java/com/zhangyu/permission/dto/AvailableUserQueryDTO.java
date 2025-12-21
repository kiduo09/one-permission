package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 可选用户查询DTO（用于分配用户弹窗）
 * 
 * @author zhangyu
 */
@Data
public class AvailableUserQueryDTO implements Serializable {
    
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
     * 部门ID（精确查询，用于筛选指定部门的用户）
     */
    private Long departmentId;
    
    /**
     * AD域账号（模糊查询）
     */
    private String adAccount;
    
    /**
     * 用户昵称（模糊查询）
     */
    private String nickname;
    
    /**
     * 工号（模糊查询）
     */
    private String workNo;
}

