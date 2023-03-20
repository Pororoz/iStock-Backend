package com.pororoz.istock.domain.file.dto.service;

import com.pororoz.istock.domain.bom.dto.response.BomResponse;
import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.file.dto.response.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FileServiceResponse {
  private Long productId;

  public FileResponse toResponse() {
    return FileResponse.builder()
            .productId(productId)
            .build();
  }

}
