package com.zhangyu.permission.service;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.AppRoleCreateDTO;
import com.zhangyu.permission.dto.AppRoleQueryDTO;
import com.zhangyu.permission.dto.AppRoleUpdateDTO;
import com.zhangyu.permission.entity.AppRole;
import com.zhangyu.permission.vo.AppRoleListVO;

/**
 * 应用角色服务接口
 * 
 * @author zhangyu
 */
public interface AppRoleService {
    
    /**
     * 分页查询应用角色列表
     * 
     * @param appId 应用ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<AppRoleListVO> pageQuery(Long appId, AppRoleQueryDTO queryDTO);
    
    /**
     * 根据ID查询角色详情
     * 
     * @param appId 应用ID
     * @param id 角色ID
     * @return 角色信息
     */
    AppRole getById(Long appId, Long id);
    
    /**
     * 创建角色
     * 
     * @param appId 应用ID
     * @param createDTO 创建信息
     * @return 角色信息
     */
    AppRole create(Long appId, AppRoleCreateDTO createDTO);
    
    /**
     * 更新角色
     * 
     * @param appId 应用ID
     * @param id 角色ID
     * @param updateDTO 更新信息
     * @return 角色信息
     */
    AppRole update(Long appId, Long id, AppRoleUpdateDTO updateDTO);
    
    /**
     * 删除角色
     * 
     * @param appId 应用ID
     * @param id 角色ID
     */
    void delete(Long appId, Long id);
}

