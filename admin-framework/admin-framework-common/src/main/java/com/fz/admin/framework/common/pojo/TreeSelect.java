package com.fz.admin.framework.common.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Trees 树结构实体类
 */
@Data
public class TreeSelect implements Serializable {


    /**
     * 节点ID
     */
    private Long id;

    private Long parentId;

    /**
     * 节点名称
     */
    private String label;
    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

}
