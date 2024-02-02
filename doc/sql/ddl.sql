CREATE TABLE IF NOT EXISTS `pay_app`
(
    `app_id`              bigint       NOT NULL COMMENT '支付应用ID',
    `name`                varchar(64)  NOT NULL COMMENT '支付应用名',
    `status`              tinyint      NOT NULL DEFAULT '1' COMMENT '支付状态',
    `remark`              varchar(64)           DEFAULT NULL COMMENT '备注',
    `order_notify_url`    varchar(256) NOT NULL COMMENT '支付结果的回调地址',
    `refund_notify_url`   varchar(256) NOT NULL COMMENT '退款结果的回调地址',
    `transfer_notify_url` varchar(45)  NOT NULL COMMENT '转账结果的回调地址',
    `deleted`             tinyint      NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`         datetime     NOT NULL COMMENT '创建时间',
    `update_time`         datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`app_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='支付应用表';

CREATE TABLE IF NOT EXISTS `pay_channel`
(
    `channel_id`  bigint         NOT NULL COMMENT '支付渠道ID',
    `app_id`      bigint         NOT NULL COMMENT '支付应用编号',
    `code`        varchar(64)    NOT NULL COMMENT '支付渠道编码',
    `status`      tinyint        NOT NULL DEFAULT '1' COMMENT '支付状态',
    `fee_rate`    decimal(10, 2) NOT NULL COMMENT '支付渠道费率',
    `remark`      varchar(64)             DEFAULT NULL COMMENT '备注',
    `config`      json           NOT NULL COMMENT '支付渠道配置',
    `deleted`     tinyint        NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time` datetime       NOT NULL COMMENT '创建时间',
    `update_time` datetime       NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`channel_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='支付渠道表';

CREATE TABLE IF NOT EXISTS `pay_notify_log`
(
    `log_id`       bigint   NOT NULL COMMENT '日志编号',
    `task_id`      bigint   NOT NULL COMMENT '通知任务编号',
    `notify_times` int      NOT NULL COMMENT '第几次被通知',
    `response`     json     NOT NULL COMMENT 'HTTP 响应结果',
    `status`       tinyint  NOT NULL COMMENT '支付通知状态',
    `deleted`      tinyint  NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`  datetime NOT NULL COMMENT '创建时间',
    `update_time`  datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`log_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='通知日志表';

CREATE TABLE IF NOT EXISTS `pay_notify_task`
(
    `task_id`              bigint       NOT NULL COMMENT '通知任务编号',
    `app_id`               bigint       NOT NULL COMMENT '应用编号',
    `type`                 tinyint      NOT NULL COMMENT '通知类型',
    `data_id`              bigint       NOT NULL COMMENT '数据编号，根据不同 type 进行关联',
    `merchant_order_id`    varchar(128) COMMENT '商户订单编号',
    `merchant_transfer_id` varchar(128) COMMENT '商户转账单编号',
    `status`               tinyint      NOT NULL COMMENT '通知状态',
    `next_notify_time`     datetime COMMENT '下一次通知时间',
    `last_execute_time`    datetime COMMENT '最后一次执行时间',
    `notify_times`         int          NOT NULL COMMENT '当前通知次数',
    `max_notify_times`     int          NOT NULL COMMENT '最大可通知次数',
    `notify_url`           varchar(256) NOT NULL COMMENT '通知地址',
    `deleted`              tinyint      NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`          datetime     NOT NULL COMMENT '创建时间',
    `update_time`          datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`task_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='通知任务表';

CREATE TABLE IF NOT EXISTS `pay_order`
(
    `pay_order_id`       bigint         NOT NULL COMMENT '支付订单编号',
    `app_id`             bigint         NOT NULL COMMENT '应用编号',
    `channel_id`         bigint         NOT NULL COMMENT '渠道编号',
    `channel_code`       varchar(32)    NOT NULL COMMENT '渠道编码',
    `merchant_order_id`  varchar(128) COMMENT '商户订单编号',
    `subject`            varchar(128) COMMENT '商品标题',
    `body`               varchar(256) COMMENT '商品描述信息',
    `notify_url`         varchar(1024)  NOT NULL COMMENT '异步通知地址',
    `price`              int            NOT NULL COMMENT '支付金额，单位：分',
    `channel_fee_rate`   decimal(10, 2) NOT NULL COMMENT '支付渠道费率',
    `channel_fee_price`  int            NOT NULL COMMENT '渠道手续金额，单位：分',
    `status`             tinyint        NOT NULL COMMENT '支付状态',
    `user_ip`            varchar(32)    NOT NULL COMMENT '用户IP',
    `expire_time`        datetime       NOT NULL COMMENT '订单失效时间',
    `success_time`       datetime       NOT NULL COMMENT '订单支付成功时间',
    `order_extension_id` bigint COMMENT '支付成功的订单拓展单编号',
    `out_trade_no`       varchar(128) COMMENT '支付成功的外部订单号',
    `refund_price`       int            NOT NULL COMMENT '退款总金额，单位：分',
    `channel_user_id`    varchar(128)   NOT NULL COMMENT '渠道用户编号, 例如微信openid、支付宝账号',
    `channel_order_no`   varchar(128)   NOT NULL COMMENT '渠道订单号',
    `deleted`            tinyint        NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`        datetime       NOT NULL COMMENT '创建时间',
    `update_time`        datetime       NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`pay_order_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='支付订单表';

CREATE TABLE IF NOT EXISTS `pay_order_extension`
(
    `order_extension_id`  bigint       NOT NULL COMMENT '支付订单编号',
    `out_trade_no`        varchar(128) NOT NULL COMMENT '支付成功的外部订单号',
    `pay_order_id`        bigint       NOT NULL COMMENT '支付订单编号',
    `channel_id`          bigint       NOT NULL COMMENT '渠道编号',
    `channel_code`        varchar(32)  NOT NULL COMMENT '渠道编码',
    `user_ip`             varchar(32)  NOT NULL COMMENT '用户IP',
    `status`              tinyint      NOT NULL COMMENT '支付状态',
    `channel_extras`      varchar(256) NOT NULL COMMENT '支付渠道的额外参数',
    `channel_error_code`  varchar(128) COMMENT '调用渠道的错误码',
    `channel_error_msg`   varchar(256) COMMENT '调用渠道报错时，错误信息',
    `channel_notify_data` varchar(4096) COMMENT '支付渠道的同步/异步通知的内容',
    `deleted`             tinyint      NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`         datetime     NOT NULL COMMENT '创建时间',
    `update_time`         datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`order_extension_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='支付订单拓展';

CREATE TABLE IF NOT EXISTS `pay_refund`
(
    `refund_id`           bigint        NOT NULL COMMENT '支付退款编号',
    `out_trade_no`        varchar(128)  NOT NULL COMMENT '退款单号',
    `app_id`              bigint        NOT NULL COMMENT '应用编号',
    `channel_id`          bigint        NOT NULL COMMENT '渠道编号',
    `channel_code`        varchar(32)   NOT NULL COMMENT '渠道编码',
    `pay_order_id`        bigint        NOT NULL COMMENT '支付订单编号 pay_order 表id',
    `order_no`            varchar(128)  NOT NULL COMMENT '支付订单 no',
    `merchant_order_id`   varchar(128)  NOT NULL COMMENT '商户订单编号（商户系统生成）',
    `merchant_refund_id`  varchar(128)  NOT NULL COMMENT '商户退款订单号（商户系统生成）',
    `notify_url`          varchar(1024) NOT NULL COMMENT '异步通知商户地址',
    `status`              tinyint       NOT NULL COMMENT '退款状态',
    `pay_price`           bigint        NOT NULL COMMENT '支付金额,单位分',
    `refund_price`        bigint        NOT NULL COMMENT '退款金额,单位分',
    `reason`              varchar(256)  NOT NULL COMMENT '退款原因',
    `user_ip`             varchar(32)            DEFAULT NULL COMMENT '用户 IP',
    `channel_order_no`    varchar(128)  NOT NULL COMMENT '渠道订单号，pay_order 中的 channel_order_no 对应',
    `channel_refund_no`   varchar(128)           DEFAULT NULL COMMENT '渠道退款单号，渠道返回',
    `success_time`        datetime               DEFAULT NULL COMMENT '退款成功时间',
    `channel_error_code`  varchar(128)           DEFAULT NULL COMMENT '渠道调用报错时，错误码',
    `channel_error_msg`   varchar(256)           DEFAULT NULL COMMENT '渠道调用报错时，错误信息',
    `channel_notify_data` varchar(4096)          DEFAULT NULL COMMENT '支付渠道异步通知的内容',
    `deleted`             tinyint       NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`         datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`refund_id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '退款订单';

CREATE TABLE IF NOT EXISTS `pay_transfer`
(
    `transfer_id`          bigint        NOT NULL COMMENT '转账编号',
    `out_trade_no`         varchar(128)  NOT NULL COMMENT '退款单号',
    `app_id`               bigint        NOT NULL COMMENT '应用编号',
    `channel_id`           bigint        NOT NULL COMMENT '转账渠道编号',
    `channel_code`         varchar(32)   NOT NULL COMMENT '转账渠道编码',
    `merchant_transfer_id` varchar(64)   NOT NULL COMMENT '商户转账单编号',
    `type`                 int           NOT NULL COMMENT '类型',
    `status`               tinyint       NOT NULL COMMENT '转账状态',
    `success_time`         datetime      NULL COMMENT '转账成功时间',
    `price`                int           NOT NULL COMMENT '转账金额，单位：分',
    `subject`              varchar(512)  NOT NULL COMMENT '转账标题',
    `user_name`            varchar(64)   NULL COMMENT '收款人姓名',
    `alipay_logon_id`      varchar(64)   NULL COMMENT '支付宝登录号',
    `openid`               varchar(64)   NULL COMMENT '微信 openId',
    `notify_url`           varchar(1024) NOT NULL COMMENT '异步通知商户地址',
    `user_ip`              varchar(50)   NOT NULL COMMENT '用户 IP',
    `channel_extras`       varchar(512)  NULL     DEFAULT NULL COMMENT '渠道的额外参数',
    `channel_transfer_no`  varchar(64)   NULL     DEFAULT NULL COMMENT '渠道转账单号',
    `channel_error_code`   varchar(128)  NULL     DEFAULT NULL COMMENT '调用渠道的错误码',
    `channel_error_msg`    varchar(256)  NULL     DEFAULT NULL COMMENT '调用渠道的错误提示',
    `channel_notify_data`  varchar(4096) NULL     DEFAULT NULL COMMENT '渠道的同步/异步通知的内容',
    `deleted`              tinyint       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `create_time`          datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`transfer_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='转账单表';

