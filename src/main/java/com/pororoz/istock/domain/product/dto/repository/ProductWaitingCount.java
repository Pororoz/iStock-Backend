package com.pororoz.istock.domain.product.dto.repository;

public interface ProductWaitingCount {

  Long getId();

  String getProductName();

  Long getProductionWaitingCount();

  Long getPurchaseWaitingCount();
}
