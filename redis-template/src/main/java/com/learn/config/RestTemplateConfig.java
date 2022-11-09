package com.learn.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;

@Configuration
public class RestTemplateConfig {

//    @Bean
//    public RedisTemplate<String, Object> restTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
//        // key序列化
//        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
//        // value序列化
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        // 配置redisTemplate
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
//        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
//        redisTemplate.setKeySerializer(stringSerializer);// key序列化
//        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);// value序列化
//        redisTemplate.setHashKeySerializer(stringSerializer);// Hash key序列化
//        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);// Hash value序列化
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }


    //自定义json序列化
    public RedisSerializer jsonSerializer() {
        //1.定义Redis序列化,反序列化规范对象(此对象底层通过ObjectMapper完成对象序列化和反序列化)
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        //2.创建ObjectMapper(有jackson api库提供)对象,基于此对象进行序列化和反序列化
        //2.1创建ObjectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();
        //2.2设置按哪些方法规则进行序列化
        objectMapper.setVisibility(PropertyAccessor.GETTER,//get方法
                JsonAutoDetect.Visibility.ANY);//Any 表示任意方法访问修饰符
        //对象属性值为null时,不进行序列化存储
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //2.2激活序列化类型存储,对象序列化时还会将对象的类型存储到redis数据库
        //假如没有这个配置,redis存储数据时不存储类型,反序列化时会默认将其数据存储到map
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),//多态校验分析
                ObjectMapper.DefaultTyping.NON_FINAL,//激活序列化类型存储,类不能使用final修饰
                JsonTypeInfo.As.PROPERTY);//PROPERTY 表示类型会以json对象属性形式存储
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        //设置值的序列化方式
        template.setValueSerializer(jsonSerializer());
        template.setHashValueSerializer(jsonSerializer());
        //更新一下RedisTemplate对象的默认配置
        template.afterPropertiesSet();
        return template;
    }


}
