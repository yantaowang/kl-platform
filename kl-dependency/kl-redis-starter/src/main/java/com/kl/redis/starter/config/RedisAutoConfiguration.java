package com.kl.redis.starter.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.kl.core.util.JsonUtils;
import com.kl.redis.starter.lock.LockService;
import com.kl.redis.starter.lock.RedisLockServiceImpl;
import com.kl.redis.starter.serializer.PartnerStringRedisSerializer;
import com.kl.redis.starter.service.RedisLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.kl.redis.starter.service.RedisCommand;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * reids配置类
 */

@Configuration
@ConditionalOnProperty(name = "spring.redis.host")
@Slf4j
public class RedisAutoConfiguration<K, V> {

    @Value("${spring.profiles.active}")
    private String mode = null;


    @Value("${ewp.partner.enable:true}")
    private String enablePartner;


    @Autowired
    private RedisProperties redisProperties;

    @Bean
//    @RefreshScope
    public LettuceConnectionFactory buildLettuceConnectionFactory() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        genericObjectPoolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        genericObjectPoolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        genericObjectPoolConfig.setMaxWaitMillis(redisProperties.getLettuce().getPool().getMaxWait().toMillis());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        LettucePoolingClientConfiguration poolingClientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(genericObjectPoolConfig).build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, poolingClientConfiguration);
        //lettuceConnectionFactory.afterPropertiesSet();
        log.info("[REDIS_INIT] 初始化redis factory,host{},port:{}", redisProperties.getHost(), redisProperties.getPort());
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, V> redisTemplate(LettuceConnectionFactory lqlcfactory) {
        RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
        return buildRedisTemplate(lqlcfactory, redisTemplate);
    }

    /**
     * redis 工具bean
     *
     * @param redisTemplate
     * @param stringRedisTemplate
     * @return
     */
    @Bean
    public RedisCommand redisCacheService(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        return new RedisCommand(redisTemplate, stringRedisTemplate, false);
    }

    /**
     * redis 限流
     *
     * @param stringRedisTemplate
     * @return
     */
    @Bean
    public RedisLimiter redisLimiter(StringRedisTemplate stringRedisTemplate) {
        return new RedisLimiter(stringRedisTemplate);
    }

    /**
     * jackson 序列化
     *
     * @return
     */
    private Jackson2JsonRedisSerializer buildJackson2JsonRedisSerializer() {
        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 是否使用jackson注解
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        // 未知属性是否报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 空对象是否报错
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        // 序列化所有非空属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // objectMapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build());
        // 指定日期序列化格式 yyyy-MM-dd HH:mm:ss.SSS
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(JsonUtils.DateFormatStr.NORMAL_MILLISECOND.getDateFormat())));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(JsonUtils.DateFormatStr.NORMAL_MILLISECOND.getDateFormat())));
        objectMapper.registerModule(javaTimeModule);
        DateFormat DATE_FORMAT = new SimpleDateFormat(JsonUtils.DateFormatStr.NORMAL_MILLISECOND.getDateFormat());
        objectMapper.setDateFormat(DATE_FORMAT);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }

    /**
     * redis 分布式锁
     *
     * @param stringRedisTemplate
     * @return
     */
    @Bean
    public LockService buildLockService(StringRedisTemplate stringRedisTemplate) {
        return new RedisLockServiceImpl(stringRedisTemplate);
    }

    /**
     * 该bean只是用来存取字符串类型
     *
     * @param lqlcfactory
     * @return
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lqlcfactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        if ("true".equals(enablePartner)) {
            log.info("[EnablePartnerInterceptor]启用redis多合作方拦截插件");
            redisTemplate.setKeySerializer(new PartnerStringRedisSerializer(mode));
        } else {
            redisTemplate.setKeySerializer(new StringRedisSerializer());
        }
        lqlcfactory.setShareNativeConnection(false);
        redisTemplate.setConnectionFactory(lqlcfactory);
        return redisTemplate;
    }

    /**
     * 通用RedisTemplate 基于jackson
     *
     * @param lqlcfactory
     * @param redisTemplate
     * @return
     */
    private RedisTemplate<String, V> buildRedisTemplate(LettuceConnectionFactory lqlcfactory, RedisTemplate<String, V> redisTemplate) {
        lqlcfactory.setShareNativeConnection(false);
        redisTemplate.setConnectionFactory(lqlcfactory);
        // 设置value的序列化规则和 key的序列化规则
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = buildJackson2JsonRedisSerializer();
        if ("true".equals(enablePartner)) {
            log.info("[EnablePartnerInterceptor]启用redis多合作方拦截插件");
            redisTemplate.setKeySerializer(new PartnerStringRedisSerializer(mode));
        } else {
            redisTemplate.setKeySerializer(new StringRedisSerializer());
        }
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        log.info("[REDIS_INIT] redis初始化完成");
        return redisTemplate;
    }

    /**
     * 对hash类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public HashOperations<String, String, V> hashOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 对redis字符串类型数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ValueOperations<String, V> valueOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 对链表类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ListOperations<String, V> listOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 对无序集合类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public SetOperations<String, V> setOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 对有序集合类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ZSetOperations<String, V> zSetOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    /**
     * 基数统计
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public HyperLogLogOperations<String, V> hyperLogLogOperations(RedisTemplate<String, V> redisTemplate) {
        return redisTemplate.opsForHyperLogLog();
    }
}