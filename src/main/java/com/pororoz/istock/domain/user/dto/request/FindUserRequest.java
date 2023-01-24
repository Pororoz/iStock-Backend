package com.pororoz.istock.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;


@Getter
@Builder
public class FindUserRequest {

  @Schema(description = "사용자 이름", example = "0")
  private int page;
  @Schema(description = "사용자 이름", example = "20")
  private int size;

  public PageRequest toPageRequest() {
    return PageRequest.of(page, size);
  }
}
