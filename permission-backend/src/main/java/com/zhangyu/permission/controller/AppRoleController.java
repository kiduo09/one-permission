package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.AppRoleCreateDTO;
import com.zhangyu.permission.dto.AppRoleQueryDTO;
import com.zhangyu.permission.dto.AppRoleUpdateDTO;
import com.zhangyu.permission.entity.AppRole;
import com.zhangyu.permission.service.AppRoleService;
import com.zhangyu.permission.service.impl.AppRoleServiceImpl;
import com.zhangyu.permission.vo.AppRoleListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用角色管理控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/applications/{appId}/roles")
public class AppRoleController {
    
    @Autowired
    private AppRoleService appRoleService;
    
    /**
     * 分页查询应用角色列表
     * 
     * @param appId 应用ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<AppRoleListVO>> pageQuery(@PathVariable Long appId, 
                                                        AppRoleQueryDTO queryDTO) {
        log.info("分页查询应用角色列表，应用ID：{}，查询条件：{}", appId, queryDTO);
        PageResult<AppRoleListVO> result = appRoleService.pageQuery(appId, queryDTO);
        return Result.success(result);
    }
    
    /**
     * 根据ID查询角色详情
     * 
     * @param appId 应用ID
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    public Result<AppRole> getById(@PathVariable Long appId, @PathVariable Long id) {
        log.info("查询应用角色详情，应用ID：{}，角色ID：{}", appId, id);
        AppRole role = appRoleService.getById(appId, id);
        return Result.success(role);
    }
    
    /**
     * 新增角色
     * 
     * @param appId 应用ID
     * @param createDTO 创建信息
     * @return 角色信息
     */
    @PostMapping
    public Result<AppRole> create(@PathVariable Long appId, 
                                  @Validated @RequestBody AppRoleCreateDTO createDTO) {
        log.info("新增应用角色，应用ID：{}，参数：{}", appId, createDTO);
        AppRole role = appRoleService.create(appId, createDTO);
        return Result.success("创建成功", role);
    }
    
    /**
     * 更新角色
     * 
     * @param appId 应用ID
     * @param id 角色ID
     * @param updateDTO 更新信息
     * @return 角色信息
     */
    @PutMapping("/{id}")
    public Result<AppRole> update(@PathVariable Long appId, 
                                  @PathVariable Long id,
                                  @Validated @RequestBody AppRoleUpdateDTO updateDTO) {
        log.info("更新应用角色，应用ID：{}，角色ID：{}，参数：{}", appId, id, updateDTO);
        AppRole role = appRoleService.update(appId, id, updateDTO);
        return Result.success("更新成功", role);
    }
    
    /**
     * 删除角色
     * 
     * @param appId 应用ID
     * @param id 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long appId, @PathVariable Long id) {
        log.info("删除应用角色，应用ID：{}，角色ID：{}", appId, id);
        appRoleService.delete(appId, id);
        return Result.success("删除成功");
    }
    
    /**
     * 查询角色的菜单权限
     * 
     * @param appId 应用ID
     * @param id 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long appId, @PathVariable Long id) {
        log.info("查询角色菜单权限，应用ID：{}，角色ID：{}", appId, id);
        if (appRoleService instanceof AppRoleServiceImpl) {
            List<Long> menuIds = ((AppRoleServiceImpl) appRoleService).getRoleMenuIds(appId, id);
            return Result.success(menuIds);
        }
        return Result.success(new java.util.ArrayList<>());
    }
}

