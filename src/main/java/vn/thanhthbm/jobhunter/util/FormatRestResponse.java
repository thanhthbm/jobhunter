package vn.thanhthbm.jobhunter.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import vn.thanhthbm.jobhunter.domain.response.RestResponse;
import vn.thanhthbm.jobhunter.util.annotation.ApiMessage;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType, Class converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
    int status = servletResponse.getStatus();

    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(status);

    if (body instanceof String || body instanceof Resource) {
      return body;
    }

    if (status >= 400) {
      return body;
    } else {
      res.setData(body);
      ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
      res.setMessage(message != null ? message.value() : "CALL API SUCCESS");
    }

    return res;
  }

}
