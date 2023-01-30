package com.pororoz.istock.domain.user.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.user.dto.service.FindUserServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class FindUserRequest {

  @Schema(description = "사용자 이름", example = "0")
  @Nullable
  @PositiveOrZero(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private Integer page;
  @Schema(description = "사용자 이름", example = "20")
  @Nullable
  @Positive(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private Integer size;

  public FindUserServiceRequest toService() {
    return FindUserServiceRequest.builder().page(page).size(size).build();
  }
}
