package com.pororoz.istock.domain.category.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.SaveCategoryRequest;
import com.pororoz.istock.domain.category.dto.response.CategoryResponse;
import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.service.CategoryService;
import com.pororoz.istock.domain.category.swagger.exception.InternalServerErrorExceptionSwagger;
import com.pororoz.istock.domain.category.swagger.response.SaveCategoryResponseSwagger;
import com.pororoz.istock.domain.user.dto.response.UserResponse;
import com.pororoz.istock.domain.user.dto.service.DeleteUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.swagger.exception.InvalidPathExceptionSwagger;
import com.pororoz.istock.domain.user.swagger.exception.RoleNotFoundExceptionSwagger;
import com.pororoz.istock.domain.user.swagger.exception.UserNotFoundExceptionSwagger;
import com.pororoz.istock.domain.user.swagger.response.DeleteUserResponseSwagger;
import com.pororoz.istock.domain.user.swagger.response.SaveUserResponseSwagger;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "Category API")
@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "save category", description = "카테고리 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = ResponseMessage.SAVE_CATEGORY,
                    content = {@Content(schema = @Schema(implementation = SaveCategoryResponseSwagger.class))}),
            @ApiResponse(responseCode = "400", description = ExceptionMessage.INTERTNAL_SERVER_ERROR,
                    content = {@Content(schema = @Schema(implementation = InternalServerErrorExceptionSwagger.class))})
    })
    @PostMapping
    public ResponseEntity<ResultDTO<CategoryResponse>> saveCategory(@Valid @RequestBody SaveCategoryRequest saveCategoryRequest){
        CategoryServiceResponse serviceDto = categoryService.saveCategory(saveCategoryRequest.toService());
        CategoryResponse response = serviceDto.toResponse();
        return ResponseEntity.ok(new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_CATEGORY, response));
    }

}
