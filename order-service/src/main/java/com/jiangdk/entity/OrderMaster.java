package com.jiangdk.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author JiangDK
 * @since 2024-11-26
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    public class OrderMaster implements Serializable {

    private static final long serialVersionUID=1L;


      @TableId(type = IdType.ASSIGN_UUID) // 设置id自增
      private String orderId;

      /**
     * 买家名字
     */
      private String buyerName;

      /**
     * 买家电话
     */
      private String buyerPhone;

      /**
     * 买家地址
     */
      private String buyerAddress;

      /**
     * 买家id
     */
      private Integer buyerOpenid;

      /**
     * 订单总金额
     */
      private BigDecimal orderAmount;

      /**
     * 订单状态，默认0新下单,1完成，2取消
     */
      private Integer orderStatus = 0;

      /**
     * 支付状态，默认0未支付，1已支付
     */
      private Integer payStatus = 0;

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


}
