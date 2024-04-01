package com.fz.admin.module.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuTypeEnum {


    DIR(1, "目录", "M"),
    MENU(2, "菜单", "C"),
    BUTTON(3, "按钮", "F");

    private final Integer type;
    private final String msg;
    private final String key;


    public static boolean isDir(String key) {
        return DIR.getKey().equals(key);
    }

    public static boolean isMenu(String key) {
        return MENU.getKey().equals(key);
    }
}
