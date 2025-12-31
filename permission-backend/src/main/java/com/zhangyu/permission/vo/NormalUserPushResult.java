package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 普通用户推送结果VO
 * 
 * @author zhangyu
 */
@Data
public class NormalUserPushResult implements Serializable {
    
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
     * 成功的用户ID列表
     */
    private List<Long> successIds;
    
    /**
     * 失败信息列表（工号 + 错误信息）
     */
    private List<NormalUserPushError> errors;
}

