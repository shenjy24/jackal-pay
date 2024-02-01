package com.jonas.pay.config.response;

import com.jonas.pay.util.GsonUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 通用返回结构
 *
 * @author shenjy
 * @time 2020/8/13
 */
@Getter
@Setter
@NoArgsConstructor
public class JsonResult<T> implements Serializable {
    private String code;
    private String message;
    private T data;

    public JsonResult(CodeStatus codeStatus) {
        this.code = codeStatus.getCode();
        this.message = codeStatus.getMessage();
    }

    public JsonResult(CodeStatus codeStatus, T data) {
        this.code = codeStatus.getCode();
        this.message = codeStatus.getMessage();
        this.data = data;
    }

    public JsonResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> JsonResult<T> success(T data) {
        return restResult(data, SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMessage());
    }

    private static <T> JsonResult<T> restResult(T data, String code, String msg) {
        JsonResult<T> result = new JsonResult();
        result.setCode(code);
        result.setData(data);
        result.setMessage(msg);
        return result;
    }

    public static boolean isSuccess(String code) {
        return SystemCode.SUCCESS.getCode().equals(code);
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this);
    }
}
