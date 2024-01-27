package com.jonas.pay.config;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jonas.pay.config.response.BizException;
import com.jonas.pay.config.response.JsonResult;
import com.jonas.pay.config.response.SystemCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 全局异常处理器
 *
 * @author shenjy
 * @time 2020/8/13
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public JsonResult handle(Exception ex) {
        log.error("handle exception", ex);
        if (ex instanceof BizException) {
            Iterable iterable = Splitter.on(":").trimResults().omitEmptyStrings().split(ex.getMessage());
            List<String> items = Lists.newArrayList(iterable);
            return new JsonResult(items.get(1), items.get(2), null);
        }
        if (ex instanceof ConstraintViolationException e) {
            String message = e.getMessage().split(",")[0];
            message = message.split(":")[1].trim();
            return new JsonResult(SystemCode.SERVER_ERROR.getCode(), message);
        }
        if (ex instanceof MethodArgumentNotValidException e) {
            BindingResult result = e.getBindingResult();
            if (result.getFieldError() != null) {
                String msg = result.getFieldError().getDefaultMessage();
                return new JsonResult(SystemCode.SERVER_ERROR.getCode(), msg);
            }
        }
        return new JsonResult(SystemCode.SERVER_ERROR);
    }
}
