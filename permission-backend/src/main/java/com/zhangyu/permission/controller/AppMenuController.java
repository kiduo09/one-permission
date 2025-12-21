package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.AppMenuCreateDTO;
import com.zhangyu.permission.dto.AppMenuQueryDTO;
import com.zhangyu.permission.dto.AppMenuUpdateDTO;
import com.zhangyu.permission.entity.AppMenu;
import com.zhangyu.permission.service.AppMenuService;
import com.zhangyu.permission.vo.AppMenuListVO;
import com.zhangyu.permission.vo.AppMenuTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用菜单管理控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/applications/{appId}/menus")
public class AppMenuController {
    
    @Autowired
    private AppMenuService appMenuService;
    
    /**
     * 查询应用菜单树形列表
     * 
     * @param appId 应用ID
     * @param status 状态（可选）
     * @return 树形菜单列表
     */
    @GetMapping("/tree")
    public Result<List<AppMenuTreeVO>> getMenuTree(@PathVariable Long appId,
                                                    @RequestParam(required = false) String status) {
        log.info("查询应用菜单树形列表，应用ID：{}，状态：{}", appId, status);
        List<AppMenuTreeVO> tree = appMenuService.getMenuTree(appId, status);
        return Result.success(tree);
    }
    
    /**
     * 分页查询应用菜单列表
     * 
     * @param appId 应用ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<AppMenuListVO>> pageQuery(@PathVariable Long appId,
                                                        @Validated AppMenuQueryDTO queryDTO) {
        log.info("分页查询应用菜单列表，应用ID：{}，参数：{}", appId, queryDTO);
        PageResult<AppMenuListVO> result = appMenuService.pageQuery(appId, queryDTO);
        return Result.success(result);
    }
    
    /**
     * 根据ID查询菜单详情
     * 
     * @param appId 应用ID
     * @param id 菜单ID
     * @return 菜单信息
     */
    @GetMapping("/{id}")
    public Result<AppMenu> getById(@PathVariable Long appId, @PathVariable Long id) {
        log.info("查询应用菜单详情，应用ID：{}，菜单ID：{}", appId, id);
        AppMenu menu = appMenuService.getById(id);
        if (menu == null || !menu.getAppId().equals(appId)) {
            return Result.notFound("菜单不存在");
        }
        return Result.success(menu);
    }
    
    /**
     * 新增菜单
     * 
     * @param appId 应用ID
     * @param createDTO 创建信息
     * @return 菜单信息
     */
    @PostMapping
    public Result<AppMenu> create(@PathVariable Long appId,
                                  @Validated @RequestBody AppMenuCreateDTO createDTO) {
        log.info("新增应用菜单，应用ID：{}，参数：{}", appId, createDTO);
        AppMenu menu = appMenuService.create(appId, createDTO);
        return Result.success("创建成功", menu);
    }
    
    /**
     * 更新菜单
     * 
     * @param appId 应用ID
     * @param id 菜单ID
     * @param updateDTO 更新信息
     * @return 菜单信息
     */
    @PutMapping("/{id}")
    public Result<AppMenu> update(@PathVariable Long appId,
                                  @PathVariable Long id,
                                  @Validated @RequestBody AppMenuUpdateDTO updateDTO) {
        log.info("更新应用菜单，应用ID：{}，菜单ID：{}，参数：{}", appId, id, updateDTO);
        AppMenu menu = appMenuService.update(appId, id, updateDTO);
        return Result.success("更新成功", menu);
    }
    
    /**
     * 删除菜单
     * 
     * @param appId 应用ID
     * @param id 菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long appId, @PathVariable Long id) {
        log.info("删除应用菜单，应用ID：{}，菜单ID：{}", appId, id);
        appMenuService.delete(appId, id);
        return Result.success("删除成功");
    }
}

