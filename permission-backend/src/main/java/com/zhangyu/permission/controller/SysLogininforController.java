package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.SysLogininforQueryDTO;
import com.zhangyu.permission.service.SysLogininforService;
import com.zhangyu.permission.vo.SysLogininforListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统登录日志控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/sys-logininfor")
public class SysLogininforController {
    
    @Autowired
    private SysLogininforService sysLogininforService;
    
    /**
     * 分页查询登录日志
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<SysLogininforListVO>> pageQuery(@Validated SysLogininforQueryDTO queryDTO) {
        log.info("分页查询登录日志，参数：current={}, size={}, userName={}, ipaddr={}, status={}, startTime={}, endTime={}", 
                queryDTO.getCurrent(), queryDTO.getSize(), queryDTO.getUserName(), 
                queryDTO.getIpaddr(), queryDTO.getStatus(), queryDTO.getStartTime(), queryDTO.getEndTime());
        PageResult<SysLogininforListVO> result = sysLogininforService.pageQuery(queryDTO);
        log.info("分页查询登录日志结果：total={}, listSize={}", result.getTotal(), 
                result.getList() != null ? result.getList().size() : 0);
        return Result.success(result);
    }
    
    /**
     * 删除登录日志
     * 
     * @param infoId 日志ID
     * @return 操作结果
     */
    @DeleteMapping("/{infoId}")
    public Result<?> delete(@PathVariable Long infoId) {
        log.info("删除登录日志，ID：{}", infoId);
        sysLogininforService.delete(infoId);
        return Result.success("删除成功");
    }
    
    /**
     * 清空登录日志
     * 
     * @return 操作结果
     */
    @DeleteMapping("/clean")
    public Result<?> clean() {
        log.info("清空登录日志");
        sysLogininforService.clean();
        return Result.success("清空成功");
    }
}

