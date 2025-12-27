package com.zhangyu.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.ApplicationCreateDTO;
import com.zhangyu.permission.dto.ApplicationQueryDTO;
import com.zhangyu.permission.dto.ApplicationUpdateDTO;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.mapper.ApplicationMapper;
import com.zhangyu.permission.service.ApplicationService;
import com.zhangyu.permission.service.LoginUserAppService;
import com.zhangyu.permission.vo.ApplicationListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应用服务实现类
 * 
 * @author zhangyu
 */
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {
    
    @Autowired
    @Lazy
    private LoginUserAppService loginUserAppService;
    
    @Override
    public PageResult<ApplicationListVO> pageQuery(ApplicationQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        
        // 应用名称模糊查询（过滤null、空字符串和"undefined"）
        String name = queryDTO.getName();
        if (StringUtils.hasText(name) && !"undefined".equals(name)) {
            wrapper.like(Application::getName, name.trim());
        }
        
        // 应用Key模糊查询（过滤null、空字符串和"undefined"）
        String appKey = queryDTO.getAppKey();
        if (StringUtils.hasText(appKey) && !"undefined".equals(appKey)) {
            wrapper.like(Application::getAppKey, appKey.trim());
        }
        
        // 状态精确查询（过滤null、空字符串和"undefined"）
        String status = queryDTO.getStatus();
        if (StringUtils.hasText(status) && !"undefined".equals(status)) {
            wrapper.eq(Application::getStatus, status.trim());
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(Application::getCreateTime);
        
        // 分页查询
        Page<Application> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        IPage<Application> pageResult = this.page(page, wrapper);
        
        // 转换为VO列表
        List<ApplicationListVO> voList = pageResult.getRecords().stream()
                .map(app -> {
                    ApplicationListVO vo = new ApplicationListVO();
                    BeanUtil.copyProperties(app, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        return PageResult.of(voList, pageResult.getTotal(), queryDTO.getPage(), queryDTO.getPageSize());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Application create(ApplicationCreateDTO createDTO) {
        // 检查应用Key是否已存在
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getAppKey, createDTO.getAppKey());
        Application existApp = this.getOne(wrapper);
        if (existApp != null) {
            throw new BusinessException("应用Key已存在");
        }
        
        // 自动生成clientId和clientSecret
        String clientId = generateClientId();
        String clientSecret = generateClientSecret();
        
        // 检查clientId是否已存在（理论上不会重复，但为了安全还是检查一下）
        LambdaQueryWrapper<Application> clientIdWrapper = new LambdaQueryWrapper<>();
        clientIdWrapper.eq(Application::getClientId, clientId);
        Application existing = this.getOne(clientIdWrapper);
        if (existing != null) {
            // 如果重复，重新生成（最多重试10次）
            for (int i = 0; i < 10; i++) {
                clientId = generateClientId();
                clientIdWrapper.clear();
                clientIdWrapper.eq(Application::getClientId, clientId);
                existing = this.getOne(clientIdWrapper);
                if (existing == null) {
                    break;
                }
            }
            if (existing != null) {
                throw new BusinessException("生成clientId失败，请重试");
            }
        }
        
        // 创建应用实体
        Application application = new Application();
        BeanUtil.copyProperties(createDTO, application);
        application.setClientId(clientId);
        application.setClientSecret(clientSecret);
        
        // 保存
        this.save(application);
        
        return application;
    }
    
    /**
     * 生成clientId
     * 格式：app_ + 13位随机字符串（数字+小写字母）
     * 示例：app_b113b4e000000
     */
    private String generateClientId() {
        // 生成13位随机字符（数字+小写字母）
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            randomPart.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        
        return "app_" + randomPart.toString();
    }
    
    /**
     * 生成clientSecret
     * 格式：32位十六进制字符串（UUID去掉横线）
     */
    private String generateClientSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Application update(Long id, ApplicationUpdateDTO updateDTO) {
        // 查询应用
        Application application = this.getById(id);
        if (application == null) {
            throw new BusinessException("应用不存在");
        }
        
        // 检查应用Key是否被其他应用使用
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getAppKey, updateDTO.getAppKey());
        Application existApp = this.getOne(wrapper);
        if (existApp != null && !existApp.getId().equals(id)) {
            throw new BusinessException("应用Key已被其他应用使用");
        }
        
        // 更新字段
        application.setName(updateDTO.getName());
        application.setAppKey(updateDTO.getAppKey());
        application.setRemark(updateDTO.getRemark());
        application.setStatus(updateDTO.getStatus());
        
        // 保存
        this.updateById(application);
        
        return application;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查应用是否存在
        Application application = this.getById(id);
        if (application == null) {
            throw new BusinessException("应用不存在");
        }
        
        // TODO: 检查是否有关联数据（菜单、角色、授权等），如果有则不允许删除或提示
        // 这里可以添加关联检查逻辑：
        // 1. 检查是否有应用菜单
        // 2. 检查是否有应用角色
        // 3. 检查是否有登录用户授权
        // 如果有关联数据，可以抛出异常或返回提示信息
        
        // 删除应用
        this.removeById(id);
    }
    
    @Override
    public List<Application> getAuthorizedApplications(Long loginUserId) {
        // 如果loginUserId为null，返回所有应用（不过滤状态）
        if (loginUserId == null) {
            LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByAsc(Application::getName);
            return this.list(wrapper);
        }
        
        // 查询用户授权的应用
        return loginUserAppService.getApplicationsByLoginUserId(loginUserId);
    }
}

