package com.pororoz.istock.domain.purchase.swagger.response;

import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public class ConfirmSubAssyPurchaseResponseSwagger {

  @Schema(description = "Result Code", example = ResponseStatus.OK)
  private String status;

  @Schema(description = "Message", example = ResponseMessage.CONFIRM_SUB_ASSY_PURCHASE)
  private String message;

  private ProductResponse data;

}
