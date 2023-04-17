package com.pororoz.istock.domain.file.dto;

import com.pororoz.istock.domain.part.dto.repository.PartPurchaseCount;

public class PartPurchaseCountImpl implements PartPurchaseCount {

  private Long id;
  private String partName;
  private String spec;
  private long stock;
  private long purchaseWaitingCount;

  public PartPurchaseCountImpl(Long id, String partName, String spec, long stock,
      long purchaseWaitingCount) {
    this.id = id;
    this.partName = partName;
    this.spec = spec;
    this.stock = stock;
    this.purchaseWaitingCount = purchaseWaitingCount;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public String getPartName() {
    return partName;
  }

  @Override
  public String getSpec() {
    return spec;
  }

  @Override
  public long getStock() {
    return stock;
  }

  @Override
  public long getPurchaseWaitingCount() {
    return purchaseWaitingCount;
  }
}
