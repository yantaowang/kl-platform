package com.kl.db.starter.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.kl.common.context.LoginUserContextHolder;
import com.kl.common.model.LoginAppUser;
import com.kl.db.starter.properties.DataScopeProperties;

import javax.annotation.Resource;

/**
 * 个人权限的处理器
 *
 * @author jarvis create by 2023/1/10
 */
public class CreatorDataScopeSqlHandler implements SqlHandler{

    @Resource
    private DataScopeProperties dataScopeProperties;

    /**
     * 返回需要增加的where条件，返回空字符的话则代表不需要权限控制
     *
     * @return where条件
     * 如果角色是全部权限的话则不进行控制，如果是个人权限的话则自动加入create_id = user_id
     */
    @Override
    public String handleScopeSql() {
        LoginAppUser user = LoginUserContextHolder.getUser();
        Assert.notNull(user, "登陆人不能为空");
        //自定义角色受到部门数据权限控制
        if (!user.isSuperAdmin()) {
            return dataScopeProperties.getDepartmentIdColumnName() + " in (" + CollectionUtil.join(user.getDepartmentIdList(), ",") + ")";
        }
        if(!StrUtil.isBlank(dataScopeProperties.getCreatorIdColumnName())) {
            return String.format("%s = '%s'", dataScopeProperties.getCreatorIdColumnName(), user.getId());
        }
        return DO_NOTHING;
    }
}
