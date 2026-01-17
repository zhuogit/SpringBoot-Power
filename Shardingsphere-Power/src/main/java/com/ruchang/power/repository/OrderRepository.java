package com.ruchang.power.repository;

import com.ruchang.power.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 * 继承JpaRepository获得基础的CRUD能力
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据订单号查询（会广播查询所有分片）
     *
     * @param orderNo 订单号
     * @return 订单
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询（带分片键 - 高效查询）
     *
     * @param userId 用户ID
     * @return 用户订单列表
     */
    List<Order> findByUserId(Long userId);

    /**
     * 分页查询用户订单
     *
     * @param userId   用户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和状态查询
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByUserIdAndStatus(Long userId, Integer status);

    /**
     * 根据创建时间范围查询（广播查询，谨慎使用）
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 订单列表
     */
    List<Order> findByCreateTimeBetween(Date start, Date end);

    /**
     * 统计用户订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    long countByUserId(Long userId);

    /**
     * 统计用户消费总额
     * 注意：这是跨分片的聚合查询
     *
     * @param userId 用户ID
     * @return 消费总额
     */
    @Query("SELECT SUM(o.amount) FROM Order o WHERE o.userId = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") Long userId);

    /**
     * 根据状态查询订单数量
     *
     * @param status 订单状态
     * @return 订单数量
     */
    long countByStatus(Integer status);

    /**
     * 删除用户的订单
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    long deleteByUserId(Long userId);
}