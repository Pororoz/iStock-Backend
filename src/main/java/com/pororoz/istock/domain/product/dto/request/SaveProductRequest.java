package com.pororoz.istock.domain.product.dto.request;

import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
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
public class SaveProductRequest {

  @Schema(description = "제품명", example = "인덕션 컨트롤러 V1.2")
  @NotNull
  @Size(max = 100)
  private String productName;

  @Schema(description = "제품 번호", example = "GS-IH-01")
  @NotNull
  @Size(max = 100)
  private String productNumber;

  @Schema(description = "코드 번호", example = "0A")
  @Size(max = 20)
  private String codeNumber;

  @Schema(description = "재고 수량", example = "0")
  @PositiveOrZero
  private long stock;

  @Schema(description = "거래처 이름", example = "pororoz")
  @Size(max = 50)
  private String companyName;

  @Schema(description = "카테고리 아이디", example = "1")
  @NotNull
  private Long categoryId;

  public SaveProductServiceRequest toService() {
    return SaveProductServiceRequest.builder()
        .productName(productName).productNumber(productNumber)
        .codeNumber(codeNumber).stock(stock)
        .companyName(companyName).categoryId(categoryId)
        .build();
  }
}
