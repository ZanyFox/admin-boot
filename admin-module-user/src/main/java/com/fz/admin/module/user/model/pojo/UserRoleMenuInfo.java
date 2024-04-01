package com.fz.admin.module.user.model.pojo;

import com.fz.admin.module.user.model.entity.SysMenu;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleMenuInfo {

    private String userId;

    private List<String> roles;

    private List<SysMenu> menus;
}
