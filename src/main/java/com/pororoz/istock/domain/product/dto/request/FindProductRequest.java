package com.pororoz.istock.domain.product.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.product.dto.service.FindProductServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductRequest {

  @Schema(description = "페이지 요청", example = "0")
  @PositiveOrZero(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private Integer page;

  @Schema(description = "사이즈 요청", example = "20")
  @Positive(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private Integer size;

  @Schema(description = "카테고리 아이디", example = "1")
  @NotNull
  @PositiveOrZero
  private Long categoryId;

  public FindProductServiceRequest toService() {
    return FindProductServiceRequest.builder()
        .page(page).size(size)
        .categoryId(categoryId)
        .build();
  }
}
