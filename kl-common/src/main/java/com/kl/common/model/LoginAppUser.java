package com.kl.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户实体绑定spring security
 */
@Getter
@Setter
public class LoginAppUser {
    private static final long serialVersionUID = -3685249101751401211L;

    private Set<String> permissions;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 渠道id
     */
    private Long tenantId;

    /**
     * 超级管理员
     */
    private boolean superAdmin;

    /**
     * 角色类型
     */
    private Integer roleType;

    /**
     * 员工部门及其子部门id列表
     */
    private List<Long> departmentIdList;
}
