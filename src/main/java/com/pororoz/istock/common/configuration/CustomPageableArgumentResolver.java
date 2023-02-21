package com.pororoz.istock.common.configuration;

import com.pororoz.istock.common.exception.CustomException;
import com.pororoz.istock.common.exception.ErrorCode;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CustomPageableArgumentResolver implements HandlerMethodArgumentResolver {

  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 20;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return Pageable.class.equals(parameter.getParameterType());
  }

  @Override
  public Pageable resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    Integer page = getIntegerOrElseThrow(webRequest.getParameter("page"));
    Integer size = getIntegerOrElseThrow(webRequest.getParameter("size"));

    return getPageable(page, size);
  }

  private Integer getIntegerOrElseThrow(String param) {
    if (Objects.isNull(param) || param.isEmpty()) {
      return null;
    }
    //정수 판별
    if (param.matches("-?\\d+")) {
      return Integer.parseInt(param);
    }
    throw new CustomException(ErrorCode.INVALID_PAGE_REQUEST);
  }

  private Pageable getPageable(Integer page, Integer size) {
    if (page == null && size == null) {
      return Pageable.unpaged();
    }
    if ((Objects.nonNull(page) && page < 0) || (Objects.nonNull(size) && size < 1)) {
      throw new CustomException(ErrorCode.INVALID_PAGE_REQUEST);
    }
    return PageRequest.of(
        page == null ? DEFAULT_PAGE : page,
        size == null ? DEFAULT_SIZE : size);
  }


}
