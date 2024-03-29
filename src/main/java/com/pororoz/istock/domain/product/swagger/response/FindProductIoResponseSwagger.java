package com.pororoz.istock.domain.product.swagger.response;

import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.product.dto.response.FindProductIoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class FindProductIoResponseSwagger {

  @Schema(description = "Result Code", example = ResponseStatus.OK)
  private String status;

  @Schema(description = "Message", example = ResponseMessage.FIND_PRODUCT_IO)
  private String message;

  private PageResponse<FindProductIoResponse> data;
}
