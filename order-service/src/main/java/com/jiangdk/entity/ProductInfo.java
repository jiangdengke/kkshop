package com.jiangdk.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author JiangDK
 * @since 2024-11-26
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    public class ProductInfo implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "product_id", type = IdType.AUTO)
      private Integer productId;

      /**
     * 商品名称
     */
      private String productName;

      /**
     * 商品单价
     */
      private BigDecimal productPrice;

      /**
     * 库存
     */
      private Integer productStock;

      /**
     * 描述
     */
      private String productDescription;

      /**
     * 小图
     */
      private String productIcon;

      /**
     * 类目编号
     */
      private Integer categoryType;

      /**
     * 创建时间
     */
        @TableField(fill = FieldFill.INSERT)
      private LocalDateTime createTime;

      /**
     * 修改时间
     */
        @TableField(fill = FieldFill.INSERT_UPDATE)
      private LocalDateTime updateTime;

      /**
     * 商品状态，1正常0下架
     */
      private Integer productStatus;


}
