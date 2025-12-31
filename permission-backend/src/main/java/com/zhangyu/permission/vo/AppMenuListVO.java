package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用菜单列表视图对象
 * 
 * @author zhangyu
 */
@Data
public class AppMenuListVO implements Serializable {
    
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
     * 显示排序
     */
    private Integer sort;
    
    /**
     * 路由地址
     */
    private String route;
    
    /**
     * 组件路径
     */
    private String component;
    
    /**
     * 菜单类型：目录/菜单/按钮
     */
    private String menuType;
    
    /**
     * 权限标识（按钮类型使用）
     */
    private String permission;
    
    /**
     * 菜单状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

