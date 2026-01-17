package com.ruchang.pointservice.controller;


import com.ruchang.common.entity.OrderTemp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * @Author : wolf
 */
@RestController
@RequestMapping(value = "point")
@RefreshScope
public class PointController {

    @Value("${config.info}")
    private String configInfo;

    @GetMapping(value = "test")
    public String test() {
        return "This is point-service";
    }

    @GetMapping("config")
    public String config() {
        return configInfo;
    }

    @PostMapping("/add")
    public String add(@RequestBody OrderTemp orderTemp) {
        return "添加积分成功！商品名称为：" + orderTemp.getProductName();
    }

}
