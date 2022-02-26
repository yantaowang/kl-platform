package com.kl.web.starter.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.kl.core.constants.CommonConstants;
import com.kl.core.constants.RunMode;
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
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.Parameter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableSwagger2
public class WebMvcAutoConfiguration implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    private String mode = null;

    @Value("${server.context-path:}")
    private String path = null;

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
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ResourceLogFilter());
        registration.addUrlPatterns("/*");
        registration.setName("resourceLogFilter");
        registration.setOrder(2);  //值越小，Filter越靠前。
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

    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 本例采用指定扫描的包路径来定义指定要建立API的目录。
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {
        //添加head参数配置start
        ParameterBuilder timestamp = new ParameterBuilder();
        ParameterBuilder token = new ParameterBuilder();
        ParameterBuilder signature = new ParameterBuilder();
        ParameterBuilder greenPass = new ParameterBuilder();
        timestamp.name("timestamp").description("时间戳").
                modelRef(new ModelRef("string")).
                parameterType("header")
                .required(false).build();
        token.name(CommonConstants.JWTModelKeyHeader.JWT_HEADER_PARAM).description("登录认证")
                .defaultValue(CommonConstants.JWTModelKeyHeader.JWT_HEADER_PARAM)
                .modelRef(new ModelRef("string")).
                parameterType("header").required(false).build();
        signature.name("signature").description("签名").
                modelRef(new ModelRef("string")).
                parameterType("header").required(false).build();
        greenPass.name("greenPass").description("开绿灯:true,免去签名和时间戳，只需token,仅测试环境用于swagger调试")
                .defaultValue("true")
                .modelRef(new ModelRef("string")).
                parameterType("header").required(false).build();
        List<Parameter> pars = new ArrayList<>();
        pars.add(token.build());
        pars.add(timestamp.build());
        pars.add(signature.build());
        pars.add(greenPass.build());
        log.info("swagger2========初始化当前运行环境{}，是否启用：{}", mode, RunMode.TEST.equals(mode) || RunMode.DEV.equals(mode));
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(RunMode.TEST.equals(mode) || RunMode.DEV.equals(mode))
                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.ewp.examples.web.controller"))
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .globalOperationParameters(pars).groupName("JAVA-API");
    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * 访问地址：http://项目实际地址/swagger-ui.html
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Java-API接口文档")
                .description("使用Swagger2构建，只在测试环境部署，生产环境不生效;\n\t" +
                        "前端开发、测试可通过本系统调试接口;\n\t" +
                        "swagger2注解详细说明:https://www.cnblogs.com/tangsong41/p/11197782.html")
                .termsOfServiceUrl("swagger2注解详细说明 https://www.cnblogs.com/tangsong41/p/11197782.html")
                .version("1.0")
                .build();
    }
}