package com.pororoz.istock.domain.purchase.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.purchase.dto.request.PurchasePartRequest;
import com.pororoz.istock.domain.purchase.dto.request.PurchaseProductRequest;
import com.pororoz.istock.domain.purchase.dto.response.PurchasePartResponse;
import com.pororoz.istock.domain.purchase.dto.response.PurchaseProductResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
import com.pororoz.istock.domain.purchase.exception.response.PurchaseProductResponseSwagger;
import com.pororoz.istock.domain.purchase.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Purchase", description = "Purchase API")
@Validated
@RestController
@RequestMapping("/v1/purchase")
@RequiredArgsConstructor
public class PurchaseController {

  private final PurchaseService purchaseService;

  @Operation(summary = "purchase product", description = "제품 자재 일괄 구매 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.PURCHASE_PRODUCT, content = {
          @Content(schema = @Schema(implementation = PurchaseProductResponseSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PRODUCT_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = ProductNotFoundException.class))})
  })
  @PostMapping("/product")
  public ResponseEntity<ResultDTO<PurchaseProductResponse>> purchaseProduct(
      @Valid @RequestBody PurchaseProductRequest purchaseProductRequest) {
    PurchaseProductServiceResponse serviceDto = purchaseService.purchaseProduct(
        purchaseProductRequest.toService());
    PurchaseProductResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.PURCHASE_PRODUCT, response));
  }

  @Operation(summary = "purchase part", description = "제품 자재 개별 구매 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.PURCHASE_PART, content = {
          @Content(schema = @Schema(implementation = PurchaseProductResponseSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PART_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = PartNotFoundException.class))})
  })
  @PostMapping("/part")
  public ResponseEntity<ResultDTO<PurchasePartResponse>> purchasePart(
      @Valid @RequestBody PurchasePartRequest purchasePartRequest) {
    PurchasePartServiceResponse serviceDto = purchaseService.purchasePart(
        purchasePartRequest.toService());
    PurchasePartResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.PURCHASE_PART, response));
  }
}
