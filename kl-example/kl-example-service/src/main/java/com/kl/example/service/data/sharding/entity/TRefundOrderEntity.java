package com.kl.example.service.data.sharding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 退款单
 *
 * @author xinminghao
 * @since 2021-09-17 20:10:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_refund_order")
public class TRefundOrderEntity implements Serializable {

    private static final long serialVersionUID = -53888996103229349L;

    
    private Long id;

    /**
    * 付款单id
    */
    private Long payOrderId;

    /**
    * 客户id
    */
    private Long customerId;

    /**
    * 总支付金额,单位分
    */
    private Long totalAmount;

    /**
    * 退款金额,单位分
    */
    private Long refundAmount;

    /**
    * 第三方订单号
    */
    private String thirdOrderNo;

    /**
    * 退款状态(0,待审核1,退款中,2,等待响应3,退款成功4,退款失败)
    */
    private Integer status;

    /**
    * 退款方式(1,原生退款2,微信退款3,支付宝退款4,其他)
    */
    private Integer refundType;

    
    private LocalDateTime finishTime;

    
    private LocalDateTime createdTime;

    
    private LocalDateTime lastModifyTime;

    /**
    * 请求反馈结果
    */
    private String serverResponse;

    /**
    * 订单ids
    */
    private String orderIds;

    /**
    * 对应账户信息
    */
    private String accountInfo;

    /**
    * 第三方退款单号(如有)
    */
    private String thirdRefundNo;

    /**
    * 使用的公众号appid
    */
    private String appId;

    /**
    * 付款单编号
    */
    private String payOrderNumber;

    /**
    * 退款状态描述
    */
    private String statusDesc;

    /**
    * 退款备注
    */
    private String refundMark;

    /**
    * 订单大类型, 10: 小白营或进阶课, 20: 直播报名
    */
    private Integer orderType;

    /**
    * 1 线上 0 线下
    */
    private Integer online;

    /**
    * 操作人标识
    */
    private Long operatorId;

    /**
    * 附加字段
    */
    private String attach;

    /**
    * 一级原因
    */
    private Integer reasonFirst;

    /**
    * 二级原因
    */
    private Integer reasonSecond;

}
