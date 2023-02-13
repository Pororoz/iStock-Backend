package com.pororoz.istock.domain.part.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.part.dto.request.SavePartRequest;
import com.pororoz.istock.domain.part.dto.response.PartResponse;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.part.service.PartService;
import com.pororoz.istock.domain.part.swagger.exception.PartNameDuplicatedSwagger;
import com.pororoz.istock.domain.part.swagger.exception.PartNotFoundExceptionSwagger;
import com.pororoz.istock.domain.part.swagger.response.DeletePartResponseSwagger;
import com.pororoz.istock.domain.part.swagger.response.SavePartResponseSwagger;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Part", description = "Part API")
@Validated
@RestController
@RequestMapping("/v1/parts")
@RequiredArgsConstructor
public class PartController {

  private final PartService partService;

  @Operation(summary = "save part", description = "Part 추가 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.SAVE_PART, content = {
          @Content(schema = @Schema(implementation = SavePartResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.PART_NAME_DUPLICATED, content = {
          @Content(schema = @Schema(implementation = PartNameDuplicatedSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))})})
  @PostMapping
  public ResponseEntity<ResultDTO<PartResponse>> savePart(
      @Valid @RequestBody SavePartRequest savePartRequest) {
    PartServiceResponse serviceDto = partService.savePart(
        savePartRequest.toService());
    PartResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_PART, response));
  }

  @Operation(summary = "delete part", description = "Part 삭제 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.DELETE_PART, content = {
          @Content(schema = @Schema(implementation = DeletePartResponseSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PART_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = PartNotFoundExceptionSwagger.class))})
  })
  @DeleteMapping("/{partId}")
  public ResponseEntity<ResultDTO<PartResponse>> deletePart(
      @PathVariable("partId") @Positive(message = ExceptionMessage.INVALID_PATH) Long partId) {
    PartServiceResponse serviceDto = partService.deletePart(partId);
    PartResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.DELETE_PART, response));
  }
}
