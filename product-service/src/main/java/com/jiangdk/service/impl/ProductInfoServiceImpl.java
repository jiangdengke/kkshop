package com.jiangdk.service.impl;

import com.jiangdk.entity.ProductInfo;
import com.jiangdk.mapper.ProductInfoMapper;
import com.jiangdk.service.ProductInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author JiangDK
 * @since 2024-11-26
 */
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements ProductInfoService {

}
