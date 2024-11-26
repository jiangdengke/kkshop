package com.jiangdk.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: JiangDk
 * @date: 2024/11/26 14:44
 * @description:
 */
@Data
public class ProductCategoryVO {
    private String name;
    private Integer type;
    private List goods;
}
