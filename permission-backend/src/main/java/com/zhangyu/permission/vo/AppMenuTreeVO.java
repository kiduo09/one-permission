package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用菜单树形视图对象
 * 
 * @author zhangyu
 */
@Data
public class AppMenuTreeVO implements Serializable {
    
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
     * 菜单Key（唯一标识）
     */
    private String menuKey;
    
    /**
     * 父菜单ID
     */
    private Long parentId;
    
    /**
     * 菜单类型
     */
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
     * 是否外链
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
     * 权限标识
     */
    private String permission;
    
    /**
     * 显示状态
     */
    private String displayStatus;
    
    /**
     * 菜单状态
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 子菜单列表
     */
    private List<AppMenuTreeVO> children;
}

