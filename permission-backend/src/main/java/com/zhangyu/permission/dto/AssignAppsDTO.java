package com.zhangyu.permission.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分配应用DTO
 * 
 * @author zhangyu
 */
@Data
public class AssignAppsDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 应用ID列表（允许为空，表示移除所有应用）
     */
    private List<Long> appIds;
}

