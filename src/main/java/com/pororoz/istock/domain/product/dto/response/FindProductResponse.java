package com.pororoz.istock.domain.product.dto.response;

import com.pororoz.istock.domain.part.dto.response.PartResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductResponse {

  @Schema(description = "제품 아이디", example = "1")
  private Long productId;

  @Schema(description = "제품명", example = "인덕션 컨트롤러 V1.2")
  private String productName;

  @Schema(description = "제품 번호", example = "GS-IH-01")
  private String productNumber;

  @Schema(description = "코드 번호", example = "0A")
  private String codeNumber;

  @Schema(description = "재고 수량", example = "0")
  private long stock;

  @Schema(description = "거래처 이름", example = "공신금속")
  private String companyName;

  @Schema(description = "카테고리 아이디", example = "1")
  private Long categoryId;

  @Schema(description = "생성 시간", example = "2023-01-01 00:00:00")
  private String createdAt;

  @Schema(description = "수정 시간", example = "2023-01-01 00:00:01")
  private String updatedAt;

  private List<PartResponse> subAssy;
}
