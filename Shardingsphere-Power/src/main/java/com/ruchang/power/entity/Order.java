package com.ruchang.power.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类
 * 对应逻辑表 t_order，实际物理表为 t_order_0, t_order_1
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_order")  // 逻辑表名
public class Order {

    /**
     * 订单ID - 使用Snowflake算法生成分布式ID
     * 作为分表键，决定数据落在哪个物理表
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * 订单编号 - 业务唯一标识
     */
    @Column(name = "order_no", nullable = false, length = 64, unique = true)
    private String orderNo;

    /**
     * 用户ID - 作为分库键，决定数据落在哪个物理库
     * 分库规则：user_id % 2
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 商品名称
     */
    @Column(name = "product_name", nullable = false)
    private String productName;

    /**
     * 订单金额
     */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /**
     * 订单状态：
     * 1 - 待支付
     * 2 - 已支付
     * 3 - 已完成
     * 4 - 已取消
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 设置订单状态为待支付
     */
    public void initStatus() {
        if (this.status == null) {
            this.status = 1; // 待支付
        }
    }

    /**
     * 支付订单
     */
    public boolean pay() {
        if (this.status == 1) { // 只有待支付状态才能支付
            this.status = 2;
            return true;
        }
        return false;
    }

    /**
     * 完成订单
     */
    public boolean complete() {
        if (this.status == 2) { // 只有已支付状态才能完成
            this.status = 3;
            return true;
        }
        return false;
    }

    /**
     * 取消订单
     */
    public boolean cancel() {
        if (this.status == 1) { // 只有待支付状态才能取消
            this.status = 4;
            return true;
        }
        return false;
    }
}