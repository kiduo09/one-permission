package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.ConsumerInfoCreateDTO;
import com.zhangyu.permission.dto.ConsumerInfoQueryDTO;
import com.zhangyu.permission.dto.ConsumerInfoUpdateDTO;
import com.zhangyu.permission.entity.ConsumerInfo;
import com.zhangyu.permission.service.ConsumerInfoService;
import com.zhangyu.permission.vo.ConsumerInfoListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 消费者信息管理控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/consumer-info")
public class ConsumerInfoController {
    
    @Autowired
    private ConsumerInfoService consumerInfoService;
    
    /**
     * 分页查询消费者信息列表
     */
    @GetMapping("/page")
    public Result<PageResult<ConsumerInfoListVO>> pageQuery(ConsumerInfoQueryDTO queryDTO) {
        log.info("分页查询消费者信息列表，查询条件：{}", queryDTO);
        PageResult<ConsumerInfoListVO> result = consumerInfoService.pageQuery(queryDTO);
        return Result.success(result);
    }
    
    /**
     * 创建消费者信息
     */
    @PostMapping
    public Result<ConsumerInfo> create(@Validated @RequestBody ConsumerInfoCreateDTO createDTO) {
        log.info("创建消费者信息，创建信息：{}", createDTO);
        ConsumerInfo consumer = consumerInfoService.create(createDTO);
        return Result.success(consumer);
    }
    
    /**
     * 更新消费者信息
     */
    @PutMapping("/{id}")
    public Result<ConsumerInfo> update(@PathVariable Long id, @Validated @RequestBody ConsumerInfoUpdateDTO updateDTO) {
        log.info("更新消费者信息，ID：{}，更新信息：{}", id, updateDTO);
        updateDTO.setId(id);
        ConsumerInfo consumer = consumerInfoService.update(id, updateDTO);
        return Result.success(consumer);
    }
    
    /**
     * 删除消费者信息（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除消费者信息，ID：{}", id);
        consumerInfoService.delete(id);
        return Result.success();
    }
    
    /**
     * 根据ID查询消费者信息
     */
    @GetMapping("/{id}")
    public Result<ConsumerInfo> getById(@PathVariable Long id) {
        log.info("查询消费者信息，ID：{}", id);
        ConsumerInfo consumer = consumerInfoService.getById(id);
        if (consumer == null || consumer.getDeleted() == 1) {
            return Result.error("消费者信息不存在");
        }
        return Result.success(consumer);
    }
}

