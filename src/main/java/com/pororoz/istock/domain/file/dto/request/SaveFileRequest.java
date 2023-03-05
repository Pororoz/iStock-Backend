package com.pororoz.istock.domain.file.dto.request;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.file.dto.service.UploadFileServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveFileRequest {

  @Schema(description = "product 아이디", example = "2")
  @NotNull
  private Long productId;

  public UploadFileServiceRequest toService() {
    return UploadFileServiceRequest.builder()
        .productId(productId)
        .build();
  }

}
