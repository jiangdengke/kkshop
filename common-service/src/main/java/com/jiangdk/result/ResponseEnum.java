package com.jiangdk.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  ResponseEnum {

    PRODUCT_STOCK_ERROR(300,"商品库存不足");

    private Integer code;
    private String msg;
}
