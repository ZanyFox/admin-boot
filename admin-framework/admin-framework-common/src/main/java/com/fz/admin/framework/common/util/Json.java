package com.fz.admin.framework.common.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fz.admin.framework.common.config.jackson.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Slf4j
public class Json {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    static {

        Module module = new SimpleModule()
                .addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE)
                .addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);


        OBJECT_MAPPER
                .registerModules(new JavaTimeModule(), new Jdk8Module(), module)
                .disable(
                        SerializationFeature.FAIL_ON_EMPTY_BEANS,
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                )

                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    /**
     * 对象转json
     *
     * @param object 对象
     * @return json
     */
    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("[toJsonString]: {}", e.getMessage());
        }
        return "";
    }


    public static <T> T parseObject(String json, JavaType type) {
        if (json == null) {
            return null;
        }
        T result = null;
        try {
            result = OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            log.error("parseObject() error: {}", e.getMessage());
        }
        return result;
    }


    public static <T> T parseObject(String json, TypeReference<T> valueTypeRef) {
        if (json == null) {
            return null;
        }
        T result = null;
        try {
            TypeFactory typeFactory = TypeFactory.defaultInstance();
            JavaType javaType = typeFactory.constructType(valueTypeRef);
            result = OBJECT_MAPPER.readValue(json, javaType);
        } catch (Exception e) {
            log.error("parseObject() error: {}", e.getMessage());
        }
        return result;
    }


    /**
     * json转换换成对象
     *
     * @param json  json
     * @param clazz clazz
     * @return Class
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        T result = null;
        try {
            result = OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("parseObject() error: {}", e.getMessage());
        }
        return result;
    }

    /**
     * json转换换成对象
     *
     * @param src   src
     * @param clazz clazz
     * @return Class
     */
    public static <T> T parseObject(byte[] src, Class<T> clazz) {
        T result = null;
        try {
            result = OBJECT_MAPPER.readValue(src, clazz);
        } catch (Exception e) {
            log.error("parseObject() error: {}", e.getMessage());
        }
        return result;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /*
     *
     * https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
     * * List<MyClass> myObjects = Arrays.asList(mapper.readValue(json, MyClass[].class))
     * * works up to 10 time faster than TypeRefence.
     *
     * @return List数组
     */
    public static <T> List<T> parseArray(String json, Class<T[]> clazz) {
        if (json == null) {
            return null;
        }
        T[] result = null;
        try {
            result = OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("parseArray() error: {}", e.getMessage());
        }
        if (result == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(result);
    }

    public static <T> List<T> parseArray(byte[] src, Class<T[]> clazz) {
        T[] result = null;
        try {
            result = OBJECT_MAPPER.readValue(src, clazz);
        } catch (Exception e) {
            log.error("parseArray() error: {}", e.getMessage());
        }
        if (result == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(result);
    }


    /**
     * 转换成json节点，即map
     *
     * @param jsonStr jsonStr
     * @return JsonNode
     */
    public static JsonNode parseJson(String jsonStr) {
        if (jsonStr == null) {
            return null;
        }
        JsonNode jsonNode = null;
        try {
            jsonNode = OBJECT_MAPPER.readTree(jsonStr);
        } catch (Exception e) {
            log.error("parseJson() error: {}", e.getMessage());
        }
        return jsonNode;
    }

}
