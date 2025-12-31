package com.zhangyu.permission.service;

import com.zhangyu.permission.dto.DepartmentPushDTO;
import com.zhangyu.permission.dto.MenuPermissionQueryDTO;
import com.zhangyu.permission.dto.NormalUserPushDTO;
import com.zhangyu.permission.dto.TokenRequestDTO;
import com.zhangyu.permission.vo.DepartmentPushResult;
import com.zhangyu.permission.vo.MenuPermissionVO;
import com.zhangyu.permission.vo.NormalUserPushResult;
import com.zhangyu.permission.vo.TokenResponseVO;

import java.util.List;

/**
 * 对外接口服务
 * 
 * @author zhangyu
 */
public interface ExternalApiService {
    
    /**
     * 获取访问令牌（使用 applications 表的 clientId/clientSecret）
     * 
     * @param tokenRequest 令牌请求
     * @return 令牌响应
     */
    TokenResponseVO getToken(TokenRequestDTO tokenRequest);
    
    /**
     * 获取访问令牌（使用 consumer_info 表的 clientId/clientSecret）
     * 
     * @param tokenRequest 令牌请求
     * @return 令牌响应
     */
    TokenResponseVO getConsumerToken(TokenRequestDTO tokenRequest);
    
    /**
     * 根据应用ID和工号查询菜单权限
     * 
     * @param token 访问令牌
     * @param queryDTO 查询条件
     * @return 菜单权限列表（树形结构）
     */
    List<MenuPermissionVO> getMenuPermissions(String token, MenuPermissionQueryDTO queryDTO);
    
    /**
     * 验证Token是否有效
     * 
     * @param token 访问令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
    
    /**
     * 验证Consumer Token是否有效（consumer_ 前缀）
     * 
     * @param token 访问令牌
     * @return 是否有效
     */
    boolean validateConsumerToken(String token);
    
    /**
     * 批量推送部门数据（一次性推送所有部门）
     * 
     * @param departmentList 部门数据列表
     * @return 推送结果（成功数量、失败数量、部门ID列表）
     */
    DepartmentPushResult pushDepartments(List<DepartmentPushDTO> departmentList);
    
    /**
     * 批量推送普通用户数据
     * 
     * @param userList 用户数据列表
     * @return 推送结果（成功数量、失败数量、用户ID列表）
     */
    NormalUserPushResult pushNormalUsers(List<NormalUserPushDTO> userList);
}

