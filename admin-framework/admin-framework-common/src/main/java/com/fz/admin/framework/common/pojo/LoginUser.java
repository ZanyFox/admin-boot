package com.fz.admin.framework.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过token缓存到redis的用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    private Long id;


    /**
     * 过期时间 = 创建缓存时间 + 有效期
     */
    private Long expiredAt;


    /**
     * 上下文字段，不进行持久化
     */
    @JsonIgnore
    private Map<String, Object> context;

    public void setContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    public Object getContext(String key) {
        return context.get(key);
    }

}
