package com.zhangyu.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.LoginUserCreateDTO;
import com.zhangyu.permission.dto.LoginUserQueryDTO;
import com.zhangyu.permission.dto.LoginUserUpdateDTO;
import com.zhangyu.permission.entity.LoginUser;
import com.zhangyu.permission.mapper.LoginUserMapper;
import com.zhangyu.permission.service.LoginUserService;
import com.zhangyu.permission.vo.LoginUserListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录用户服务实现类
 * 
 * @author zhangyu
 */
@Service
public class LoginUserServiceImpl extends ServiceImpl<LoginUserMapper, LoginUser> implements LoginUserService {
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public LoginUser getByLoginAccount(String loginAccount) {
        LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginUser::getLoginAccount, loginAccount);
        return this.getOne(wrapper);
    }
    
    @Override
    public PageResult<LoginUserListVO> pageQuery(LoginUserQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
        
        // 登录账户模糊查询（过滤null、空字符串和"undefined"）
        String loginAccount = queryDTO.getLoginAccount();
        if (StringUtils.hasText(loginAccount) && !"undefined".equals(loginAccount)) {
            wrapper.like(LoginUser::getLoginAccount, loginAccount.trim());
        }
        
        // 姓名模糊查询（过滤null、空字符串和"undefined"）
        String name = queryDTO.getName();
        if (StringUtils.hasText(name) && !"undefined".equals(name)) {
            wrapper.like(LoginUser::getName, name.trim());
        }
        
        // 状态精确查询（过滤null、空字符串和"undefined"）
        String status = queryDTO.getStatus();
        if (StringUtils.hasText(status) && !"undefined".equals(status)) {
            wrapper.eq(LoginUser::getStatus, status.trim());
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(LoginUser::getCreateTime);
        
        // 分页查询
        Page<LoginUser> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        IPage<LoginUser> pageResult = this.page(page, wrapper);
        
        // 转换为VO列表
        List<LoginUserListVO> voList = pageResult.getRecords().stream()
                .map(user -> {
                    LoginUserListVO vo = new LoginUserListVO();
                    BeanUtil.copyProperties(user, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        return PageResult.of(voList, pageResult.getTotal(), queryDTO.getPage(), queryDTO.getPageSize());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginUser create(LoginUserCreateDTO createDTO) {
        // 检查登录账户是否已存在
        LoginUser existUser = getByLoginAccount(createDTO.getLoginAccount());
        if (existUser != null) {
            throw new BusinessException("登录账户已存在");
        }
        
        // 创建用户实体
        LoginUser loginUser = new LoginUser();
        BeanUtil.copyProperties(createDTO, loginUser);
        
        // 加密密码（如果密码为空，使用默认密码：onepermission）
        String password = createDTO.getPassword();
        if (password == null || password.trim().isEmpty()) {
            password = "onepermission";
        }
        loginUser.setPassword(passwordEncoder.encode(password));
        
        // 保存
        this.save(loginUser);
        
        return loginUser;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginUser update(Long id, LoginUserUpdateDTO updateDTO) {
        // 查询用户
        LoginUser loginUser = this.getById(id);
        if (loginUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查登录账户是否被其他用户使用
        LoginUser existUser = getByLoginAccount(updateDTO.getLoginAccount());
        if (existUser != null && !existUser.getId().equals(id)) {
            throw new BusinessException("登录账户已被其他用户使用");
        }
        
        // 更新字段
        loginUser.setLoginAccount(updateDTO.getLoginAccount());
        loginUser.setName(updateDTO.getName());
        loginUser.setEmail(updateDTO.getEmail());
        loginUser.setAdminType(updateDTO.getAdminType());
        loginUser.setStatus(updateDTO.getStatus());
        loginUser.setRemark(updateDTO.getRemark());
        
        // 如果提供了新密码，则更新密码
        if (StringUtils.hasText(updateDTO.getPassword())) {
            loginUser.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        // 保存
        this.updateById(loginUser);
        
        return loginUser;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查用户是否存在
        LoginUser loginUser = this.getById(id);
        if (loginUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // TODO: 检查是否有关联的应用授权，如果有则不允许删除或提示
        // 这里可以添加关联检查逻辑
        
        // 删除用户
        this.removeById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的用户");
        }
        
        // 批量删除
        this.removeByIds(ids);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        // 查询用户
        LoginUser loginUser = this.getById(id);
        if (loginUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 重置密码为默认密码：onepermission
        loginUser.setPassword(passwordEncoder.encode("onepermission"));
        
        // 保存
        this.updateById(loginUser);
    }
}

