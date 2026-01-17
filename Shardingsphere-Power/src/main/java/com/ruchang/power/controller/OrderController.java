package com.ruchang.power.controller;

import com.ruchang.power.entity.Order;
import com.ruchang.power.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 订单REST控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * POST /api/orders
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam Long userId,
            @RequestParam String productName,
            @RequestParam BigDecimal amount) {

        try {
            Order order = orderService.createOrder(userId, productName, amount);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("data", order);
            result.put("orderId", order.getOrderId());
            result.put("orderNo", order.getOrderNo());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("创建订单失败: userId={}, productName={}", userId, productName, e);
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "创建订单失败: " + e.getMessage())
            );
        }
    }

    /**
     * 批量创建测试数据
     * POST /api/orders/batch-test
     */
    @PostMapping("/batch-test")
    public ResponseEntity<Map<String, Object>> batchTest() {
        try {
            orderService.batchCreateTestOrders();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "批量创建测试订单成功",
                    "stats", orderService.getSystemStats()
            ));
        } catch (Exception e) {
            log.error("批量创建测试订单失败", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("success", false, "message", "批量创建失败: " + e.getMessage())
            );
        }
    }

    /**
     * 查询订单详情
     * GET /api/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable Long orderId) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);

        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", orderOpt.get()
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "订单不存在"
            ));
        }
    }

    /**
     * 根据订单号查询
     * GET /api/orders/no/{orderNo}
     */
    @GetMapping("/no/{orderNo}")
    public ResponseEntity<Map<String, Object>> getOrderByNo(@PathVariable String orderNo) {
        Optional<Order> orderOpt = orderService.getOrderByNo(orderNo);

        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", orderOpt.get()
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "订单不存在"
            ));
        }
    }

    /**
     * 查询用户订单列表
     * GET /api/orders/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("userId", userId);
        result.put("total", orders.size());
        result.put("data", orders);

        // 统计消费总额
        BigDecimal totalAmount = orderService.getUserTotalAmount(userId);
        result.put("totalAmount", totalAmount);

        return ResponseEntity.ok(result);
    }

    /**
     * 分页查询用户订单
     * GET /api/orders/user/{userId}/page?page=0&size=10
     */
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Map<String, Object>> getUserOrdersPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Order> orderPage = orderService.getUserOrdersPage(userId, page, size);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("userId", userId);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", orderPage.getTotalPages());
        result.put("totalElements", orderPage.getTotalElements());
        result.put("data", orderPage.getContent());

        return ResponseEntity.ok(result);
    }

    /**
     * 支付订单
     * PUT /api/orders/{orderId}/pay
     */
    @PutMapping("/{orderId}/pay")
    public ResponseEntity<Map<String, Object>> payOrder(@PathVariable Long orderId) {
        boolean success = orderService.payOrder(orderId);

        if (success) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "订单支付成功"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "订单支付失败（订单不存在或状态异常）"
            ));
        }
    }

    /**
     * 完成订单
     * PUT /api/orders/{orderId}/complete
     */
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<Map<String, Object>> completeOrder(@PathVariable Long orderId) {
        boolean success = orderService.completeOrder(orderId);

        if (success) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "订单完成成功"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "订单完成失败（订单不存在或状态异常）"
            ));
        }
    }

    /**
     * 取消订单
     * PUT /api/orders/{orderId}/cancel
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long orderId) {
        boolean success = orderService.cancelOrder(orderId);

        if (success) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "订单取消成功"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "订单取消失败（订单不存在或状态异常）"
            ));
        }
    }

    /**
     * 统计用户消费总额
     * GET /api/orders/user/{userId}/total-amount
     */
    @GetMapping("/user/{userId}/total-amount")
    public ResponseEntity<Map<String, Object>> getUserTotalAmount(@PathVariable Long userId) {
        BigDecimal totalAmount = orderService.getUserTotalAmount(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", userId,
                "totalAmount", totalAmount,
                "message", String.format("用户 %d 消费总额: %.2f 元", userId, totalAmount)
        ));
    }

    /**
     * 删除用户所有订单
     * DELETE /api/orders/user/{userId}
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUserOrders(@PathVariable Long userId) {
        long deletedCount = orderService.deleteUserOrders(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", userId,
                "deletedCount", deletedCount,
                "message", String.format("成功删除用户 %d 的 %d 条订单", userId, deletedCount)
        ));
    }

    /**
     * 获取系统统计
     * GET /api/orders/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        String stats = orderService.getSystemStats();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", stats,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * 健康检查
     * GET /api/orders/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "order-service",
                "timestamp", System.currentTimeMillis(),
                "sharding", "ShardingSphere-JDBC 5.2.1"
        ));
    }
}