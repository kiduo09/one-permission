package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.AssignDepartmentsDTO;
import com.zhangyu.permission.dto.RoleDepartmentQueryDTO;
import com.zhangyu.permission.service.AppRoleDepartmentService;
import com.zhangyu.permission.vo.RoleDepartmentListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用角色部门管理控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/applications/{appId}/roles/{roleId}/departments")
public class AppRoleDepartmentController {
    
    @Autowired
    private AppRoleDepartmentService appRoleDepartmentService;
    
    /**
     * 分页查询角色的部门分配列表
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<RoleDepartmentListVO>> pageQueryDepartments(@PathVariable Long appId,
                                                                           @PathVariable Long roleId,
                                                                           RoleDepartmentQueryDTO queryDTO) {
        log.info("分页查询角色部门分配列表，应用ID：{}，角色ID：{}，查询条件：{}", appId, roleId, queryDTO);
        PageResult<RoleDepartmentListVO> result = appRoleDepartmentService.pageQueryDepartments(appId, roleId, queryDTO);
        return Result.success(result);
    }
    
    /**
     * 分配部门给角色
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param assignDTO 分配信息
     * @return 操作结果
     */
    @PostMapping
    public Result<?> assignDepartments(@PathVariable Long appId,
                                        @PathVariable Long roleId,
                                        @Validated @RequestBody AssignDepartmentsDTO assignDTO) {
        log.info("分配部门给角色，应用ID：{}，角色ID：{}，部门IDs：{}", appId, roleId, assignDTO.getDepartmentIds());
        appRoleDepartmentService.assignDepartments(appId, roleId, assignDTO);
        return Result.success("分配成功");
    }
    
    /**
     * 取消部门授权
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param departmentId 部门ID（关联表ID）
     * @return 操作结果
     */
    @DeleteMapping("/{departmentId}")
    public Result<?> revokeDepartment(@PathVariable Long appId,
                                       @PathVariable Long roleId,
                                       @PathVariable Long departmentId) {
        log.info("取消部门授权，应用ID：{}，角色ID：{}，部门ID：{}", appId, roleId, departmentId);
        appRoleDepartmentService.revokeDepartment(appId, roleId, departmentId);
        return Result.success("取消授权成功");
    }
    
    /**
     * 批量取消部门授权
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param departmentIds 部门ID列表（关联表ID）
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public Result<?> batchRevokeDepartments(@PathVariable Long appId,
                                             @PathVariable Long roleId,
                                             @RequestBody List<Long> departmentIds) {
        log.info("批量取消部门授权，应用ID：{}，角色ID：{}，部门IDs：{}", appId, roleId, departmentIds);
        appRoleDepartmentService.batchRevokeDepartments(appId, roleId, departmentIds);
        return Result.success("取消授权成功");
    }
}

