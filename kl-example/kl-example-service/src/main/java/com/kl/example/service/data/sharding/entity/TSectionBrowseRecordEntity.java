package com.kl.example.service.data.sharding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 小节详情浏览记录表
 * </p>
 *
 * @author ewp
 * @since 2021-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_section_browse_record")
public class TSectionBrowseRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录标识
     */
    private Long id;

    /**
     * 客户标识
     */
    private Long customerId;

    /**
     * 营期标识
     */
    private Long campDateId;

    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 章节标识
     */
    private Long chapterId;

    /**
     * 小节标识
     */
    private Long sectionId;

    /**
     * 停留时长
     */
    private Long stayTime;

    /**
     * 系统名称(安卓:android,苹果:iOS,微信:wechat)
     */
    private String osName;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifiedTime;


}
