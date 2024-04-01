package com.fz.admin.framework.common.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageParam implements Serializable {


    public static final Integer PAGE_SIZE_NONE = -1;

    /**
     * 每页数据大小
     */
    private Integer size = 10;

    /**
     * 当前页码
     */
    private Integer page = 1;

    /**
     * 顺序 ASC DESC
     */
    private String order = SortingField.ORDER_ASC;


    /**
     * 排序字段
     */
    private String sort;

}


