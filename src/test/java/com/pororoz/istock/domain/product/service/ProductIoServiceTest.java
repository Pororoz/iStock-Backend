package com.pororoz.istock.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.product.dto.service.FindProductIoServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductIoServiceTest {

  @InjectMocks
  ProductIoService productIoService;

  @Mock
  ProductIoRepository productIoRepository;

  @Nested
  @DisplayName("productIo 조회")
  class FindProductIo {

    Category category = Category.builder().id(1L).build();
    Product product = Product.builder().id(1L).category(category).build();
    Product subAssy = Product.builder().id(2L).codeNumber("11").category(category).build();

    @Test
    @DisplayName("'생산대기'의 productIo를 페이지네이션하여 조회한다.")
    void findProductIo() {
      //given
      PageRequest pageRequest = PageRequest.of(0, 5);
      ProductIo productIo = ProductIo.builder()
          .id(1L).quantity(1).status(ProductStatus.생산대기).product(product)
          .build();

      //when
      when(productIoRepository.findByStatusContainingAndProductIdWithProduct(eq("생산대기"),
          eq(product.getId()), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(productIo), pageRequest, 1L));
      Page<FindProductIoServiceResponse> productIoPage = productIoService.findProductIo("생산대기",
          product.getId(), pageRequest);

      //then
      FindProductIoServiceResponse serviceResponse = FindProductIoServiceResponse.builder()
          .productIoId(productIo.getId())
          .quantity(productIo.getQuantity())
          .status(productIo.getStatus())
          .productServiceResponse(ProductServiceResponse.of(product))
          .build();
      assertThat(productIoPage.getTotalPages()).isEqualTo(1);
      assertThat(productIoPage.getTotalElements()).isEqualTo(1);
      assertThat(productIoPage.getContent()).usingRecursiveComparison()
          .isEqualTo(List.of(serviceResponse));
    }

    @Test
    @DisplayName("'사내출고대기'의 subAssyIo를 페이지네이션하여 조회한다.")
    void findSubAssyIo() {
      //given
      PageRequest pageRequest = PageRequest.of(0, 5);
      ProductIo productIo = ProductIo.builder()
          .id(1L).quantity(1).status(ProductStatus.생산대기)
          .product(product)
          .build();
      ProductIo subAssyIo = ProductIo.builder()
          .id(2L).quantity(1).status(ProductStatus.사내출고대기)
          .product(subAssy).superIo(productIo)
          .build();

      //when
      when(productIoRepository.findByStatusContainingAndProductIdWithProduct(eq("사내출고대기"),
          eq(product.getId()), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(subAssyIo), pageRequest, 1L));
      Page<FindProductIoServiceResponse> productIoPage = productIoService.findProductIo("사내출고대기",
          product.getId(), pageRequest);

      //then
      FindProductIoServiceResponse serviceResponse = FindProductIoServiceResponse.builder()
          .productIoId(subAssyIo.getId())
          .quantity(subAssyIo.getQuantity())
          .status(subAssyIo.getStatus())
          .superIoId(productIo.getId())
          .productServiceResponse(ProductServiceResponse.of(subAssy))
          .build();
      assertThat(productIoPage.getTotalPages()).isEqualTo(1);
      assertThat(productIoPage.getTotalElements()).isEqualTo(1);
      assertThat(productIoPage.getContent()).usingRecursiveComparison()
          .isEqualTo(List.of(serviceResponse));
    }
  }

}