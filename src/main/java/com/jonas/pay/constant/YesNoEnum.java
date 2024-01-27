package com.jonas.pay.constant;

import lombok.Getter;

/**
 * YesNoEnum
 *
 * @author shenjy
 * @time 2024/1/27 15:17
 */
@Getter
public enum YesNoEnum {
    NO(0, "否"),
    YES(1, "是");

    private final int code;
    private final String message;

    YesNoEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
