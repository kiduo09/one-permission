package com.zhangyu.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用菜单实体类
 * 
 * @author zhangyu
 */
@Data
@TableName("app_menus")
public class AppMenu implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 应用ID
     */
    private Long appId;
    
    /**
     * 菜单名称
     */
    private String name;
    
    /**
     * 菜单Key（唯一标识）
     */
    @com.baomidou.mybatisplus.annotation.TableField("menu_key")
    private String menuKey;
    
    /**
     * 父菜单ID（NULL表示顶级菜单）
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

