package com.zhangyu.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.ConsumerInfoCreateDTO;
import com.zhangyu.permission.dto.ConsumerInfoQueryDTO;
import com.zhangyu.permission.dto.ConsumerInfoUpdateDTO;
import com.zhangyu.permission.entity.ConsumerInfo;
import com.zhangyu.permission.vo.ConsumerInfoListVO;

/**
 * 消费者信息服务接口
 * 
 * @author zhangyu
 */
public interface ConsumerInfoService extends IService<ConsumerInfo> {
    
    /**
     * 分页查询消费者信息列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<ConsumerInfoListVO> pageQuery(ConsumerInfoQueryDTO queryDTO);
    
    /**
     * 创建消费者信息
     * 
     * @param createDTO 创建信息
     * @return 消费者信息
     */
    ConsumerInfo create(ConsumerInfoCreateDTO createDTO);
    
    /**
     * 更新消费者信息
     * 
     * @param id 消费者ID
     * @param updateDTO 更新信息
     * @return 消费者信息
     */
    ConsumerInfo update(Long id, ConsumerInfoUpdateDTO updateDTO);
    
    /**
     * 删除消费者信息（逻辑删除）
     * 
     * @param id 消费者ID
     */
    void delete(Long id);
    
    /**
     * 根据clientId查询消费者信息
     * 
     * @param clientId clientId
     * @return 消费者信息
     */
    ConsumerInfo getByClientId(String clientId);
}

