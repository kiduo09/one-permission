package com.zhangyu.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.LoginUserCreateDTO;
import com.zhangyu.permission.dto.LoginUserQueryDTO;
import com.zhangyu.permission.dto.LoginUserUpdateDTO;
import com.zhangyu.permission.entity.LoginUser;
import com.zhangyu.permission.vo.LoginUserListVO;

/**
 * 登录用户服务接口
 * 
 * @author zhangyu
 */
public interface LoginUserService extends IService<LoginUser> {
    
    /**
     * 根据登录账户查询用户
     * 
     * @param loginAccount 登录账户
     * @return 用户信息
     */
    LoginUser getByLoginAccount(String loginAccount);
    
    /**
     * 分页查询登录用户列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<LoginUserListVO> pageQuery(LoginUserQueryDTO queryDTO);
    
    /**
     * 创建登录用户
     * 
     * @param createDTO 创建信息
     * @return 用户信息
     */
    LoginUser create(LoginUserCreateDTO createDTO);
    
    /**
     * 更新登录用户
     * 
     * @param id 用户ID
     * @param updateDTO 更新信息
     * @return 用户信息
     */
    LoginUser update(Long id, LoginUserUpdateDTO updateDTO);
    
    /**
     * 删除登录用户
     * 
     * @param id 用户ID
     */
    void delete(Long id);
    
    /**
     * 批量删除登录用户
     * 
     * @param ids 用户ID列表
     */
    void batchDelete(java.util.List<Long> ids);
}

