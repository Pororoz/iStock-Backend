package com.pororoz.istock.domain.part.dto.request;

import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavePartRequest {

  @Schema(description = "품명", example = "BEAD")
  @NotNull
  @Size(max = 100)
  private String partName;

  @Schema(description = "규격", example = "BID/3216")
  @NotNull
  @Size(max = 255)
  private String spec;

  @Schema(description = "단가", example = "10000")
  @Builder.Default
  private long price = 0;

  @Schema(description = "재고", example = "5")
  @Builder.Default
  private long stock = 0;

  public SavePartServiceRequest toService() {
    return SavePartServiceRequest.builder()
        .partName(partName)
        .spec(spec)
        .price(price)
        .stock(stock).build();
  }
}
