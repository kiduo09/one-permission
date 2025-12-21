package com.zhangyu.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.AppMenuCreateDTO;
import com.zhangyu.permission.dto.AppMenuQueryDTO;
import com.zhangyu.permission.dto.AppMenuUpdateDTO;
import com.zhangyu.permission.entity.AppMenu;
import com.zhangyu.permission.vo.AppMenuListVO;
import com.zhangyu.permission.vo.AppMenuTreeVO;

import java.util.List;

/**
 * 应用菜单服务接口
 * 
 * @author zhangyu
 */
public interface AppMenuService extends IService<AppMenu> {
    
    /**
     * 查询应用菜单树形列表
     * 
     * @param appId 应用ID
     * @param status 状态（可选）
     * @return 树形菜单列表
     */
    List<AppMenuTreeVO> getMenuTree(Long appId, String status);
    
    /**
     * 分页查询应用菜单列表
     * 
     * @param appId 应用ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<AppMenuListVO> pageQuery(Long appId, AppMenuQueryDTO queryDTO);
    
    /**
     * 创建应用菜单
     * 
     * @param appId 应用ID
     * @param createDTO 创建信息
     * @return 菜单信息
     */
    AppMenu create(Long appId, AppMenuCreateDTO createDTO);
    
    /**
     * 更新应用菜单
     * 
     * @param appId 应用ID
     * @param id 菜单ID
     * @param updateDTO 更新信息
     * @return 菜单信息
     */
    AppMenu update(Long appId, Long id, AppMenuUpdateDTO updateDTO);
    
    /**
     * 删除应用菜单
     * 
     * @param appId 应用ID
     * @param id 菜单ID
     */
    void delete(Long appId, Long id);
}

