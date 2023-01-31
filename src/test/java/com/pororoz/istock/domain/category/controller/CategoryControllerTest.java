package com.pororoz.istock.domain.category.controller;


import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.dto.request.SaveCategoryRequest;
import com.pororoz.istock.domain.category.dto.response.CategoryResponse;
import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @InjectMocks
    CategoryController categoryController;

    @Mock
    CategoryService categoryService;

    @Nested
    @DisplayName("카테고리 생성하기")
    class SaveCategory {

        private Long id;

        private String categoryName;

        @Nested
        @DisplayName("성공 케이스")
        class SuccessCase {

            @Test
            @DisplayName("카테고리 생성을 성공하면 Category 값을 반환한다.")
            void saveCategory(){
                // given
                id = 1L;
                categoryName = "test";
                SaveCategoryRequest saveCategoryRequest = SaveCategoryRequest.builder()
                        .categoryName(categoryName)
                        .build();
                CategoryServiceResponse categoryServiceResponse = CategoryServiceResponse.builder()
                        .id(id)
                        .name(categoryName)
                        .build();
                CategoryResponse categoryResponse = CategoryResponse.builder()
                        .id(id)
                        .name(categoryName)
                        .build();

                // when
                when(categoryService.saveCategory(any())).thenReturn(categoryServiceResponse);

                //then
                ResponseEntity<ResultDTO<CategoryResponse>> response = categoryController.saveCategory(saveCategoryRequest);
                assertEquals(Objects.requireNonNull(response.getBody()).getData().getName(), categoryResponse.getName());
                assertEquals(Objects.requireNonNull(response.getBody()).getStatus(), ResponseStatus.OK);
                assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), ResponseMessage.SAVE_CATEGORY);
            }

            @Nested
            @DisplayName("실패 케이스")
            class FailCase {

            }
        }


        @Nested
        @DisplayName("카테고리 삭제하기")
        class DeleteCategory {

            private Long id;

            private String categoryName;

            @Nested
            @DisplayName("성공 케이스")
            class SuccessCase {

                @Test
                @DisplayName("존재하는 카테고리를 삭제하면 Category 값을 반환한다.")
                void deleteCategory() {
                    // given
                    id = 1L;
                    categoryName = "착화기";
                    CategoryServiceResponse categoryServiceResponse = CategoryServiceResponse.builder()
                        .id(id)
                        .name(categoryName)
                        .build();
                    CategoryResponse categoryResponse = CategoryResponse.builder()
                        .id(id)
                        .name(categoryName)
                        .build();

                    // when
                    when(categoryService.deleteCategory(any())).thenReturn(categoryServiceResponse);
                    ResponseEntity<ResultDTO<CategoryResponse>> response = categoryController.deleteCategory(id);

                    // then
                    assertEquals(Objects.requireNonNull(response.getBody()).getData().getName(), categoryResponse.getName());
                    assertEquals(Objects.requireNonNull(response.getBody()).getStatus(), ResponseStatus.OK);
                    assertEquals(Objects.requireNonNull(response.getBody()).getMessage(),
                        ResponseMessage.DELETE_CATEGORY);
                }
            }

            @Nested
            @DisplayName("실패 케이스")
            class FailCase {


            }
        }
    }
}