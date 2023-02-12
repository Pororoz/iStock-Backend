package com.pororoz.istock.domain.bom.dto.request;

import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveBomRequest {

  @Schema(description = "location 번호", example = "L5.L4")
  @Size(max = 20)
  private String locationNumber = "0";

  @Schema(description = "코드 번호", example = "0A")
  @Size(max = 20)
  private String codeNumber;

  @Schema(description = "제품 수량", example = "3")
  @PositiveOrZero
  private long quantity = 0;

  @Schema(description = "비고", example = "비고")
  private String memo;

  @Schema(description = "part 아이다", example = "1")
  @NotNull
  private Long partId;

  @Schema(description = "product 아이디", example = "2")
  @NotNull
  private Long productId;

  public SaveBomServiceRequest toService() {
    return SaveBomServiceRequest.builder()
        .locationNumber(locationNumber)
        .codeNumber(codeNumber)
        .quantity(quantity)
        .memo(memo)
        .partId(partId)
        .productId(productId)
        .build();
  }
}
