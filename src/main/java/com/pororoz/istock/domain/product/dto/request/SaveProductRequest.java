package com.pororoz.istock.domain.product.dto.request;

import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
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

  @NotNull
  @Size(max = 100)
  private String productName;

  @NotNull
  @Size(max = 200)
  private String productNumber;

  @Size(max = 20)
  private String codeNumber;

  @PositiveOrZero
  private long stock;

  @Size(max = 50)
  private String companyName;

  @NotNull
  private Long categoryId;

  public SaveProductServiceRequest toService() {
    return SaveProductServiceRequest.builder().productName(productName).productNumber(productNumber)
        .codeNumber(codeNumber).stock(stock).companyName(companyName).categoryId(categoryId)
        .build();
  }
}
