package com.jiangdk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: JiangDk
 * @date: 2024/11/26 15:12
 * @description:
 */
@SpringBootApplication
@MapperScan("com.jiangdk.mapper")
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class,args);
    }
}
