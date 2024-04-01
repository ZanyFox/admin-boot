package com.fz.admin.framework.redis.constant;

public interface RedisCacheConstant {

    String USER_PREFIX = "user:";

    String USER_TOKEN_PREFIX = USER_PREFIX + "token:";

    String USER_ROLE_IDS = USER_PREFIX + "role_ids:";

    String ROLE = "role:";
}
