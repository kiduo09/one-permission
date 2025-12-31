package com.zhangyu.permission.service;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.SysLogininforQueryDTO;
import com.zhangyu.permission.entity.SysLogininfor;
import com.zhangyu.permission.vo.SysLogininforListVO;

/**
 * 系统登录日志服务接口
 * 
 * @author zhangyu
 */
public interface SysLogininforService {
    
    /**
     * 记录登录日志
     * 
     * @param logininfor 登录日志信息
     */
    void recordLogininfor(SysLogininfor logininfor);
    
    /**
     * 分页查询登录日志
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<SysLogininforListVO> pageQuery(SysLogininforQueryDTO queryDTO);
    
    /**
     * 删除登录日志
     * 
     * @param infoId 日志ID
     */
    void delete(Long infoId);
    
    /**
     * 清空登录日志
     */
    void clean();
}

