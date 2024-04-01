package com.fz.admin.framework.common.pojo;

import com.fz.admin.framework.common.enums.ServRespCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class ServRespEntity<T> implements Serializable {

    private Integer code;


    private String message;

    private T data;


    public static <T> ServRespEntity<T> success(String msg, T data) {
        ServRespEntity<T> servRespEntity = new ServRespEntity<>();
        servRespEntity.setCode(ServRespCode.SUCCESS.getCode());
        servRespEntity.setMessage(msg);
        servRespEntity.setData(data);
        return servRespEntity;
    }

    public static <T> ServRespEntity<T> success(String msg) {
        ServRespEntity<T> servRespEntity = new ServRespEntity<>();
        servRespEntity.setCode(ServRespCode.SUCCESS.getCode());
        servRespEntity.setMessage(msg);
        return servRespEntity;
    }

    public static <T> ServRespEntity<T> success() {
        ServRespEntity<T> servRespEntity = new ServRespEntity<>();
        servRespEntity.setCode(ServRespCode.SUCCESS.getCode());
        servRespEntity.setData(null);
        return servRespEntity;
    }

    public static <T> ServRespEntity<T> success(T data) {
        ServRespEntity<T> servRespEntity = new ServRespEntity<>();
        servRespEntity.setCode(ServRespCode.SUCCESS.getCode());
        servRespEntity.setData(data);
        return servRespEntity;
    }

    public static ServRespEntity<Void> fail(Integer code, String message) {
        ServRespEntity<Void> servRespEntity = new ServRespEntity<>();
        servRespEntity.setCode(code);
        servRespEntity.setMessage(message);
        return servRespEntity;
    }

    public static <T> ServRespEntity<T> fail(ServRespCode servRespCode, T data) {
        ServRespEntity<T> servRespEntity = new ServRespEntity<>();
        servRespEntity.setCode(servRespCode.getCode());
        servRespEntity.setMessage(servRespCode.getMsg());
        servRespEntity.setData(data);
        return servRespEntity;
    }

    public static <T> ServRespEntity<T> fail(ServRespCode servRespCode) {
        ServRespEntity<T> servRespEntity = new ServRespEntity<>();
        servRespEntity.setCode(servRespCode.getCode());
        servRespEntity.setMessage(servRespCode.getMsg());
        servRespEntity.setData(null);
        return servRespEntity;
    }

}
