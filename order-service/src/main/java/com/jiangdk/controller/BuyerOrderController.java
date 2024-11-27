package com.jiangdk.controller;


import com.jiangdk.entity.OrderDetail;
import com.jiangdk.entity.OrderMaster;
import com.jiangdk.entity.ProductInfo;
import com.jiangdk.feign.ProductFeign;
import com.jiangdk.form.BuyerOrderForm;
import com.jiangdk.form.ProductForm;
import com.jiangdk.service.OrderDetailService;
import com.jiangdk.service.OrderMasterService;
import com.jiangdk.util.ResultVOUtil;
import com.jiangdk.vo.ResultVO;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 订单详情表 前端控制器
 * </p>
 *
 * @author JiangDK
 * @since 2024-11-26
 */
@RestController
@RequestMapping("/buyer/order")
public class BuyerOrderController {
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private OrderMasterService orderMasterService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 创建订单
     * @param buyerOrderForm
     * @return
     */
    @PostMapping("/create")
    public ResultVO create(
            @RequestBody BuyerOrderForm buyerOrderForm
    ){
        OrderMaster orderMaster = new OrderMaster();
        orderMaster.setBuyerName(buyerOrderForm.getName());
        orderMaster.setBuyerPhone(buyerOrderForm.getPhone());
        orderMaster.setBuyerAddress(buyerOrderForm.getAddress());
        orderMaster.setBuyerOpenid(buyerOrderForm.getId());
        // 计算总价
        List<ProductForm> items = buyerOrderForm.getItems();
        BigDecimal amount = new BigDecimal(0);
        for (ProductForm item : items) {
            Integer productId = item.getProductId();
            BigDecimal price = this.productFeign.findPriceById(productId);
            Integer quantity = item.getProductQuantity();
            BigDecimal multiply = price.multiply(new BigDecimal(quantity));// 将数量转为BigDecimal类型与价格相乘。
            amount = amount.add(multiply);
        }
        orderMaster.setOrderAmount(amount);
        // 向主表添加数据
        orderMasterService.save(orderMaster);
        // 向从表添加数据
        for (ProductForm item : items) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderMaster.getOrderId());
            orderDetail.setProductId(item.getProductId());
            ProductInfo productInfo = productFeign.findById(item.getProductId());
            orderDetail.setProductName(productInfo.getProductName());
            orderDetail.setProductPrice(productInfo.getProductPrice());
            orderDetail.setProductQuantity(item.getProductQuantity());
            orderDetail.setProductIcon(productInfo.getProductIcon());
            orderDetailService.save(orderDetail);
            // 减库存
            productFeign.subStock(item.getProductId(),item.getProductQuantity());
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", orderMaster.getOrderId());
        return ResultVOUtil.success(hashMap);
    }

}

