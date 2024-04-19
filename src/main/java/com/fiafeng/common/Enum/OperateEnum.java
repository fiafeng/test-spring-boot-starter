package com.fiafeng.common.Enum;

import lombok.Getter;

@Getter
public enum OperateEnum {

    /**
     * 查询数据
     */
    query("查询"),
    /**
     * 修改数据
     */
    update("更新"),
    /**
     * 删除数据
     */
    deleted("删除"),
    /**
     * 新增数据
     */
    insert("新增"),

    ;


    String operate;

    OperateEnum(String operate) {
        this.operate = operate;
    }
}
