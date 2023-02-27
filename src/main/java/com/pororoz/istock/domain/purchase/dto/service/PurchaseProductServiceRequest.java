package com.pororoz.istock.domain.purchase.dto.service;

import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseProductServiceRequest {

  private Long productId;
  private long amount;

  public ProductIo toProductIo(Product product, ProductStatus productStatus, ProductIo productIo) {
    return ProductIo.builder()
        .quantity(amount)
        .status(productStatus)
        .product(product)
        .superIo(productIo)
        .build();
  }

  public PartIo toPartIo(Part part, ProductIo productIo) {
    return PartIo.builder()
        .quantity(amount)
        .status(PartStatus.구매대기)
        .part(part)
        .productIo(productIo)
        .build();
  }
}
