package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.AssignUsersDTO;
import com.zhangyu.permission.dto.AvailableUserQueryDTO;
import com.zhangyu.permission.dto.RoleUserQueryDTO;
import com.zhangyu.permission.service.AppRoleUserService;
import com.zhangyu.permission.vo.NormalUserListVO;
import com.zhangyu.permission.vo.RoleUserListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用角色用户管理控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/applications/{appId}/roles/{roleId}/users")
public class AppRoleUserController {
    
    @Autowired
    private AppRoleUserService appRoleUserService;
    
    /**
     * 分页查询角色的用户分配列表
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<RoleUserListVO>> pageQueryUsers(@PathVariable Long appId,
                                                              @PathVariable Long roleId,
                                                              RoleUserQueryDTO queryDTO) {
        log.info("分页查询角色用户分配列表，应用ID：{}，角色ID：{}，查询条件：{}", appId, roleId, queryDTO);
        PageResult<RoleUserListVO> result = appRoleUserService.pageQueryUsers(appId, roleId, queryDTO);
        return Result.success(result);
    }
    
    /**
     * 分配用户给角色（个人分配）
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param assignDTO 分配信息
     * @return 操作结果
     */
    @PostMapping
    public Result<?> assignUsers(@PathVariable Long appId,
                                 @PathVariable Long roleId,
                                 @Validated @RequestBody AssignUsersDTO assignDTO) {
        log.info("分配用户给角色，应用ID：{}，角色ID：{}，用户工号：{}", appId, roleId, assignDTO.getWorkNos());
        appRoleUserService.assignUsers(appId, roleId, assignDTO);
        return Result.success("分配成功");
    }
    
    /**
     * 批量取消用户授权
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param userIds 用户ID列表（关联表ID）
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public Result<?> batchRevokeUsers(@PathVariable Long appId,
                                       @PathVariable Long roleId,
                                       @RequestBody List<Long> userIds) {
        log.info("批量取消用户授权，应用ID：{}，角色ID：{}，用户IDs：{}", appId, roleId, userIds);
        appRoleUserService.batchRevokeUsers(appId, roleId, userIds);
        return Result.success("取消授权成功");
    }
    
    /**
     * 取消单个用户授权
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param userId 用户ID（关联表ID）
     * @return 操作结果
     */
    @DeleteMapping("/{userId}")
    public Result<?> revokeUser(@PathVariable Long appId,
                                 @PathVariable Long roleId,
                                 @PathVariable Long userId) {
        log.info("取消用户授权，应用ID：{}，角色ID：{}，用户ID：{}", appId, roleId, userId);
        appRoleUserService.revokeUser(appId, roleId, userId);
        return Result.success("取消授权成功");
    }
    
    /**
     * 查询可选用户列表（用于分配用户弹窗）
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/available")
    public Result<PageResult<NormalUserListVO>> getAvailableUsers(@PathVariable Long appId,
                                                                    @PathVariable Long roleId,
                                                                    AvailableUserQueryDTO queryDTO) {
        log.info("查询可选用户列表，应用ID：{}，角色ID：{}，查询条件：{}", appId, roleId, queryDTO);
        PageResult<NormalUserListVO> result = appRoleUserService.getAvailableUsers(appId, roleId, queryDTO);
        return Result.success(result);
    }
}

