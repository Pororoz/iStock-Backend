package com.pororoz.istock.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNumberDuplicatedException;
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


  final Long id = 1L;
  final String productName = "인덕션 컨트롤러 V1.2";
  final String productNumber = "GS-IH-01";
  final String codeNumber = "";
  final long stock = 15;
  final String companyName = "공신금속";
  final Long categoryId = 1L;
  final Category category = Category.builder().id(categoryId).build();
  final Product product = Product.builder()
      .id(id).productName(productName)
      .productNumber(productNumber).codeNumber(codeNumber)
      .stock(stock).companyName(companyName)
      .category(category)
      .build();

  @Nested
  @DisplayName("product 저장")
  class SaveProduct {

    SaveProductServiceRequest request = SaveProductServiceRequest.builder()
        .productNumber(productNumber).categoryId(categoryId)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("product를 저장한다.")
      void saveProduct() {
        //given
        Product product = Product.builder()
            .productName(productName).category(category)
            .build();
        ProductServiceResponse response = ProductServiceResponse.builder()
            .productName(productName).category(category)
            .build();

        //when
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(
            Optional.of(category));
        ProductServiceResponse result = productService.saveProduct(request);

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
        when(productRepository.findByProductNumber(request.getProductNumber()))
            .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        //then
        assertThrows(CategoryNotFoundException.class,
            () -> productService.saveProduct(request));
      }

      @Test
      @DisplayName("같은 product number가 이미 존재하면 예외가 발생한다.")
      void productNameDuplicated() {
        //given
        //when
        when(productRepository.findByProductNumber(request.getProductNumber())).thenReturn(
            Optional.of(mock(Product.class)));

        //then
        assertThrows(ProductNumberDuplicatedException.class,
            () -> productService.saveProduct(request));
      }
    }
  }

  @Nested
  @DisplayName("product 수정")
  class UpdateProduct {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("product를 수정한다.")
      void updateProduct() {
        //given
        Category newCategory = Category.builder()
            .id(categoryId + 1).categoryName("new category")
            .build();
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("new cnumber")
            .stock(stock + 1).companyName("new cname")
            .categoryId(categoryId + 1)
            .build();
        ProductServiceResponse response = ProductServiceResponse.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("new cnumber")
            .stock(stock + 1).companyName("new cname")
            .category(newCategory)
            .build();

        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCategory.getId())).thenReturn(Optional.of(newCategory));
        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("id에 해당하는 product가 없으면 에러가 발생한다.")
      void productNotFound() {
        //given
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(2L)
            .build();

        //when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(request));
      }

      @Test
      @DisplayName("변경하려는 품번이 이미 있으면 예외가 발생한다.")
      void duplicateProductNumber() {
        //given
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(1L).productNumber("new")
            .build();
        Product product = Product.builder()
            .id(1L).productNumber("old")
            .build();

        //when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(productRepository.findByProductNumber(request.getProductNumber()))
            .thenReturn(Optional.of(mock(Product.class)));

        //then
        assertThrows(ProductNumberDuplicatedException.class,
            () -> productService.updateProduct(request));
      }

      @Test
      @DisplayName("categoryId 에 해당하는 category가 없으면 에러가 발생한다.")
      void categoryNotFound() {
        //given
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(1L).productNumber("number")
            .categoryId(2L)
            .build();

        //when
        when(productRepository.findById(request.getProductId())).thenReturn(
            Optional.of(mock(Product.class)));
        when(productRepository.findByProductNumber(request.getProductNumber()))
            .thenReturn(Optional.empty());
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.empty());

        //then
        assertThrows(CategoryNotFoundException.class, () -> productService.updateProduct(request));
      }
    }
  }

  @Nested
  @DisplayName("product 삭제")
  class DeleteProduct {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("product를 삭제한다.")
      void deleteProduct() {
        //given
        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        //then
        ProductServiceResponse response = ProductServiceResponse.builder()
            .productId(id).productNumber(productNumber)
            .productName(productName).stock(stock)
            .companyName(companyName).codeNumber(codeNumber)
            .category(category)
            .build();
        assertThat(productService.deleteProduct(id)).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("product id에 맞는 product를 찾을 수 없으면 예외가 발생한다.")
      void productNotFoundException() {
        //given
        //when
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
      }
    }


  }
}