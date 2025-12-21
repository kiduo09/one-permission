package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分配用户DTO
 * 
 * @author zhangyu
 */
@Data
public class AssignUsersDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户工号列表
     */
    private List<String> workNos;
}

