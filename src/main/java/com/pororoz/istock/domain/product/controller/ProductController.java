package com.pororoz.istock.domain.product.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.product.dto.request.SaveProductRequest;
import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceResponse;
import com.pororoz.istock.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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

  @PostMapping
  public ResponseEntity<ResultDTO<ProductResponse>> saveProduct(
      @Valid @RequestBody SaveProductRequest saveProductRequest) {
    SaveProductServiceResponse serviceDto = productService.saveProduct(
        saveProductRequest.toService());
    ProductResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_PRODUCT, response));
  }
}
