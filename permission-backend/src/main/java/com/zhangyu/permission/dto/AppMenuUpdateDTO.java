package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 更新应用菜单DTO
 * 
 * @author zhangyu
 */
@Data
public class AppMenuUpdateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    private String name;
    
    /**
     * 菜单Key（唯一标识）
     */
    @NotBlank(message = "菜单Key不能为空")
    private String menuKey;
    
    /**
     * 父菜单ID（NULL表示顶级菜单）
     */
    private Long parentId;
    
    /**
     * 菜单类型：目录/菜单/按钮
     */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;
    
    /**
     * 菜单图标
     */
    private String icon;
    
    /**
     * 菜单图标（激活状态）
     */
    private String iconActive;
    
    /**
     * 显示排序
     */
    private Integer sort;
    
    /**
     * 是否外链：0-否，1-是
     */
    private Integer isExternal;
    
    /**
     * 路由地址
     */
    private String route;
    
    /**
     * 组件路径
     */
    private String component;
    
    /**
     * 权限标识（按钮类型使用）
     */
    private String permission;
    
    /**
     * 显示状态：显示/隐藏
     */
    private String displayStatus;
    
    /**
     * 菜单状态：正常/停用
     */
    private String status;
    
    /**
     * 内嵌url
     */
    private String embeddedUrl;
    
    /**
     * 备注
     */
    private String remark;
}

