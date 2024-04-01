package com.fz.admin.module.user.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {

    private String accessToken;

    private Long validity;
}
