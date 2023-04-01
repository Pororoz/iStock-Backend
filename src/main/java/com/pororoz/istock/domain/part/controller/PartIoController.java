package com.pororoz.istock.domain.part.controller;

import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.part.dto.response.FindPartIoResponse;
import com.pororoz.istock.domain.part.dto.service.FindPartIoServiceResponse;
import com.pororoz.istock.domain.part.service.PartIoService;
import com.pororoz.istock.domain.part.swagger.response.FindPartIoResponseSwagger;
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

@Tag(name = "PartIo", description = "PartIo API")
@Validated
@RestController
@RequestMapping("/v1/part-io")
@RequiredArgsConstructor
public class PartIoController {

  private final PartIoService partIoService;

  @Operation(summary = "find part-io", description = "부품 IO 조회 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.FIND_PART_IO, content = {
          @Content(schema = @Schema(implementation = FindPartIoResponseSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))})})
  @PageableAsQueryParam
  @GetMapping
  public ResponseEntity<ResultDTO<PageResponse<FindPartIoResponse>>> findPartIO(
      @Parameter(hidden = true) Pageable pageable,
      @Schema(description = "부품 상태", example = "대기")
      @RequestParam(value = "status", required = false) String status,
      @Schema(description = "부품 id", example = "1")
      @RequestParam(value = "part-id", required = false) Long partId) {
    Page<FindPartIoResponse> partIoPage = partIoService.findPartIo(status, partId, pageable)
        .map(FindPartIoServiceResponse::toResponse);
    PageResponse<FindPartIoResponse> response = new PageResponse<>(partIoPage);
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.FIND_PART_IO, response));
  }
}
