package com.zhangyu.permission.service;

import com.zhangyu.permission.dto.MenuPermissionQueryDTO;
import com.zhangyu.permission.dto.TokenRequestDTO;
import com.zhangyu.permission.vo.MenuPermissionVO;
import com.zhangyu.permission.vo.TokenResponseVO;

import java.util.List;

/**
 * 对外接口服务
 * 
 * @author zhangyu
 */
public interface ExternalApiService {
    
    /**
     * 获取访问令牌
     * 
     * @param tokenRequest 令牌请求
     * @return 令牌响应
     */
    TokenResponseVO getToken(TokenRequestDTO tokenRequest);
    
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
}

