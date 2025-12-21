package com.zhangyu.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.dto.NormalUserQueryDTO;
import com.zhangyu.permission.entity.NormalUser;
import com.zhangyu.permission.vo.NormalUserDetailVO;
import com.zhangyu.permission.vo.NormalUserListVO;

/**
 * 普通用户服务接口
 * 
 * @author zhangyu
 */
public interface NormalUserService extends IService<NormalUser> {
    
    /**
     * 分页查询普通用户列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<NormalUserListVO> pageQuery(NormalUserQueryDTO queryDTO);
    
    /**
     * 根据ID查询普通用户详情
     * 
     * @param id 用户ID
     * @return 用户详情
     */
    NormalUserDetailVO getDetailById(Long id);
    
    /**
     * 根据工号查询用户
     * 
     * @param workNo 工号
     * @return 用户信息
     */
    NormalUser getByWorkNo(String workNo);
}

