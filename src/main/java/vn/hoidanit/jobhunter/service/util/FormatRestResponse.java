package vn.hoidanit.jobhunter.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import vn.hoidanit.jobhunter.domain.RestResponse;
import vn.hoidanit.jobhunter.service.util.annotation.ApiMessage;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Bỏ qua nếu endpoint trả String -> tránh xung đột với StringHttpMessageConverter
        return !StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        int statusCode = 200;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            statusCode = servletResponse.getServletResponse().getStatus();
        }

        // Không bọc nếu đã là RestResponse hoặc là lỗi (>=400)
        if (body instanceof RestResponse<?> || statusCode >= 400) {
            return body;
        }

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(statusCode);
        res.setData(body);

        ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
        res.setMessage(message != null ? message.value() : "CALL API SUCCESS");

        return res;
    }
}
