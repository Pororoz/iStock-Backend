package com.pororoz.istock.domain.production.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.production.dto.request.SaveProductionRequest;
import com.pororoz.istock.domain.production.dto.response.SaveProductionResponse;
import com.pororoz.istock.domain.production.service.ProductionService;
import io.swagger.v3.oas.annotations.Operation;
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
  @PostMapping("/products/{productId}/waiting")
  public ResponseEntity<ResultDTO<SaveProductionResponse>> saveWaitingProduction(
      @PathVariable @Positive Long productId,
      @Valid @RequestBody SaveProductionRequest request) {
    SaveProductionResponse response = productionService
        .saveWaitingProduction(request.toService(productId)).toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.WAIT_PRODUCTION, response));
  }
}
