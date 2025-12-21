package com.zhangyu.permission.service;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.AssignUsersDTO;
import com.zhangyu.permission.dto.AvailableUserQueryDTO;
import com.zhangyu.permission.dto.RoleUserQueryDTO;
import com.zhangyu.permission.vo.NormalUserListVO;
import com.zhangyu.permission.vo.RoleUserListVO;

import java.util.List;

/**
 * 应用角色用户服务接口
 * 
 * @author zhangyu
 */
public interface AppRoleUserService {
    
    /**
     * 分页查询角色的用户分配列表
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<RoleUserListVO> pageQueryUsers(Long appId, Long roleId, RoleUserQueryDTO queryDTO);
    
    /**
     * 分配用户给角色（个人分配）
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param assignDTO 分配信息
     */
    void assignUsers(Long appId, Long roleId, AssignUsersDTO assignDTO);
    
    /**
     * 批量取消用户授权
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param userIds 用户ID列表（关联表ID）
     */
    void batchRevokeUsers(Long appId, Long roleId, List<Long> userIds);
    
    /**
     * 取消单个用户授权
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param userId 用户ID（关联表ID）
     */
    void revokeUser(Long appId, Long roleId, Long userId);
    
    /**
     * 查询可选用户列表（用于分配用户弹窗）
     * 
     * @param appId 应用ID
     * @param roleId 角色ID
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<NormalUserListVO> getAvailableUsers(Long appId, Long roleId, AvailableUserQueryDTO queryDTO);
}

