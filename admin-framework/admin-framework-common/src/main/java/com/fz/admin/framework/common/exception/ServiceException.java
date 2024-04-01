package com.fz.admin.framework.common.exception;


import com.fz.admin.framework.common.enums.ServRespCode;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private ServRespCode respEnum;

    private Integer code;


    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }


    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ServiceException(ServRespCode respEnum) {
        super(respEnum.getMsg());
        this.respEnum = respEnum;
    }

    public ServRespCode getServCode() {
        return respEnum;
    }
}
