package com.kl.web.starter.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.kl.common.constants.CommonConstants;
import com.kl.common.constants.RunMode;
import com.kl.web.starter.aop.IgnorePartnerAop;
import com.kl.web.starter.filter.CorsFilter;
import com.kl.web.starter.filter.PartnerNumberFilter;
import com.kl.web.starter.filter.ResourceLogFilter;
import com.kl.web.starter.filter.TraceIdFilter;
import com.kl.web.starter.handle.WebsiteExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.service.Parameter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    private String mode = null;

    @Value("${server.context-path:}")
    private String path = null;

    /**
     * 请求ID字段
     */
    @Value("${kl.requestId.header:Stgw-request-id}")
    private String requestIdHeader;

    @Bean
    public IgnorePartnerAop buildIgnorePartnerAop() {
        return new IgnorePartnerAop();
    }

    /**
     * 异常处理器
     *
     * @return
     */
    @Bean
    public WebsiteExceptionHandler buildWebsiteExceptionHandler() {
        return new WebsiteExceptionHandler();
    }

    /**
     * 自定义接口规则
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        if (StringUtils.isNotEmpty(path)) {
            configurer.addPathPrefix(path, c -> c.isAnnotationPresent(RestController.class) || c.isAnnotationPresent(Controller.class));
        }
    }

    @Bean
    public FilterRegistrationBean registerCorsFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CorsFilter());
        registration.addUrlPatterns("/*");
        registration.setName("registerCorsFilter");
        registration.setOrder(-1);  //值越小，Filter越靠前。
        return registration;
    }

    @Bean
    public FilterRegistrationBean registerTraceIdFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new TraceIdFilter());
        registration.addUrlPatterns("/*");
        registration.setName("registerTraceIdFilter");
        registration.setOrder(1);  //值越小，Filter越靠前。
        return registration;
    }

    /**
     * 日志过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean registerResourceLogFilter() {
        FilterRegistrationBean<ResourceLogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ResourceLogFilter(requestIdHeader, mode));
        registration.addUrlPatterns("/*");
        registration.setName("resourceLogFilter");
        // 值越小，Filter越靠前。
        registration.setOrder(2);
        return registration;
    }


    /**
     * 商户过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean registerPartherNumberFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new PartnerNumberFilter());
        registration.addUrlPatterns("/*");
        registration.setName("partnerIdFilter");
        registration.setOrder(3);  //值越小，Filter越靠前。
        return registration;
    }

    /**
     * 使用fastJson 转换
     * 字段强转string使用注解方式  @JSONField(serializeUsing = ToStringSerializer.class)
     *
     * @return
     */
    @Bean
    public HttpMessageConverters fastJsonMessageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        //需要定义一个convert转换消息的对象;
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //添加fastJson的配置信息;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        //fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        //全局时间配置
//        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
//        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        //在convert中添加配置信息.
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);

        converters.add(0, fastConverter);
        return new HttpMessageConverters(converters);
    }
}