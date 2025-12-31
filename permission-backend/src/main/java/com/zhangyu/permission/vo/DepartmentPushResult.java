package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 部门推送结果VO
 * 
 * @author zhangyu
 */
@Data
public class DepartmentPushResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 成功数量
     */
    private Integer successCount;
    
    /**
     * 失败数量
     */
    private Integer failCount;
    
    /**
     * 总数量
     */
    private Integer totalCount;
    
    /**
     * 成功的部门ID列表
     */
    private List<Long> successIds;
    
    /**
     * 失败信息列表（部门名称 + 错误信息）
     */
    private List<DepartmentPushError> errors;
}

