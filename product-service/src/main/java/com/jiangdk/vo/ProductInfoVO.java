package com.jiangdk.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: JiangDk
 * @date: 2024/11/26 14:52
 * @description:
 */
@Data
public class ProductInfoVO {
    @JsonProperty("id")
    private Integer productId;
    @JsonProperty("name")
    private String productName;
    @JsonProperty("price")
    private BigDecimal productPrice;
    @JsonProperty("stock")
    private Integer productStock;
    @JsonProperty("description")
    private String productDescription;
    @JsonProperty("icon")
    private String productIcon;
    @JsonProperty("quantity")
    private Integer quantity = 0;
}
