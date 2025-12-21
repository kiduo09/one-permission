package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 更新应用角色DTO
 * 
 * @author zhangyu
 */
@Data
public class AppRoleUpdateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 系统ID
     */
    @NotBlank(message = "系统ID不能为空")
    private String systemId;
    
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
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
     * 菜单ID列表（用于绑定菜单）
     */
    private List<Long> menuIds;
}

