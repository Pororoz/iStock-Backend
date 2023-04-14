package com.pororoz.istock.domain.part.dto.repository;

public interface PartPurchaseCount {

  Long getId();

  String getPartName();

  String getSpec();

  Long getStock();

  Long getPurchaseWaitingCount();
}
