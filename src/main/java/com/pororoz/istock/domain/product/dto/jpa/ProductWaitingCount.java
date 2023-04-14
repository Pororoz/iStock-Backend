package com.pororoz.istock.domain.product.dto.jpa;

public interface ProductWaitingCount {

  Long getId();

  String getProductName();

  Long getProductionWaitingCount();

  Long getPurchaseWaitingCount();
}
