package com.fz.admin.module.user.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserPermMenuInfoVO {

    private Long id;

    private String username;

    private String avatar;

    private List<String> roles;

    private List<String> permissions;
}
