package com.jonas.pay.config;

import com.jonas.pay.config.response.JsonResult;
import com.jonas.pay.config.response.SystemCode;
import com.jonas.pay.util.GsonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 响应结果封装
 *
 * @author shenjy
 * @time 2020/8/13
 */
@ControllerAdvice
@ConditionalOnMissingClass
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = methodParameter.getMethod();
        //sse接口的不拦截
        if (Objects.equals(method.getName(), "sseServer") ||
                Objects.equals(method.getName(), "completions")) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        if (!(body instanceof JsonResult)) {
            if (!(body instanceof String)) {
                return new JsonResult(SystemCode.SUCCESS, body);
            } else {
                return GsonUtil.toJson(new JsonResult(SystemCode.SUCCESS, body));
            }
        }
        return body;
    }
}
