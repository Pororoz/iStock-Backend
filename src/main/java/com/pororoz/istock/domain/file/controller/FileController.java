package com.pororoz.istock.domain.file.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;

import com.pororoz.istock.domain.file.dto.response.FileResponse;
import com.pororoz.istock.domain.file.dto.service.FileServiceResponse;

import com.pororoz.istock.domain.file.service.FileService;
import com.pororoz.istock.domain.file.swagger.exception.InvalidFileExceptionSwagger;
import com.pororoz.istock.domain.file.swagger.exception.ProductNotFoundExceptionSwagger;
import com.pororoz.istock.domain.file.swagger.response.UploadFileResponseSwagger;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "File", description = "File API")
@Validated
@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  @Operation(summary = "upload file", description = "파일 업로드(import) API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.UPLOAD_CSV,
          content = {@Content(schema = @Schema(implementation = UploadFileResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.INVALID_FILE,
          content = {@Content(schema = @Schema(implementation = InvalidFileExceptionSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
              @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.PRODUCT_NOT_FOUND,
          content = {@Content(schema = @Schema(implementation = ProductNotFoundExceptionSwagger.class))})
  })

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResultDTO<FileResponse>> uploadFile(
          @RequestPart("csvFile") MultipartFile file, @RequestPart("productId") String productId) {
      FileServiceResponse serviceDTO = fileService.uploadFile(file, Long.parseLong(productId));
      FileResponse response = serviceDTO.toResponse();
      return ResponseEntity.ok(new ResultDTO<>(ResponseStatus.OK, ResponseMessage.UPLOAD_CSV, response));
  }


}
