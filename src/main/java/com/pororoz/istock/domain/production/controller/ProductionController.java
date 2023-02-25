package com.pororoz.istock.domain.production.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.production.dto.request.SaveProductionRequest;
import com.pororoz.istock.domain.production.dto.response.SaveProductionResponse;
import com.pororoz.istock.domain.production.service.ProductionService;
import com.pororoz.istock.domain.production.swagger.SaveProductionResponseSwagger;
import com.pororoz.istock.domain.production.swagger.exception.BomAndSubAssyNotMatchExceptionSwagger;
import com.pororoz.istock.domain.production.swagger.exception.ProductOrBomNotFoundExceptionSwagger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Production", description = "Production API")
@Validated
@RestController
@RequestMapping("/v1/production")
@RequiredArgsConstructor
public class ProductionController {

  private final ProductionService productionService;

  @Operation(summary = "wait production", description = "제품 생산 대기 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.SAVE_PRODUCT, content = {
          @Content(schema = @Schema(implementation = SaveProductionResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.BOM_AND_SUB_ASSY_NOT_MATCH, content = {
          @Content(schema = @Schema(implementation = BomAndSubAssyNotMatchExceptionSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PRODUCT_OR_BOM_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = ProductOrBomNotFoundExceptionSwagger.class))})})
  @PostMapping("/products/{productId}/waiting")
  public ResponseEntity<ResultDTO<SaveProductionResponse>> saveWaitingProduction(
      @PathVariable("productId") @Positive Long productId,
      @Valid @RequestBody SaveProductionRequest request) {
    SaveProductionResponse response = productionService
        .saveWaitingProduction(request.toService(productId)).toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.WAIT_PRODUCTION, response));
  }
}
