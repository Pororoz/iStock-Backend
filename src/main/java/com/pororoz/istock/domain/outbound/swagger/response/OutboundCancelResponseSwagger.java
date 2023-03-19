package com.pororoz.istock.domain.outbound.swagger.response;

import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.outbound.dto.response.OutboundUpdateResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class OutboundCancelResponseSwagger {

  @Schema(description = "Result Code", example = ResponseStatus.OK)
  private String status;

  @Schema(description = "Message", example = ResponseMessage.OUTBOUND_CANCEL)
  private String message;

  private OutboundUpdateResponse data;
}
