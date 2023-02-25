package com.pororoz.istock.domain.bom.dto.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

@Getter
@Builder
public class FindBomServiceRequest {

  public static final int DEFAULT_PAGE = 0;
  public static final int DEFAULT_SIZE = 20;

  private Integer page;
  private Integer size;
  private Long productId;

  public PageRequest toPageRequest() {
    return PageRequest.of(
        page == null ? DEFAULT_PAGE : page,
        size == null ? DEFAULT_SIZE : size);
  }

  public static FindBomServiceRequest of(Integer page, Integer size, Long productId) {
    return FindBomServiceRequest.builder()
        .page(page)
        .size(size)
        .productId(productId)
        .build();
  }
}
