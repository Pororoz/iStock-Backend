package com.pororoz.istock.domain.part.dto.service;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.part.dto.response.FindPartIoResponse;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindPartIoServiceResponse {

  private Long partIoId;
  private long quantity;
  private PartStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long productIoId;
  private PartServiceResponse partServiceResponse;
  public static FindPartIoServiceResponse of(PartIo partIo) {
    return FindPartIoServiceResponse.builder()
        .partIoId(partIo.getId())
        .quantity(partIo.getQuantity())
        .status(partIo.getStatus())
        .createdAt(partIo.getCreatedAt())
        .updatedAt(partIo.getUpdatedAt())
        .productIoId(partIo.getProductIo() == null ? null : partIo.getProductIo().getId())
        .partServiceResponse(PartServiceResponse.of(partIo.getPart()))
        .build();
  }

  public FindPartIoResponse toResponse() {
    return FindPartIoResponse.builder()
        .partIoId(partIoId)
        .quantity(quantity)
        .status(status)
        .productIoId(productIoId)
        .createdAt(TimeEntity.formatTime(createdAt))
        .updatedAt(TimeEntity.formatTime(updatedAt))
        .partId(partServiceResponse.getPartId())
        .partName(partServiceResponse.getPartName())
        .spec(partServiceResponse.getSpec()).build();
  }
}
