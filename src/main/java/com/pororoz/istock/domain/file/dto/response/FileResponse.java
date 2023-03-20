package com.pororoz.istock.domain.file.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FileResponse {

  @Schema(description = "제품 아이디", example = "1")
  private Long productId;

}
