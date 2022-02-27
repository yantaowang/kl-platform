package com.kl.example.service.data.sharding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 订单表
 *
 * @author xinminghao
 * @since 2021-09-17 20:10:38
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_order")
@ToString
public class TOrderEntity implements Serializable {

    private static final long serialVersionUID = -84607178329923230L;

    /**
    * 订单标识
    */
    private Long id;

    /**
    * 课程标识
    */
    private Long courseId;

    /**
    * 营期标识
    */
    private Long campDateId;

    /**
    * 班级标识
    */
    private Long classId;

    /**
    * 教师标识
    */
    private Long teacherId;

    /**
    * 客户标识
    */
    private Long customerId;

    
    private Long payOrderId;

    /**
    * 推广码
    */
    private String proCode;

    /**
    * 商品名称
    */
    private String name;

    /**
    * 标价金额(原始金额)
    */
    private Long priceAmount;

    /**
    * 售价金额(实际支付金额)
    */
    private Long payAmount;

    /**
    * 订单状态,1:待支付,2:已完成,3:已退款,4:退款中5,支付失败6,退款失败
    */
    private Integer status;

    /**
    * 备注
    */
    private String remark;

    /**
    * 是否删除
    */
    private Boolean isDeleted;

    /**
    * 失效时间
    */
    private Instant invalidDate;

    /**
    * 创建时间
    */
    private LocalDateTime createdTime;

    /**
    * 修改时间
    */
    private LocalDate modifiedTime;

    /**
    * LocalTime 测试
    */
    private LocalTime time;

}
