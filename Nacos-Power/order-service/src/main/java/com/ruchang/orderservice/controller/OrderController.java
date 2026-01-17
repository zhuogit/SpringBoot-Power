package com.ruchang.orderservice.controller;//package com.ruchang.orderservice.controller;

import com.ruchang.common.entity.OrderTemp;
import com.ruchang.orderservice.feignClient.PointServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author : wolf
 */
@RestController
@RequestMapping(value = "order")
public class OrderController {

    @Autowired
    private PointServiceFeignClient pointServiceFeignClient;

    @GetMapping(value = "test")
    public String test() {
        return "This is order-service";
    }

    @PostMapping("/add")
    public String add(@RequestBody OrderTemp orderTemp) {
        return pointServiceFeignClient.addPoint(orderTemp);
    }
}
