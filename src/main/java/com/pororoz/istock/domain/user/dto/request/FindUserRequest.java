package com.pororoz.istock.domain.user.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;


@Getter
@Builder
public class FindUserRequest {

  @Schema(description = "사용자 이름", example = "0")
  @PositiveOrZero(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private int page;
  @Schema(description = "사용자 이름", example = "20")
  @Positive(message = ExceptionMessage.INVALID_PAGE_REQUEST)
  private int size;

  public PageRequest toPageRequest() {
    return PageRequest.of(page, size);
  }
}
