package com.zhangyu.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.SysLogininforQueryDTO;
import com.zhangyu.permission.entity.SysLogininfor;
import com.zhangyu.permission.mapper.SysLogininforMapper;
import com.zhangyu.permission.service.SysLogininforService;
import com.zhangyu.permission.vo.SysLogininforListVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统登录日志服务实现类
 * 
 * @author zhangyu
 */
@Service
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor> implements SysLogininforService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordLogininfor(SysLogininfor logininfor) {
        if (logininfor.getLoginTime() == null) {
            logininfor.setLoginTime(LocalDateTime.now());
        }
        this.save(logininfor);
    }
    
    @Override
    public PageResult<SysLogininforListVO> pageQuery(SysLogininforQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<SysLogininfor> wrapper = new LambdaQueryWrapper<>();
        
        // 用户账号模糊查询
        if (StringUtils.hasText(queryDTO.getUserName())) {
            wrapper.like(SysLogininfor::getUserName, queryDTO.getUserName());
        }
        
        // IP地址模糊查询
        if (StringUtils.hasText(queryDTO.getIpaddr())) {
            wrapper.like(SysLogininfor::getIpaddr, queryDTO.getIpaddr());
        }
        
        // 登录状态
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(SysLogininfor::getStatus, queryDTO.getStatus());
        }
        
        // 时间范围查询
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(SysLogininfor::getLoginTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(SysLogininfor::getLoginTime, queryDTO.getEndTime());
        }
        
        // 按登录时间倒序
        wrapper.orderByDesc(SysLogininfor::getLoginTime);
        
        // 分页查询
        Page<SysLogininfor> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        IPage<SysLogininfor> pageResult = this.page(page, wrapper);
        
        // 转换为VO
        List<SysLogininforListVO> voList = pageResult.getRecords().stream()
                .map(entity -> {
                    SysLogininforListVO vo = new SysLogininforListVO();
                    BeanUtil.copyProperties(entity, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        // 构建分页结果
        return PageResult.of(voList, pageResult.getTotal(), queryDTO.getCurrent(), queryDTO.getSize());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long infoId) {
        this.removeById(infoId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clean() {
        this.remove(new LambdaQueryWrapper<>());
    }
}

