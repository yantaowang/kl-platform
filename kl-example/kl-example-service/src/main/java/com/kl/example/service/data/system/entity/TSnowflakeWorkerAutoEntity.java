package com.kl.example.service.data.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 雪花工作者表-自动生成
 * </p>
 *
 * @author ewp
 * @since 2021-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_snowflake_worker_auto")
public class TSnowflakeWorkerAutoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 主机地址
     */
    private String hostAddress;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime modifiedTime;


}
