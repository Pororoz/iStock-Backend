package com.pororoz.istock.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResponse<T> {

  @Schema(description = "첫 번째 페이지인가?", example = "true")
  boolean first;
  @Schema(description = "마지막 페이지인가?", example = "false")
  boolean last;
  List<T> contents;
  @Schema(description = "content에 담긴 내용의 개수", example = "1")
  int currentSize;
  @Schema(description = "전체 페이지", example = "10")
  int totalPages;
  @Schema(description = "전체 요소 개수", example = "100")
  long totalElements; // content 개수

  public PageResponse(Page<T> page) {
    first = page.isFirst();
    last = page.isLast();
    contents = page.getContent();
    currentSize = page.getNumberOfElements();
    totalPages = page.getTotalPages();
    totalElements = page.getTotalElements();
  }
}
