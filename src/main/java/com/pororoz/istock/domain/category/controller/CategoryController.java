package com.pororoz.istock.domain.category.controller;

import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.swagger.exception.AccessForbiddenSwagger;
import com.pororoz.istock.common.swagger.exception.InvalidPageRequestExceptionSwagger;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.FindCategoryRequest;
import com.pororoz.istock.domain.category.dto.request.SaveCategoryRequest;
import com.pororoz.istock.domain.category.dto.request.UpdateCategoryRequest;
import com.pororoz.istock.domain.category.dto.response.CategoryResponse;
import com.pororoz.istock.domain.category.dto.response.FindCategoryResponse;
import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceResponse;
import com.pororoz.istock.domain.category.service.CategoryService;
import com.pororoz.istock.domain.category.swagger.exception.CategoryNotFoundExceptionSwagger;
import com.pororoz.istock.domain.category.swagger.exception.InternalServerErrorExceptionSwagger;
import com.pororoz.istock.domain.category.swagger.response.DeleteCategoryResponseSwagger;
import com.pororoz.istock.domain.category.swagger.response.FindCategoryResponseSwagger;
import com.pororoz.istock.domain.category.swagger.response.SaveCategoryResponseSwagger;
import com.pororoz.istock.domain.category.swagger.response.UpdateCategoryResponseSwagger;
import com.pororoz.istock.domain.user.swagger.exception.InvalidPathExceptionSwagger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category", description = "Category API")
@Validated
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @Operation(summary = "find category", description = "카테고리 리스트 조회 API")
  @ApiResponses({@ApiResponse(responseCode = "200", content = {
      @Content(schema = @Schema(implementation = FindCategoryResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.INVALID_PAGE_REQUEST, content = {
          @Content(schema = @Schema(implementation = InvalidPageRequestExceptionSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))})})
  @PageableAsQueryParam
  @GetMapping
  public ResponseEntity<ResultDTO<PageResponse<FindCategoryResponse>>> findCategories(
      @Valid @ParameterObject @ModelAttribute FindCategoryRequest request,
      @Parameter(hidden = true) Pageable pageable) {
    Page<FindCategoryResponse> categoryPage = categoryService.findCategories(request.toService(),
            pageable)
        .map(FindCategoryServiceResponse::toResponse);
    PageResponse<FindCategoryResponse> response = new PageResponse<>(categoryPage);
    return ResponseEntity.ok(new ResultDTO<>(ResponseStatus.OK, "", response));
  }

  @Operation(summary = "save category", description = "카테고리 생성 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.SAVE_CATEGORY, content = {
          @Content(schema = @Schema(implementation = SaveCategoryResponseSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "500", description = ExceptionMessage.INTERNAL_SERVER_ERROR, content = {
          @Content(schema = @Schema(implementation = InternalServerErrorExceptionSwagger.class))}),})
  @PostMapping
  public ResponseEntity<ResultDTO<CategoryResponse>> saveCategory(
      @Valid @RequestBody SaveCategoryRequest saveCategoryRequest) {
    CategoryServiceResponse serviceDto = categoryService.saveCategory(
        saveCategoryRequest.toService());
    CategoryResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_CATEGORY, response));
  }

  @Operation(summary = "update category", description = "카테고리 수정 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.UPDATE_CATEGORY, content = {
          @Content(schema = @Schema(implementation = UpdateCategoryResponseSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))}),
      @ApiResponse(responseCode = "404", description = ExceptionMessage.CATEGORY_NOT_FOUND, content = {
          @Content(schema = @Schema(implementation = CategoryNotFoundExceptionSwagger.class))}),})
  @PutMapping
  public ResponseEntity<ResultDTO<CategoryResponse>> updateCategory(
      @Valid @RequestBody UpdateCategoryRequest request) {
    CategoryServiceResponse serviceDto = categoryService.updateCategory(request.toService());
    CategoryResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.UPDATE_CATEGORY, response));
  }

  @Operation(summary = "delete category", description = "카테고리 삭제 API")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = ResponseMessage.DELETE_CATEGORY, content = {
          @Content(schema = @Schema(implementation = DeleteCategoryResponseSwagger.class))}),
      @ApiResponse(responseCode = "400", description = ExceptionMessage.INVALID_PATH, content = {
          @Content(schema = @Schema(implementation = InvalidPathExceptionSwagger.class))}),
      @ApiResponse(responseCode = "403", description = ExceptionMessage.FORBIDDEN, content = {
          @Content(schema = @Schema(implementation = AccessForbiddenSwagger.class))})})
  @DeleteMapping("/{categoryId}")
  public ResponseEntity<ResultDTO<CategoryResponse>> deleteCategory(
      @PathVariable("categoryId") @Positive Long categoryId) {
    CategoryServiceResponse serviceDto = categoryService.deleteCategory(categoryId);
    CategoryResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.DELETE_CATEGORY, response));
  }
}
