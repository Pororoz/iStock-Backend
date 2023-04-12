package com.pororoz.istock.domain.product.controller;

import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.product.dto.response.FindProductIoResponse;
import com.pororoz.istock.domain.product.dto.service.FindProductIoServiceResponse;
import com.pororoz.istock.domain.product.service.ProductIoService;
import com.pororoz.istock.domain.product.swagger.response.FindProductIoResponseSwagger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ProductIo", description = "ProductIo API")
@Validated
@RestController
@RequestMapping("/v1/product-io")
@RequiredArgsConstructor
public class ProductIoController {

  private final ProductIoService productIoService;

  @Operation(summary = "find product-io", description = "제품 IO 조회 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.FIND_PRODUCT_IO, content = {
          @Content(schema = @Schema(implementation = FindProductIoResponseSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))})})
  @PageableAsQueryParam
  @GetMapping
  public ResponseEntity<ResultDTO<PageResponse<FindProductIoResponse>>> findProductIo(
      @Parameter(hidden = true) Pageable pageable,
      @Schema(description = "제품 상태", example = "대기")
      @RequestParam(value = "status", required = false) String status,
      @Schema(description = "제품 id", example = "1")
      @RequestParam(value = "product-id", required = false) Long productId) {
    Page<FindProductIoResponse> productIoPage = productIoService.findProductIo(status, productId,
            pageable)
        .map(FindProductIoServiceResponse::toResponse);
    PageResponse<FindProductIoResponse> response = new PageResponse<>(productIoPage);
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.FIND_PRODUCT_IO, response));
  }
}
