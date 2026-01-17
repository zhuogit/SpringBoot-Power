package com.ruchang.orderservice.feignClient;


import com.ruchang.common.entity.OrderTemp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Author : wolf
 */
@FeignClient(value = "point-service")
public interface PointServiceFeignClient {

    @PostMapping("/point/add")
    String addPoint(OrderTemp orderTemp);
}
