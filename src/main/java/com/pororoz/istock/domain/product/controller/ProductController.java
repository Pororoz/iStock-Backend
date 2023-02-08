package com.pororoz.istock.domain.product.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.product.dto.request.SaveProductRequest;
import com.pororoz.istock.domain.product.dto.request.UpdateProductRequest;
import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.service.ProductService;
import com.pororoz.istock.domain.product.swagger.exception.ProductNameDuplicatedSwagger;
import com.pororoz.istock.domain.product.swagger.exception.ProductNotFoundSwagger;
import com.pororoz.istock.domain.product.swagger.response.SaveProductResponseSwagger;
import com.pororoz.istock.domain.product.swagger.response.UpdateProductResponseSwagger;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "Product API")
@Validated
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @Operation(summary = "save product", description = "제품 생성 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.SAVE_PRODUCT, content = {
          @Content(schema = @Schema(implementation = SaveProductResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.PRODUCT_NUMBER_DUPLICATED, content = {
          @Content(schema = @Schema(implementation = ProductNameDuplicatedSwagger.class))})})
  @PostMapping
  public ResponseEntity<ResultDTO<ProductResponse>> saveProduct(
      @Valid @RequestBody SaveProductRequest saveProductRequest) {
    ProductServiceResponse serviceDto = productService.saveProduct(saveProductRequest.toService());
    ProductResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_PRODUCT, response));
  }

  @Operation(summary = "update product", description = "제품 수정 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.UPDATE_PRODUCT, content = {
          @Content(schema = @Schema(implementation = UpdateProductResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.PRODUCT_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = ProductNotFoundSwagger.class))})})
  @PutMapping
  public ResponseEntity<ResultDTO<ProductResponse>> updateProduct(
      @Valid @RequestBody UpdateProductRequest updateProductRequest) {
    ProductServiceResponse serviceDto = productService.updateProduct(
        updateProductRequest.toService());
    ProductResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.UPDATE_PRODUCT, response));
  }
}
