package com.pororoz.istock.domain.category.dto.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

@Getter
@Builder
public class GetCategoryServiceRequest {

  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 20;

  private String name;

  private Integer page;

  private Integer size;

  public PageRequest toPageRequest() {
    return PageRequest.of(
        page == null ? DEFAULT_PAGE : page,
        size == null ? DEFAULT_SIZE : size);
  }
}
