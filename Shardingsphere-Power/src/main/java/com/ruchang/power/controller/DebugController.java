package com.ruchang.power.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查看当前ShardingSphere配置
     */
    @GetMapping("/config")
    public Map<String, Object> showConfig() {
        Map<String, Object> config = new HashMap<>();

        // 1. 显示ShardingSphere相关配置
        String prefix = "spring.shardingsphere.";
        String[] configKeys = {
                "mode.type",
                "datasource.names",
                "sharding.tables.t_order.actual-data-nodes",
                "sharding.tables.t_order.table-strategy.inline.sharding-column",
                "sharding.tables.t_order.table-strategy.inline.algorithm-expression",
                "sharding.tables.t_order.database-strategy.inline.sharding-column",
                "sharding.tables.t_order.database-strategy.inline.algorithm-expression"
        };

        for (String key : configKeys) {
            String fullKey = prefix + key;
            String value = env.getProperty(fullKey);
            config.put(fullKey, value != null ? value : "未配置");
        }

        // 2. 显示数据源信息
        try {
            config.put("dataSourceClass", dataSource.getClass().getName());
        } catch (Exception e) {
            config.put("dataSourceError", e.getMessage());
        }

        // 3. 显示数据库中的实际表
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                    "SHOW TABLES LIKE 't_order%'"
            );
            config.put("existingTables", tables);
        } catch (Exception e) {
            config.put("tableQueryError", e.getMessage());
        }

        return config;
    }

    /**
     * 测试SQL路由
     */
    @GetMapping("/test-sql")
    public String testSqlRouting() {
        try {
            // 清除之前的测试数据
            jdbcTemplate.execute("DELETE FROM t_order WHERE order_no LIKE 'TEST%'");

            // 测试1：应该路由到 t_order_0
            jdbcTemplate.execute(
                    "INSERT INTO t_order(order_id, order_no, user_id, product_name, amount, status) " +
                            "VALUES (100, 'TEST_100', 100, 'Test Product', 100.00, 1)"
            );

            // 测试2：应该路由到 t_order_1
            jdbcTemplate.execute(
                    "INSERT INTO t_order(order_id, order_no, user_id, product_name, amount, status) " +
                            "VALUES (101, 'TEST_101', 101, 'Test Product', 101.00, 1)"
            );

            return "测试SQL已执行，请查看控制台日志中的'Actual SQL'部分";

        } catch (Exception e) {
            return "测试失败: " + e.getMessage();
        }
    }

    /**
     * 直接执行原始SQL测试
     */
    @GetMapping("/test-raw-sql")
    public String testRawSql() {
        StringBuilder result = new StringBuilder();

        // 直接向物理表插入数据，验证表是否存在
        String[] testSqls = {
                "INSERT INTO t_order_0(order_id, order_no, user_id) VALUES (1000, 'RAW_TEST_0', 1000)",
                "INSERT INTO t_order_1(order_id, order_no, user_id) VALUES (1001, 'RAW_TEST_1', 1001)",
                "SELECT COUNT(*) FROM t_order_0",
                "SELECT COUNT(*) FROM t_order_1"
        };

        for (String sql : testSqls) {
            try {
                if (sql.startsWith("INSERT")) {
                    int rows = jdbcTemplate.update(sql);
                    result.append("✅ ").append(sql).append(" → 影响行数: ").append(rows).append("\n");
                } else if (sql.startsWith("SELECT")) {
                    Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
                    result.append("✅ ").append(sql).append(" → 结果: ").append(count).append("\n");
                }
            } catch (Exception e) {
                result.append("❌ ").append(sql).append(" → 失败: ").append(e.getMessage()).append("\n");
            }
        }

        return result.toString();
    }
}