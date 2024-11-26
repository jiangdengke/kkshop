package com.jiangdk.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiangdk.entity.ProductCategory;
import com.jiangdk.entity.ProductInfo;
import com.jiangdk.exception.ShopException;
import com.jiangdk.result.ResponseEnum;
import com.jiangdk.service.ProductCategoryService;
import com.jiangdk.service.ProductInfoService;
import com.jiangdk.util.ResultVOUtil;
import com.jiangdk.vo.ProductCategoryVO;
import com.jiangdk.vo.ProductInfoVO;
import com.jiangdk.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 类目表 前端控制器
 * </p>
 *
 * @author JiangDK
 * @since 2024-11-26
 */
@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {

    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductInfoService productInfoService;

    /**
     * 更新库存信息
     * @param id
     * @param quantity
     * @return
     */
    @PutMapping("/subStockById/{id}/{quantity}")
    public boolean subStockById(
            @PathVariable("id") Integer id,
            @PathVariable("quantity") Integer quantity){
        ProductInfo productInfo = productInfoService.getById(id);
        Integer productStock = productInfo.getProductStock();
        Integer result = productStock-quantity;
        if (result < 0){
            throw new ShopException(ResponseEnum.PRODUCT_STOCK_ERROR.getMsg());
        }
        productInfo.setProductStock(result);
        return this.productInfoService.updateById(productInfo);
    }
    /**
     * 根据id查商品信息
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public ProductInfo findById(@PathVariable("id")Integer id){
        ProductInfo productInfo = productInfoService.getById(id);
        return productInfo;
    }
    /**
     * 根据id查商品价格
     * @param id
     * @return
     */
    @GetMapping("/findPriceById/{id}")
    public BigDecimal findPriceById(@PathVariable("id") Integer id){
        ProductInfo productInfo = productInfoService.getById(id);
        return productInfo.getProductPrice();
    }

    /**
     * 查商品列表
     * @return
     */
    @GetMapping("list")
    public ResultVO list(){
        List<ProductCategory> productCategoryList = productCategoryService.list();
        List<ProductCategoryVO> list = new ArrayList<>();
        for (ProductCategory productCategory : productCategoryList) {
            ProductCategoryVO productCategoryVO = new ProductCategoryVO();
            productCategoryVO.setName(productCategory.getCategoryName());
            productCategoryVO.setType(productCategory.getCategoryType());
            // 拿到goods
            QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("category_type",productCategory.getCategoryType());
            List<ProductInfo> productInfoList = productInfoService.list(queryWrapper);
            List<ProductInfoVO> list2 = new ArrayList<>();
            for (ProductInfo productInfo : productInfoList) {
                ProductInfoVO productInfoVO = new ProductInfoVO();
                BeanUtils.copyProperties(productInfo,productInfoVO);
                list2.add(productInfoVO);
            }
            productCategoryVO.setGoods(list2);
            list.add(productCategoryVO);
        }
        return ResultVOUtil.success(list);
    }
}

