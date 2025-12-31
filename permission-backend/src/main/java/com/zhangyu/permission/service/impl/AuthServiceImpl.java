package com.zhangyu.permission.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.ChangePasswordDTO;
import com.zhangyu.permission.dto.LoginDTO;
import com.zhangyu.permission.entity.LoginUser;
import com.zhangyu.permission.entity.SysLogininfor;
import com.zhangyu.permission.service.AuthService;
import com.zhangyu.permission.service.LoginUserService;
import com.zhangyu.permission.service.SysLogininforService;
import com.zhangyu.permission.util.IpUtils;
import com.zhangyu.permission.util.UserAgentUtils;
import com.zhangyu.permission.vo.LoginResultVO;
import com.zhangyu.permission.vo.LoginUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

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
    
    @Autowired
    private SysLogininforService sysLogininforService;
    
    @Override
    public LoginResultVO login(LoginDTO loginDTO, HttpServletRequest request) {
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
        
        // 5. 记录登录成功日志
        recordLoginSuccess(loginUser.getLoginAccount(), request);
        
        // 6. 构建返回结果
        LoginResultVO result = new LoginResultVO();
        result.setToken(token);
        
        LoginUserVO userInfo = new LoginUserVO();
        BeanUtil.copyProperties(loginUser, userInfo);
        result.setUserInfo(userInfo);
        
        return result;
    }
    
    @Override
    public void recordLoginFail(String loginAccount, String message, HttpServletRequest request) {
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(loginAccount);
        logininfor.setIpaddr(IpUtils.getIpAddr(request));
        logininfor.setLoginLocation(IpUtils.getIpLocation(logininfor.getIpaddr()));
        logininfor.setBrowser(UserAgentUtils.getBrowser(request));
        logininfor.setOs(UserAgentUtils.getOs(request));
        logininfor.setStatus("1"); // 1表示失败
        logininfor.setMsg(message);
        logininfor.setLoginTime(LocalDateTime.now());
        sysLogininforService.recordLogininfor(logininfor);
    }
    
    /**
     * 记录登录成功日志
     * 
     * @param loginAccount 登录账户
     * @param request HTTP请求
     */
    private void recordLoginSuccess(String loginAccount, HttpServletRequest request) {
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(loginAccount);
        logininfor.setIpaddr(IpUtils.getIpAddr(request));
        logininfor.setLoginLocation(IpUtils.getIpLocation(logininfor.getIpaddr()));
        logininfor.setBrowser(UserAgentUtils.getBrowser(request));
        logininfor.setOs(UserAgentUtils.getOs(request));
        logininfor.setStatus("0"); // 0表示成功
        logininfor.setMsg("登录成功");
        logininfor.setLoginTime(LocalDateTime.now());
        sysLogininforService.recordLogininfor(logininfor);
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
    
    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        // 1. 验证新密码和确认密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不一致");
        }
        
        // 2. 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 3. 查询用户信息
        LoginUser loginUser = loginUserService.getById(userId);
        if (loginUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 4. 验证旧密码
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), loginUser.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        
        // 5. 检查新密码是否与旧密码相同
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), loginUser.getPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        
        // 6. 加密新密码并更新
        loginUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        loginUser.setUpdateTime(LocalDateTime.now());
        loginUserService.updateById(loginUser);
    }
}

