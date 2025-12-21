package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分配菜单DTO
 * 
 * @author zhangyu
 */
@Data
public class AssignMenusDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 菜单ID列表
     */
    private List<Long> menuIds;
}

