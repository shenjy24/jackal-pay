package com.jonas.pay.config.response;

/**
 * 业务错误码
 *
 * @author shenjy
 * @time 2020/8/13
 */
public enum ErrorCode {
    /**
     * 通用异常
     */
    PARAM_ERROR(100001, "参数异常"),
    UPDATE_ERROR(100002,"更新异常"),

    /**
     * 用户模块
     */
    USER_ERROR1(200001, "用户名重复"),
    USER_ERROR2(200002, "手机号重复"),
    USER_ERROR3(200003, "用户不存在"),
    /**
     * 店铺模块
     */
    SHOP_ERROR1(300001, "店铺名重复"),
    SHOP_ERROR2(300002, "店铺申请不存在"),
    SHOP_ERROR3(300003, "申请状态异常"),
    SHOP_ERROR4(300004, "当前非审核中状态"),
    SHOP_ERROR5(300005, "店铺不存在"),

    /**
     * 商品模块
     */
    PRODUCT_ERROR1(400001, "商品分组名不能为空"),
    PRODUCT_ERROR2(400002, "商品分组不存在"),
    PRODUCT_ERROR3(400003, "商品不存在"),
    PRODUCT_ERROR4(400004, "商品状态异常"),

    /**
     * 订单模块
     */
    ORDER_ERROR1(500001, "订单状态异常");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
