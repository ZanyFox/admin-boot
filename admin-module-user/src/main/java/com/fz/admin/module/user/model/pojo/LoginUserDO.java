package com.fz.admin.module.user.model.pojo;

import com.fz.admin.module.user.model.entity.SysMenu;
import com.fz.admin.module.user.model.entity.SysRole;
import lombok.Data;

import java.util.List;

@Data
public class LoginUserDO {

    private Long userId;

    private String password;

    private String username;

    private Integer status;

    private Integer deleted;

    private List<SysRole> roles;

    private List<SysMenu> menus;
}
