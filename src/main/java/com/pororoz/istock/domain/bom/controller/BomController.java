package com.pororoz.istock.domain.bom.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.dto.request.DeleteBomRequest;
import com.pororoz.istock.domain.bom.dto.request.SaveBomRequest;
import com.pororoz.istock.domain.bom.dto.request.UpdateBomRequest;
import com.pororoz.istock.domain.bom.dto.response.BomResponse;
import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.service.BomService;
import com.pororoz.istock.domain.bom.swagger.exception.BomIdBadRequestExceptionSwagger;
import com.pororoz.istock.domain.bom.swagger.exception.BomNotFoundExceptionSwagger;
import com.pororoz.istock.domain.bom.swagger.exception.PartIdBadRequestExceptionSwagger;
import com.pororoz.istock.domain.bom.swagger.response.DeleteBomResponseSwagger;
import com.pororoz.istock.domain.bom.swagger.response.SaveBomResponseSwagger;
import com.pororoz.istock.domain.bom.swagger.response.UpdateBomResponseSwagger;
import com.pororoz.istock.domain.part.swagger.exception.PartNotFoundExceptionSwagger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
          @Content(schema = @Schema(implementation = SaveBomResponseSwagger.class))}
      ),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.BAD_REQUEST, content = {
          @Content(schema = @Schema(implementation = PartIdBadRequestExceptionSwagger.class))}
      ),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}
      ),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PART_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = PartNotFoundExceptionSwagger.class))}
      ),
  })
  @PostMapping
  public ResponseEntity<ResultDTO<BomResponse>> saveBom(
      @Valid @RequestBody SaveBomRequest request) {
    BomServiceResponse serviceDto = bomService.saveBom(request.toService());
    BomResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_BOM, response));
  }

  @Operation(summary = "save bom", description = "BOM 행 추가 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.DELETE_BOM, content = {
          @Content(schema = @Schema(implementation = DeleteBomResponseSwagger.class))}
      ),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.BAD_REQUEST, content = {
          @Content(schema = @Schema(implementation = BomIdBadRequestExceptionSwagger.class))}
      ),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}
      ),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.BOM_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = BomNotFoundExceptionSwagger.class))}
      ),
  })
  @DeleteMapping
  public ResponseEntity<ResultDTO<BomResponse>> deleteBom(
      @Valid @ModelAttribute("request") DeleteBomRequest request) {
    BomServiceResponse serviceDto = bomService.deleteBom(request.toService());
    BomResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.DELETE_BOM, response));
  }

  @Operation(summary = "save bom", description = "BOM 행 수정 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.UPDATE_BOM, content = {
          @Content(schema = @Schema(implementation = UpdateBomResponseSwagger.class))}
      ),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.BAD_REQUEST, content = {
          @Content(schema = @Schema(implementation = BomIdBadRequestExceptionSwagger.class))}
      ),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}
      ),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.BOM_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = BomNotFoundExceptionSwagger.class))}
      ),
  })
  @PutMapping
  public ResponseEntity<ResultDTO<BomResponse>> updateBom(
      @Valid @RequestBody UpdateBomRequest request) {
    BomServiceResponse serviceDto = bomService.updateBom(request.toService());
    BomResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.UPDATE_BOM, response));
  }
}
