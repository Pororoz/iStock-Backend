package com.pororoz.istock.domain.outbound.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.outbound.dto.request.OutboundRequest;
import com.pororoz.istock.domain.outbound.dto.response.OutboundUpdateResponse;
import com.pororoz.istock.domain.outbound.dto.response.OutboundResponse;
import com.pororoz.istock.domain.outbound.dto.service.OutboundUpdateServiceRequest;
import com.pororoz.istock.domain.outbound.dto.service.OutboundUpdateServiceResponse;
import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceResponse;
import com.pororoz.istock.domain.outbound.service.OutboundService;
import com.pororoz.istock.domain.outbound.swagger.exception.ProductIdNotPositiveExceptionSwagger;
import com.pororoz.istock.domain.outbound.swagger.exception.ProductIoIdNotPositiveExceptionSwagger;
import com.pororoz.istock.domain.outbound.swagger.response.OutboundCancelResponseSwagger;
import com.pororoz.istock.domain.outbound.swagger.response.OutboundConfirmResponseSwagger;
import com.pororoz.istock.domain.outbound.swagger.response.OutboundResponseSwagger;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.swagger.exception.ProductIoNotFoundExceptionSwagger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Outbound", description = "Outbound API")
@Validated
@RestController
@RequestMapping("/v1/outbounds")
@RequiredArgsConstructor
public class OutboundController {

  private final OutboundService outboundService;

  @Operation(summary = "outbound wait", description = "제품 출고 대기")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.OUTBOUND_WAIT, content = {
          @Content(schema = @Schema(implementation = OutboundResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.BAD_REQUEST, content = {
          @Content(schema = @Schema(implementation = ProductIdNotPositiveExceptionSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PRODUCT_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = ProductNotFoundException.class))}),
  })
  @PostMapping("/products/{productId}/waiting")
  public ResponseEntity<ResultDTO<OutboundResponse>> outbound(
      @PathVariable("productId") @NotNull @Positive long productId,
      @Valid @RequestBody OutboundRequest request
  ) {
    OutboundServiceResponse serviceDto = outboundService.outbound(request.toService(productId));
    OutboundResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.OUTBOUND_WAIT, response));
  }

  @Operation(summary = "outbound confirm", description = "제품 출고 확정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.OUTBOUND_CONFIRM, content = {
          @Content(schema = @Schema(implementation = OutboundConfirmResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.BAD_REQUEST, content = {
          @Content(schema = @Schema(implementation = ProductIoIdNotPositiveExceptionSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PRODUCT_IO_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = ProductIoNotFoundExceptionSwagger.class))}),
  })
  @PostMapping("/product-io/{productIoId}/confirm")
  public ResponseEntity<ResultDTO<OutboundUpdateResponse>> outboundConfirm(
      @PathVariable("productIoId") @NotNull @Positive long productIoId
  ) {
    OutboundUpdateServiceResponse serviceDto = outboundService.outboundConfirm(
        OutboundUpdateServiceRequest.builder().productIoId(productIoId).build());
    OutboundUpdateResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.OUTBOUND_CONFIRM, response));
  }

  @Operation(summary = "outbound cancel", description = "제품 출고 취소")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.OUTBOUND_CANCEL, content = {
          @Content(schema = @Schema(implementation = OutboundCancelResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.BAD_REQUEST, content = {
          @Content(schema = @Schema(implementation = ProductIoIdNotPositiveExceptionSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PRODUCT_IO_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = ProductIoNotFoundExceptionSwagger.class))}),
  })
  @PostMapping("/product-io/{productIoId}/cancel")
  public ResponseEntity<ResultDTO<OutboundUpdateResponse>> outboundCancel(
      @PathVariable("productIoId") @NotNull @Positive long productIoId
  ) {
    OutboundUpdateServiceResponse serviceDto = outboundService.outboundCancel(
        OutboundUpdateServiceRequest.builder().productIoId(productIoId).build());
    OutboundUpdateResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.OUTBOUND_CANCEL, response));
  }
}
