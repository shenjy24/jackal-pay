CREATE TABLE `pay_app`
(
    `id`                  bigint       NOT NULL COMMENT '支付应用ID',
    `name`                varchar(64)  NOT NULL COMMENT '支付应用名',
    `status`              tinyint      NOT NULL DEFAULT '1' COMMENT '支付状态',
    `remark`              varchar(64)           DEFAULT NULL COMMENT '备注',
    `order_notify_url`    varchar(256) NOT NULL COMMENT '支付结果的回调地址',
    `refund_notify_url`   varchar(256) NOT NULL COMMENT '退款结果的回调地址',
    `transfer_notify_url` varchar(45)  NOT NULL COMMENT '转账结果的回调地址',
    `deleted`             tinyint      NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time`         datetime     NOT NULL COMMENT '创建时间',
    `update_time`         datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `pay_channel`
(
    `id`          bigint         NOT NULL COMMENT '支付渠道ID',
    `app_id`      bigint         NOT NULL COMMENT '支付应用编号',
    `code`        varchar(64)    NOT NULL COMMENT '支付渠道编码',
    `status`      tinyint        NOT NULL DEFAULT '1' COMMENT '支付状态',
    `fee_rate`    decimal(10, 2) NOT NULL COMMENT '支付渠道费率',
    `remark`      varchar(64)             DEFAULT NULL COMMENT '备注',
    `config`      json           NOT NULL COMMENT '支付渠道配置',
    `deleted`     tinyint        NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_time` datetime       NOT NULL COMMENT '创建时间',
    `update_time` datetime       NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

