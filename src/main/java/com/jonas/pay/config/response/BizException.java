package com.jonas.pay.config.response;


/**
 * 自定义异常类
 *
 * @author shenjy
 * @time 2020/8/13
 */
public class BizException extends RuntimeException {
    private CodeStatus codeStatus;
    private String message;

    public BizException(CodeStatus codeStatus, Throwable cause) {
        super(codeStatus.getMessage());
        this.codeStatus = codeStatus;
        this.message = "BizException:" + codeStatus.getCode() + ":" + codeStatus.getMessage();
    }

    public BizException(CodeStatus codeStatus) {
        super("BizException:" + codeStatus.getCode() + ":" + codeStatus.getMessage());
        this.codeStatus = codeStatus;
        this.message = "BizException:" + codeStatus.getCode() + ":" + codeStatus.getMessage();
    }

    public BizException(String message) {
        super("BizException:" + SystemCode.SERVER_ERROR.getCode() + ":" + message);
        this.codeStatus = new CodeStatus() {
            @Override
            public String getCode() {
                return SystemCode.SERVER_ERROR.getCode();
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
        this.message = "BizException:" + SystemCode.SERVER_ERROR.getCode() + ":" + message;
    }

    public BizException(String code, String message) {
        super("BizException:" + code + ":" + message);
        this.codeStatus = new CodeStatus() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
        this.message = "BizException:" + code + ":" + message;
    }

    public BizException(ErrorCode errorCode) {
        super("BizException:" + SystemCode.SERVER_ERROR.getCode() + ":" + errorCode.getMessage());
        this.codeStatus = new CodeStatus() {
            @Override
            public String getCode() {
                return SystemCode.SERVER_ERROR.getCode();
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
        this.message = "BizException:" + SystemCode.SERVER_ERROR.getCode() + ":" + errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public CodeStatus getCodeStatus() {
        return codeStatus;
    }
}
