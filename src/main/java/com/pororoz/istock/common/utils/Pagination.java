package com.pororoz.istock.common.utils;

import org.springframework.data.domain.PageRequest;

public class Pagination {

  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 20;

  public static PageRequest toPageRequest(Integer page, Integer size) {
    return PageRequest.of(
        page == null ? DEFAULT_PAGE : page,
        size == null ? DEFAULT_SIZE : size);
  }
}
