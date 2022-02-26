package com.kl.db.starter.config;


import lombok.Data;

import java.util.List;

/**
 * 忽略合作方信息的表
 */
@Data
public class IgnorePartnerTableConfig {
    public static List<String> IGNORE_TABLES;
}
