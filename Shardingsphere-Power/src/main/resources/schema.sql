-- 创建数据库（手动执行或在应用启动前创建）
CREATE
DATABASE IF NOT EXISTS db_order_0 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE
DATABASE IF NOT EXISTS db_order_1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用 db_order_0
USE
db_order_0;

-- 创建订单表0
CREATE TABLE IF NOT EXISTS t_order_0
(
    order_id
    BIGINT
    NOT
    NULL
    COMMENT
    '订单ID（雪花算法）',
    order_no
    VARCHAR
(
    64
) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_name VARCHAR
(
    255
) NOT NULL COMMENT '商品名称',
    amount DECIMAL
(
    15,
    2
) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待支付，2-已支付，3-已完成，4-已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
(
    order_id
),
    UNIQUE KEY uk_order_no
(
    order_no
),
    KEY idx_user_id
(
    user_id
),
    KEY idx_create_time
(
    create_time
),
    KEY idx_status
(
    status
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_unicode_ci COMMENT='订单表0';

-- 创建订单表1
CREATE TABLE IF NOT EXISTS t_order_1
(
    order_id
    BIGINT
    NOT
    NULL
    COMMENT
    '订单ID（雪花算法）',
    order_no
    VARCHAR
(
    64
) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_name VARCHAR
(
    255
) NOT NULL COMMENT '商品名称',
    amount DECIMAL
(
    15,
    2
) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待支付，2-已支付，3-已完成，4-已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
(
    order_id
),
    UNIQUE KEY uk_order_no
(
    order_no
),
    KEY idx_user_id
(
    user_id
),
    KEY idx_create_time
(
    create_time
),
    KEY idx_status
(
    status
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_unicode_ci COMMENT='订单表1';

-- 创建配置表（广播表，两个库都有相同数据）
CREATE TABLE IF NOT EXISTS t_config
(
    config_id
    INT
    NOT
    NULL
    AUTO_INCREMENT
    COMMENT
    '配置ID',
    config_key
    VARCHAR
(
    100
) NOT NULL COMMENT '配置键',
    config_value VARCHAR
(
    500
) NOT NULL COMMENT '配置值',
    config_desc VARCHAR
(
    255
) COMMENT '配置描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
(
    config_id
),
    UNIQUE KEY uk_config_key
(
    config_key
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_unicode_ci COMMENT='系统配置表';

-- 使用 db_order_1
USE
db_order_1;

-- 创建相同的表结构
CREATE TABLE IF NOT EXISTS t_order_0
(
    order_id
    BIGINT
    NOT
    NULL
    COMMENT
    '订单ID（雪花算法）',
    order_no
    VARCHAR
(
    64
) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_name VARCHAR
(
    255
) NOT NULL COMMENT '商品名称',
    amount DECIMAL
(
    15,
    2
) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待支付，2-已支付，3-已完成，4-已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
(
    order_id
),
    UNIQUE KEY uk_order_no
(
    order_no
),
    KEY idx_user_id
(
    user_id
),
    KEY idx_create_time
(
    create_time
),
    KEY idx_status
(
    status
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_unicode_ci COMMENT='订单表0';

CREATE TABLE IF NOT EXISTS t_order_1
(
    order_id
    BIGINT
    NOT
    NULL
    COMMENT
    '订单ID（雪花算法）',
    order_no
    VARCHAR
(
    64
) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_name VARCHAR
(
    255
) NOT NULL COMMENT '商品名称',
    amount DECIMAL
(
    15,
    2
) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待支付，2-已支付，3-已完成，4-已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
(
    order_id
),
    UNIQUE KEY uk_order_no
(
    order_no
),
    KEY idx_user_id
(
    user_id
),
    KEY idx_create_time
(
    create_time
),
    KEY idx_status
(
    status
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_unicode_ci COMMENT='订单表1';

CREATE TABLE IF NOT EXISTS t_config
(
    config_id
    INT
    NOT
    NULL
    AUTO_INCREMENT
    COMMENT
    '配置ID',
    config_key
    VARCHAR
(
    100
) NOT NULL COMMENT '配置键',
    config_value VARCHAR
(
    500
) NOT NULL COMMENT '配置值',
    config_desc VARCHAR
(
    255
) COMMENT '配置描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY
(
    config_id
),
    UNIQUE KEY uk_config_key
(
    config_key
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_unicode_ci COMMENT='系统配置表';