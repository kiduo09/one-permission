package com.zhangyu.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.ConsumerInfoCreateDTO;
import com.zhangyu.permission.dto.ConsumerInfoQueryDTO;
import com.zhangyu.permission.dto.ConsumerInfoUpdateDTO;
import com.zhangyu.permission.entity.ConsumerInfo;
import com.zhangyu.permission.mapper.ConsumerInfoMapper;
import com.zhangyu.permission.service.ConsumerInfoService;
import com.zhangyu.permission.vo.ConsumerInfoListVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 消费者信息服务实现类
 * 
 * @author zhangyu
 */
@Service
public class ConsumerInfoServiceImpl extends ServiceImpl<ConsumerInfoMapper, ConsumerInfo> implements ConsumerInfoService {
    
    @Override
    public PageResult<ConsumerInfoListVO> pageQuery(ConsumerInfoQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<ConsumerInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 未删除
        wrapper.eq(ConsumerInfo::getDeleted, 0);
        
        // 消费者名称模糊查询
        String consumerName = queryDTO.getConsumerName();
        if (StringUtils.hasText(consumerName) && !"undefined".equals(consumerName)) {
            wrapper.like(ConsumerInfo::getConsumerName, consumerName.trim());
        }
        
        // clientId精确查询
        String clientId = queryDTO.getClientId();
        if (StringUtils.hasText(clientId) && !"undefined".equals(clientId)) {
            wrapper.eq(ConsumerInfo::getClientId, clientId.trim());
        }
        
        // 状态精确查询
        Integer status = queryDTO.getStatus();
        if (status != null) {
            wrapper.eq(ConsumerInfo::getStatus, status);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(ConsumerInfo::getCreateTime);
        
        // 分页查询
        Page<ConsumerInfo> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        IPage<ConsumerInfo> pageResult = this.page(page, wrapper);
        
        // 转换为VO列表
        List<ConsumerInfoListVO> voList = pageResult.getRecords().stream()
                .map(consumer -> {
                    ConsumerInfoListVO vo = new ConsumerInfoListVO();
                    BeanUtil.copyProperties(consumer, vo);
                    // 设置状态文本
                    vo.setStatusText(consumer.getStatus() == 1 ? "启用" : "禁用");
                    return vo;
                })
                .collect(Collectors.toList());
        
        return PageResult.of(voList, pageResult.getTotal(), queryDTO.getCurrent(), queryDTO.getSize());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsumerInfo create(ConsumerInfoCreateDTO createDTO) {
        // 自动生成clientId和clientSecret
        String clientId = generateClientId();
        String clientSecret = generateClientSecret();
        
        // 检查clientId是否已存在（理论上不会重复，但为了安全还是检查一下）
        LambdaQueryWrapper<ConsumerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsumerInfo::getClientId, clientId);
        wrapper.eq(ConsumerInfo::getDeleted, 0);
        ConsumerInfo existing = this.getOne(wrapper);
        if (existing != null) {
            // 如果重复，重新生成（最多重试10次）
            for (int i = 0; i < 10; i++) {
                clientId = generateClientId();
                wrapper.clear();
                wrapper.eq(ConsumerInfo::getClientId, clientId);
                wrapper.eq(ConsumerInfo::getDeleted, 0);
                existing = this.getOne(wrapper);
                if (existing == null) {
                    break;
                }
            }
            if (existing != null) {
                throw new BusinessException("生成clientId失败，请重试");
            }
        }
        
        // 创建消费者信息
        ConsumerInfo consumer = new ConsumerInfo();
        consumer.setConsumerName(createDTO.getConsumerName());
        consumer.setClientId(clientId);
        consumer.setClientSecret(clientSecret);
        consumer.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : 1);
        consumer.setDeleted(0);
        this.save(consumer);
        
        return consumer;
    }
    
    /**
     * 生成clientId
     * 格式：zy_ + 13位随机字符串（数字+小写字母）
     * 示例：zy_b113b4e000000
     */
    private String generateClientId() {
        // 生成13位随机字符（数字+小写字母）
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            randomPart.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        
        return "zy_" + randomPart.toString();
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
    public ConsumerInfo update(Long id, ConsumerInfoUpdateDTO updateDTO) {
        // 查询消费者信息
        ConsumerInfo consumer = this.getById(id);
        if (consumer == null || consumer.getDeleted() == 1) {
            throw new BusinessException("消费者信息不存在");
        }
        
        // 更新信息（不允许修改clientId和clientSecret）
        consumer.setConsumerName(updateDTO.getConsumerName());
        // clientSecret不允许修改，保持原值
        if (updateDTO.getStatus() != null) {
            consumer.setStatus(updateDTO.getStatus());
        }
        this.updateById(consumer);
        
        return consumer;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 逻辑删除
        ConsumerInfo consumer = this.getById(id);
        if (consumer == null || consumer.getDeleted() == 1) {
            throw new BusinessException("消费者信息不存在");
        }
        
        consumer.setDeleted(1);
        this.updateById(consumer);
    }
    
    @Override
    public ConsumerInfo getByClientId(String clientId) {
        LambdaQueryWrapper<ConsumerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsumerInfo::getClientId, clientId);
        wrapper.eq(ConsumerInfo::getDeleted, 0);
        wrapper.eq(ConsumerInfo::getStatus, 1); // 只查询启用的
        return this.getOne(wrapper);
    }
}

