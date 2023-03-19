package com.pororoz.istock.domain.product.dto.service;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.product.dto.response.FindProductIoResponse;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindProductIoServiceResponse {

  private Long productIoId;
  private long quantity;
  private ProductStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long superIoId;
  private ProductServiceResponse productServiceResponse;

  public static FindProductIoServiceResponse of(ProductIo productIo) {
    return FindProductIoServiceResponse.builder()
        .productIoId(productIo.getId())
        .quantity(productIo.getQuantity())
        .status(productIo.getStatus())
        .createdAt(productIo.getCreatedAt())
        .updatedAt(productIo.getUpdatedAt())
        .superIoId(productIo.getSuperIo() == null ? null : productIo.getSuperIo().getId())
        .productServiceResponse(ProductServiceResponse.of(productIo.getProduct()))
        .build();
  }

  public FindProductIoResponse toResponse() {
    return FindProductIoResponse.builder()
        .productIoId(productIoId)
        .quantity(quantity)
        .status(status)
        .superIoId(superIoId)
        .createdAt(TimeEntity.formatTime(createdAt))
        .updatedAt(TimeEntity.formatTime(updatedAt))
        .productId(productServiceResponse.getProductId())
        .productName(productServiceResponse.getProductName())
        .productNumber(productServiceResponse.getProductNumber()).build();
  }
}
