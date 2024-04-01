package com.fz.admin.framework.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 通用状态枚举
 *
 *
 */
@Getter
@AllArgsConstructor
public enum CommonStatusEnum implements IntArrayValuable{

    ENABLE(0, "开启"),
    DISABLE(1, "关闭");


    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CommonStatusEnum::getStatus).toArray();

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;


    public static boolean isEnable(Integer status) {
        return Objects.equals(ENABLE.status, status);
    }

    public static boolean isDisable(Integer status) {
        return Objects.equals(DISABLE.status, status);
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
