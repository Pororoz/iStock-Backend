package com.pororoz.istock.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceResponse;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNameDuplicatedException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @InjectMocks
  ProductService productService;

  @Mock
  ProductRepository productRepository;

  @Mock
  CategoryRepository categoryRepository;

  @Nested
  @DisplayName("product 저장")
  class SaveProduct {

    SaveProductServiceRequest request = SaveProductServiceRequest.builder().productName("name")
        .categoryId(1L).build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("product를 저장한다.")
      void saveProduct() {
        //given
        Category category = Category.builder().id(1L).build();
        Product product = Product.builder().name("name").category(category).build();
        SaveProductServiceResponse response = SaveProductServiceResponse.builder()
            .productName("name").categoryId(1L).build();

        //when
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(
            Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        SaveProductServiceResponse result = productService.saveProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("categoryId에 해당하는 category가 존재하지 않으면 예외가 발생한다.")
      void categoryIdNotExist() {
        //given
        //when
        when(categoryRepository.findById(anyLong())).thenThrow(
            ProductNameDuplicatedException.class);
        //then
        assertThrows(ProductNameDuplicatedException.class,
            () -> productService.saveProduct(request));
      }

      @Test
      @DisplayName("같은 product name이 이미 존재하면 예외가 발생한다.")
      void productNameDuplicated() {
        //given
        Category category = Category.builder().id(1L).build();

        //when
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.findByName(request.getProductName())).thenReturn(
            Optional.of(mock(Product.class)));

        //then
        assertThrows(ProductNameDuplicatedException.class,
            () -> productService.saveProduct(request));
      }
    }
  }

}