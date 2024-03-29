package com.kl.common.dto.model;

import lombok.Data;

import java.util.Date;

@Data
public class JwtTokenInfo {
    // 用户id
    private String uid;
    // 用户名 可以为空
    private String username;
    // 合作方标识n
    private int partnerCode;
    // token 创建时间（毫秒）
    private Date createTime;
    // validityTime 有效时长（分钟）
    private int validityTime;

    public JwtTokenInfo(String uid, int partnerCode, Date createTime, int validityTime) {
        this.uid = uid;
        this.partnerCode = partnerCode;
        this.createTime = createTime;
        this.validityTime = validityTime;
    }

    public JwtTokenInfo(String uid, String username, int partnerCode, Date createTime, int validityTime) {
        this.uid = uid;
        this.username = username;
        this.partnerCode = partnerCode;
        this.createTime = createTime;
        this.validityTime = validityTime;
    }

    public JwtTokenInfo() {
    }
}