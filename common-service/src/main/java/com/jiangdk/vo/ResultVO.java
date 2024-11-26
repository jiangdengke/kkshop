package com.jiangdk.vo;

import lombok.Data;

/**
 * @author: JiangDk
 * @date: 2024/11/26 14:41
 * @description:
 */

@Data
public class ResultVO<T> {
    private Integer code;
    private String msg;
    private T data;
}

