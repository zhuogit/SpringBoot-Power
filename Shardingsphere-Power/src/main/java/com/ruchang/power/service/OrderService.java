package com.ruchang.power.service;

import com.ruchang.power.entity.Order;
import com.ruchang.power.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 创建订单
     */
    @Transactional
    public Order createOrder(Long userId, String productName, BigDecimal amount) {
        // 生成订单号
        String orderNo = generateOrderNo();

        // 构建订单对象
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .productName(productName)
                .amount(amount)
                .status(1) // 待支付
                .build();

        // 保存订单
        Order savedOrder = orderRepository.save(order);
//        // 等待一下确保ID已生成
//        orderRepository.flush();
//
//        // 重新获取订单以获取生成的ID
//        Order orderWithId = orderRepository.findByOrderNo(orderNo)
//                .orElseThrow(() -> new RuntimeException("订单保存失败"));

        // 打印分片路由信息
        logShardingInfo(savedOrder);

        return savedOrder;
    }

    /**
     * 批量创建测试订单
     */
    @Transactional
    public void batchCreateTestOrders() {
        String[] products = {"iPhone 15", "MacBook Pro", "iPad Air", "AirPods Pro", "Apple Watch"};
        int size = 2;
        log.info("开始批量创建测试订单...");
        for (int i = 1; i <= size; i++) {
            Long userId = (long) i;
            String product = products[i % products.length];
            BigDecimal amount = new BigDecimal("5999.00").add(new BigDecimal(i * 100));

            Order order = createOrder(userId, product, amount);
            log.info("创建订单成功: 订单ID={}, 用户ID={}, 订单号={}",
                    order.getOrderId(), userId, order.getOrderNo());

            // 模拟部分订单支付
            if (i % 3 == 0) {
                payOrder(order.getOrderId());
            }
        }
        log.info("批量创建测试订单完成，共{}条", size);
    }

    /**
     * 根据ID查询订单
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * 根据订单号查询
     */
    public Optional<Order> getOrderByNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            return Optional.empty();
        }
        return orderRepository.findByOrderNo(orderNo);
    }

    /**
     * 查询用户订单
     */
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * 分页查询用户订单
     */
    public Page<Order> getUserOrdersPage(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.min(size, 100),
                Sort.by(Sort.Direction.DESC, "createTime")
        );
        return orderRepository.findByUserId(userId, pageRequest);
    }

    /**
     * 支付订单
     */
    @Transactional
    public boolean payOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    if (order.pay()) {
                        orderRepository.save(order);
                        log.info("订单支付成功: orderId={}", orderId);
                        return true;
                    }
                    log.warn("订单支付失败，状态异常: orderId={}, status={}", orderId, order.getStatus());
                    return false;
                })
                .orElse(false);
    }

    /**
     * 完成订单
     */
    @Transactional
    public boolean completeOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    if (order.complete()) {
                        orderRepository.save(order);
                        log.info("订单完成成功: orderId={}", orderId);
                        return true;
                    }
                    log.warn("订单完成失败，状态异常: orderId={}, status={}", orderId, order.getStatus());
                    return false;
                })
                .orElse(false);
    }

    /**
     * 取消订单
     */
    @Transactional
    public boolean cancelOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    if (order.cancel()) {
                        orderRepository.save(order);
                        log.info("订单取消成功: orderId={}", orderId);
                        return true;
                    }
                    log.warn("订单取消失败，状态异常: orderId={}, status={}", orderId, order.getStatus());
                    return false;
                })
                .orElse(false);
    }

    /**
     * 统计用户消费总额
     */
    public BigDecimal getUserTotalAmount(Long userId) {
        BigDecimal total = orderRepository.sumAmountByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * 删除用户订单
     */
    @Transactional
    public long deleteUserOrders(Long userId) {
        long count = orderRepository.deleteByUserId(userId);
        log.info("删除用户订单: userId={}, count={}", userId, count);
        return count;
    }

    /**
     * 查询时间范围内的订单
     */
    public List<Order> getOrdersByTimeRange(Date start, Date end) {
        return orderRepository.findByCreateTimeBetween(start, end);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD" + timeStr + randomStr;
    }

    /**
     * 打印分片路由信息
     */
    private void logShardingInfo(Order order) {
        long dbIndex = order.getUserId() % 2;
        long tableIndex = order.getOrderId() % 2;
        log.info("订单路由信息: orderId={}, userId={}, 路由到: 库ds{}, 表t_order_{}",
                order.getOrderId(), order.getUserId(), dbIndex, tableIndex);
    }

    /**
     * 获取系统统计信息
     */
    public String getSystemStats() {
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(1);
        long paidOrders = orderRepository.countByStatus(2);
        long completedOrders = orderRepository.countByStatus(3);

        return String.format("系统统计: 总订单=%d, 待支付=%d, 已支付=%d, 已完成=%d",
                totalOrders, pendingOrders, paidOrders, completedOrders);
    }
}