package com.pororoz.istock.domain.product.controller;

import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.swagger.exception.CategoryNotFoundExceptionSwagger;
import com.pororoz.istock.domain.product.dto.request.SaveProductRequest;
import com.pororoz.istock.domain.product.dto.request.UpdateProductRequest;
import com.pororoz.istock.domain.product.dto.response.FindProductResponse;
import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.dto.service.FindProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.FindProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.service.ProductService;
import com.pororoz.istock.domain.product.swagger.exception.ProductNameDuplicatedSwagger;
import com.pororoz.istock.domain.product.swagger.exception.ProductNotFoundSwagger;
import com.pororoz.istock.domain.product.swagger.response.DeleteProductResponseSwagger;
import com.pororoz.istock.domain.product.swagger.response.SaveProductResponseSwagger;
import com.pororoz.istock.domain.product.swagger.response.UpdateProductResponseSwagger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
          @Content(schema = @Schema(implementation = ProductNameDuplicatedSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.CATEGORY_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = CategoryNotFoundExceptionSwagger.class))})})
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
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PRODUCT_NOT_FOUND, content = {
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

  @Operation(summary = "delete product", description = "제품 삭제 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.DELETE_PRODUCT, content = {
          @Content(schema = @Schema(implementation = DeleteProductResponseSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PRODUCT_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = ProductNotFoundSwagger.class))})})
  @DeleteMapping("/{productId}")
  public ResponseEntity<ResultDTO<ProductResponse>> deleteProduct(
      @Valid @PathVariable("productId") @Positive(message = ExceptionMessage.INVALID_PATH) Long productId) {
    ProductServiceResponse serviceDto = productService.deleteProduct(productId);
    ProductResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.DELETE_PRODUCT, response));
  }

  @GetMapping
  public ResponseEntity<ResultDTO<PageResponse<FindProductResponse>>> findProducts(
      @Valid
      @Schema(description = "페이지 요청", example = "0")
      @PositiveOrZero(message = ExceptionMessage.INVALID_PAGE_REQUEST)
      @RequestParam(required = false)
      Integer page,
      @Schema(description = "사이즈 요청", example = "20")
      @Positive(message = ExceptionMessage.INVALID_PAGE_REQUEST)
      @RequestParam(required = false)
      Integer size,
      @Schema(description = "카테고리 아이디", example = "1")
      @PositiveOrZero
      @RequestParam("category-id")
      Long categoryId) {
    FindProductServiceRequest serviceRequest = FindProductServiceRequest.builder()
        .categoryId(categoryId)
        .page(page).size(size)
        .build();
    Page<FindProductResponse> findProductPage = productService.findProducts(serviceRequest)
        .map(FindProductServiceResponse::toResponse);
    PageResponse<FindProductResponse> response = new PageResponse<>(findProductPage);
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.FIND_PRODUCT, response));
  }
}
