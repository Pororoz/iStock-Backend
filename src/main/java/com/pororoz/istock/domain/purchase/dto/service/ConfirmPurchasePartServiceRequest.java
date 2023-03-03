package com.pororoz.istock.domain.purchase.dto.service;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConfirmPurchasePartServiceRequest {

  private Long partIoId;

}
