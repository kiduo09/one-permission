package com.zhangyu.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.ApplicationCreateDTO;
import com.zhangyu.permission.dto.ApplicationQueryDTO;
import com.zhangyu.permission.dto.ApplicationUpdateDTO;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.vo.ApplicationListVO;
import java.util.List;

/**
 * 应用服务接口
 * 
 * @author zhangyu
 */
public interface ApplicationService extends IService<Application> {
    
    /**
     * 分页查询应用列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<ApplicationListVO> pageQuery(ApplicationQueryDTO queryDTO);
    
    /**
     * 创建应用
     * 
     * @param createDTO 创建信息
     * @return 应用信息
     */
    Application create(ApplicationCreateDTO createDTO);
    
    /**
     * 更新应用
     * 
     * @param id 应用ID
     * @param updateDTO 更新信息
     * @return 应用信息
     */
    Application update(Long id, ApplicationUpdateDTO updateDTO);
    
    /**
     * 删除应用
     * 
     * @param id 应用ID
     */
    void delete(Long id);
    
    /**
     * 查询当前登录用户可访问的应用列表（用于下拉选择）
     * 
     * @param loginUserId 登录用户ID（如果为null，则返回所有应用）
     * @return 应用列表
     */
    List<Application> getAuthorizedApplications(Long loginUserId);
}

