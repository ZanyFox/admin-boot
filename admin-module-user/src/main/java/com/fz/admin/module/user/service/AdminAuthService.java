package com.fz.admin.module.user.service;

import com.fz.admin.module.user.model.param.PasswordLoginParam;
import com.fz.admin.module.user.model.vo.AuthLoginRespVO;

public interface AdminAuthService {
    AuthLoginRespVO passwordLogin(PasswordLoginParam param);
}
