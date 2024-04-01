package com.fz.admin.module.user.enums;

import com.fz.admin.framework.common.enums.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 性别的枚举值
 *
 *
 */
@Getter
@AllArgsConstructor
public enum GenderEnum implements IntArrayValuable {

    /**
     * 男
     */
    MALE(0),
    /**
     * 女
     */
    FEMALE(1),

    /**
     * 未知
     */
    UNKNOWN(2);

    /**
     * 性别
     */
    private final Integer gender;

    @Override
    public int[] array() {
        return Arrays.stream(values()).mapToInt(GenderEnum::getGender).toArray();
    }
}
