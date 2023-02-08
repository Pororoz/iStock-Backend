package com.pororoz.istock.domain.bom.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.dto.request.SaveBomRequest;
import com.pororoz.istock.domain.bom.dto.response.BomResponse;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceResponse;
import com.pororoz.istock.domain.bom.service.BomService;
import com.pororoz.istock.domain.bom.swagger.exception.NotExistedPartExceptionSwagger;
import com.pororoz.istock.domain.bom.swagger.response.SaveBomResponseSwagger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BOM", description = "BOM API")
@RequiredArgsConstructor
@RequestMapping("/v1/bom")
@RestController
public class BomController {

  private final BomService bomService;

  @Operation(summary = "save bom", description = "BOM 행 추가 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.SAVE_BOM, content = {
          @Content(schema = @Schema(implementation = SaveBomResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.NOT_EXISTED_PART, content = {
          @Content(schema = @Schema(implementation = NotExistedPartExceptionSwagger.class))}
      ),
  })
  @PostMapping
  public ResponseEntity<ResultDTO<BomResponse>> saveBom(
      @Valid @RequestBody SaveBomRequest request) {
    SaveBomServiceResponse serviceDto = bomService.saveBom(request.toService());
    BomResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_BOM, response));
  }
}
