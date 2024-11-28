package com.jiangdk.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangdk.entity.OrderDetail;
import com.jiangdk.entity.OrderMaster;
import com.jiangdk.entity.ProductInfo;
import com.jiangdk.feign.ProductFeign;
import com.jiangdk.form.BuyerOrderForm;
import com.jiangdk.form.ProductForm;
import com.jiangdk.service.OrderDetailService;
import com.jiangdk.service.OrderMasterService;
import com.jiangdk.util.ResultVOUtil;
import com.jiangdk.vo.OrderDetailVO;
import com.jiangdk.vo.OrderMasterVO;
import com.jiangdk.vo.ResultVO;
import io.swagger.models.auth.In;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForReadableInstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    /**
     * 获取订单列表
     * @param buyerId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{buyerId}/{page}/{size}")
    public ResultVO list(
            @PathVariable("buyerId") Integer buyerId,
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size
    ){
        Page<OrderMaster> orderMasterPage = new Page<>(page, size);
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buyer_id",buyerId);
        Page<OrderMaster> resultPage = orderMasterService.page(orderMasterPage,queryWrapper);
        return ResultVOUtil.success(resultPage.getRecords());
    }
    /**
     * 查询订单详请
     */
    @GetMapping("/detail/{buyerId}/{orderId}")
    public ResultVO detail(
            @PathVariable("buyerId") Integer buyerId,
            @PathVariable("orderId") String orderId
    ){
        // 查询主表
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buyer_openid",buyerId);
        queryWrapper.eq("order_id",orderId);
        OrderMaster orderMaster = orderMasterService.getOne(queryWrapper);
        // 查询从表
        QueryWrapper<OrderDetail> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("order_id",orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper1);
        // 封装返回对象
        OrderMasterVO orderMasterVO = new OrderMasterVO();
        BeanUtils.copyProperties(orderMaster,orderMasterVO);
        ArrayList<OrderDetailVO> orderDetailVOArrayList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            OrderDetailVO orderDetailVO = new OrderDetailVO();
            BeanUtils.copyProperties(orderDetail,orderDetailVO);
            orderDetailVOArrayList.add(orderDetailVO);
        }
        orderMasterVO.setOrderDetailList(orderDetailVOArrayList);
        return ResultVOUtil.success(orderMasterVO);
    }
    /**
     * 取消订单
     */
    @PutMapping("/cancel/{buyerId}/{orderId}")
    public ResultVO cancel(
           @PathVariable("buyerId") Integer buyerId,
           @PathVariable("orderId") String orderId
    ){
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buyer_openid",buyerId);
        queryWrapper.eq("order_id",orderId);
        OrderMaster orderMaster = orderMasterService.getOne(queryWrapper);
        orderMaster.setOrderStatus(2);
        orderMasterService.updateById(orderMaster);
        return ResultVOUtil.success(null);
    }
    /**
     * 完结订单
     */
    @PutMapping("/finish/{orderId}")
    public ResultVO finish(
            @PathVariable("orderId") String orderId
    ){
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        OrderMaster orderMaster = orderMasterService.getOne(queryWrapper);
        orderMaster.setOrderStatus(1);
        orderMasterService.updateById(orderMaster);
        return ResultVOUtil.success(null);
    }
    /**
     * 支付订单
     */
    @PutMapping("/pay/{buyerId}/{orderId}")
    public ResultVO pay(
            @PathVariable("buyerId")Integer buyerId,
            @PathVariable("orderId") String orderId
    ){
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buyer_id",buyerId);
        queryWrapper.eq("order_id",orderId);
        OrderMaster orderMaster = orderMasterService.getOne(queryWrapper);
        orderMaster.setPayStatus(1);
        orderMasterService.updateById(orderMaster);
        return ResultVOUtil.success(null);
    }
}

