package com.zhangyu.permission.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.LoginDTO;
import com.zhangyu.permission.entity.LoginUser;
import com.zhangyu.permission.service.AuthService;
import com.zhangyu.permission.service.LoginUserService;
import com.zhangyu.permission.vo.LoginResultVO;
import com.zhangyu.permission.vo.LoginUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 * 
 * @author zhangyu
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private LoginUserService loginUserService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public LoginResultVO login(LoginDTO loginDTO) {
        // 1. 根据登录账户查询用户
        LoginUser loginUser = loginUserService.getByLoginAccount(loginDTO.getLoginAccount());
        if (loginUser == null) {
            throw new BusinessException("登录账户或密码错误");
        }
        
        // 2. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), loginUser.getPassword())) {
            throw new BusinessException("登录账户或密码错误");
        }
        
        // 3. 检查用户状态
        if (!"正常".equals(loginUser.getStatus())) {
            throw new BusinessException("账户已被禁用，请联系管理员");
        }
        
        // 4. 生成Token（Sa-Token会自动生成）
        StpUtil.login(loginUser.getId());
        String token = StpUtil.getTokenValue();
        
        // 5. 构建返回结果
        LoginResultVO result = new LoginResultVO();
        result.setToken(token);
        
        LoginUserVO userInfo = new LoginUserVO();
        BeanUtil.copyProperties(loginUser, userInfo);
        result.setUserInfo(userInfo);
        
        return result;
    }
    
    @Override
    public LoginUserVO getCurrentUser() {
        // 从Sa-Token中获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 查询用户信息
        LoginUser loginUser = loginUserService.getById(userId);
        if (loginUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 转换为VO
        LoginUserVO userInfo = new LoginUserVO();
        BeanUtil.copyProperties(loginUser, userInfo);
        
        return userInfo;
    }
    
    @Override
    public void logout() {
        // Sa-Token退出登录
        StpUtil.logout();
    }
}

