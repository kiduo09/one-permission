package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 创建应用菜单DTO
 * 
 * @author zhangyu
 */
@Data
public class AppMenuCreateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    private String name;
    
    /**
     * 菜单Key（唯一标识，后端自动生成，格式：menu_key + 32位UUID）
     */
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
    private Integer sort = 0;
    
    /**
     * 是否外链：0-否，1-是
     */
    private Integer isExternal = 0;
    
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
    private String displayStatus = "显示";
    
    /**
     * 菜单状态：正常/停用
     */
    private String status = "正常";
    
    /**
     * 内嵌url
     */
    private String embeddedUrl;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 预留字段1（用户特殊需求）
     */
    private String extField1;
    
    /**
     * 预留字段2（用户特殊需求）
     */
    private String extField2;
    
    /**
     * 预留字段3（用户特殊需求）
     */
    private String extField3;
    
    /**
     * 预留字段4（用户特殊需求）
     */
    private String extField4;
    
    /**
     * 预留字段5（用户特殊需求）
     */
    private String extField5;
    
    /**
     * 预留字段6（用户特殊需求）
     */
    private String extField6;
}

