package com.jonas.pay.config.response;

import lombok.Getter;

/**
 * 业务错误码
 *
 * @author shenjy
 * @time 2020/8/13
 */
@Getter
public enum ErrorCode {
    /**
     * 通用异常
     */
    NOT_IMPLEMENTED(100001, "功能未实现/未开启"),

    // ========== 支付宝错误段 ==========
    ALIPAY_ERROR1(200001, "支付宝单笔转账必须使用公钥证书模式"),
    ALIPAY_ERROR2(200002, "不正确的转账类型"),
    ALIPAY_ERROR3(200003, "条形码不能为空"),

    // ========== 微信支付错误段 ==========
    WECHAT_PAY_ERROR1(300001, "支付请求的 authCode 不能为空"),
    WECHAT_PAY_ERROR2(300002, "支付请求的 openid 不能为空"),

    // ========== 支付渠道错误段 ==========
    CHANNEL_ERROR1(400001, "已存在相同的渠道"),
    CHANNEL_ERROR2(400002, "支付渠道的配置不存在"),

    // ========== 客户端错误段 ==========

    BAD_REQUEST(400, "请求参数不正确"),
    UNAUTHORIZED(401, "账号未登录"),
    FORBIDDEN(403, "没有该操作权限"),
    NOT_FOUND(404, "请求未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不正确"),
    LOCKED(423, "请求失败，请稍后重试"), // 并发请求，不允许
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),

    // ========== 服务端错误段 ==========

    INTERNAL_SERVER_ERROR(500, "系统异常"),
    ERROR_CONFIGURATION(502, "错误的配置项"),

    // ========== 自定义错误段 ==========
    REPEATED_REQUESTS(900, "重复请求，请稍后重试"), // 重复请求
    DEMO_DENY(901, "演示模式，禁止写操作"),
    UNKNOWN(999, "未知错误");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
