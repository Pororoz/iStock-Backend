package com.pororoz.istock.domain.part.swagger.response;

import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.part.dto.response.PartResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public class DeletePartResponseSwagger {
  @Schema(description = "Result Code", example = ResponseStatus.OK)
  private String status;

  @Schema(description = "Message", example = ResponseMessage.DELETE_PART)
  private String message;

  private PartResponse data;

}
