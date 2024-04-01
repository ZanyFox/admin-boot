package com.fz.admin.framework.web.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fz.admin.framework.common.config.jackson.LocalDateTimeDeserializer;
import com.fz.admin.framework.common.config.jackson.LocalDateTimeSerializer;
import com.fz.admin.framework.common.config.jackson.NumberSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Configuration(proxyBeanMethods = false)
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {


        SimpleModule simpleModule = new SimpleModule();
        simpleModule
                // 新增 Long 类型序列化规则，数值超过 2^53-1，在 JS 会出现精度丢失问题，因此 Long 自动序列化为字符串类型
                .addSerializer(Long.class, NumberSerializer.INSTANCE)
                .addSerializer(Long.TYPE, NumberSerializer.INSTANCE)

                .addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE)
                .addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE)

                .addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE)
                .addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE)
                // LocalDateTime 序列化成时间戳
                .addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE)
                .addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);

        return Jackson2ObjectMapperBuilder ->
                Jackson2ObjectMapperBuilder
                        .featuresToDisable(
                                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                SerializationFeature.FAIL_ON_EMPTY_BEANS,
                                DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,
                                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                                // 设置不使用 nanoseconds 的格式。例如说 1611460870.401，而是直接 1611460870401
                                SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
                        )
                        .featuresToEnable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                        .modules(simpleModule)
                        .serializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
