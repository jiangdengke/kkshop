package com.jiangdk.form;

import lombok.Data;

import java.util.List;

/**
 * @author: JiangDk
 * @date: 2024/11/27 14:06
 * @description:
 */
@Data
public class BuyerOrderForm {
    private String name;
    private String phone;
    private String address;
    private Integer id;
    private List<ProductForm> items;
}
