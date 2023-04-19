package com.pororoz.istock.domain.file.dto;

import com.pororoz.istock.domain.product.dto.repository.ProductWaitingCount;

public class ProductWaitingCountImpl implements ProductWaitingCount {

  private Long id;
  private String productName;
  private long productionWaitingCount;
  private long purchaseWaitingCount;

  public ProductWaitingCountImpl(Long id, String productName, long productionWaitingCount,
      long purchaseWaitingCount) {
    this.id = id;
    this.productName = productName;
    this.productionWaitingCount = productionWaitingCount;
    this.purchaseWaitingCount = purchaseWaitingCount;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public String getProductName() {
    return productName;
  }

  @Override
  public long getProductionWaitingCount() {
    return productionWaitingCount;
  }

  @Override
  public long getPurchaseWaitingCount() {
    return purchaseWaitingCount;
  }
}
