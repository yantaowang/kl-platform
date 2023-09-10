/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kl.monitor.starter.condition;

import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.text.StrPool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * {@link Condition} that match based on monitor with name enabled
 *
 * @author xinminghao
 */
public class MonitorEnableCondition implements Condition {

    private static final String ENABLE_PROPERTY = "kl.monitor.enabled";
    private static final String ENABLE_PROPERTY_ENABLED = "true";
    private static final String INCLUDE_PROPERTY = "kl.monitor.include";
    private static final String INCLUDE_PROPERTY_ALL = "*";

    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String enabled = environment.getProperty(ENABLE_PROPERTY);
        if (!ENABLE_PROPERTY_ENABLED.equals(enabled)) {
            return false;
        }
        String includesStr = getIncludes(environment);
        MultiValueMap<String, Object> attrs = metadata
                .getAllAnnotationAttributes(ConditionalOnMonitorEnable.class.getName());
        List<Object> value = null;
        if (attrs != null) {
            value = attrs.get("value");
        }
        if (value == null) {
            return false;
        }
        Object[] objects = value.stream().flatMap((Function<Object, Stream<Object>>) valueItem -> {
            if (valueItem instanceof String[]) {
                String[] valueArray = (String[]) valueItem;
                return Arrays.stream(valueArray);
            }
            return StreamUtil.of(valueItem);
        }).distinct().toArray();
        return matches(includesStr, objects);
    }

    public static String getIncludes(Environment environment) {
        return environment.getProperty(INCLUDE_PROPERTY);
    }

    public static <T> boolean matches(String includesStr, T... value) {
        if (StringUtils.isBlank(includesStr)) {
            return false;
        }
        List<String> includes = Arrays.asList(includesStr.split(StrPool.COMMA));
        if (includes.contains(INCLUDE_PROPERTY_ALL)) {
            return true;
        }
        for (Object item : value) {
            if (includes.contains(item.toString())) {
                return true;
            }
        }
        return false;
    }
}
