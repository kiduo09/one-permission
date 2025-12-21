package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单权限VO
 * 
 * @author zhangyu
 */
@Data
public class MenuPermissionVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 菜单ID
     */
    private Long id;
    
    /**
     * 菜单名称
     */
    private String name;
    
    /**
     * 菜单Key
     */
    private String menuKey;
    
    /**
     * 父菜单ID
     */
    private Long parentId;
    
    /**
     * 菜单类型：目录/菜单/按钮
     */
    private String menuType;
    
    /**
     * 菜单图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否外链
     */
    private Boolean isExternal;
    
    /**
     * 路由地址
     */
    private String route;
    
    /**
     * 组件路径
     */
    private String component;
    
    /**
     * 权限标识
     */
    private String permission;
    
    /**
     * 显示状态：显示/隐藏
     */
    private String displayStatus;
    
    /**
     * 状态：正常/停用
     */
    private String status;
    
    /**
     * 内嵌URL
     */
    private String embeddedUrl;
    
    /**
     * 子菜单列表
     */
    private List<MenuPermissionVO> children;
}

