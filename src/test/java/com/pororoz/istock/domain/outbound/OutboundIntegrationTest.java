package com.pororoz.istock.domain.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.outbound.dto.request.OutboundRequest;
import com.pororoz.istock.domain.outbound.dto.response.OutboundUpdateResponse;
import com.pororoz.istock.domain.outbound.dto.response.OutboundResponse;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.ProductIoNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

public class OutboundIntegrationTest extends IntegrationTest {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ProductIoRepository productIoRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Nested
  @DisplayName("POST /v1/outbounds/products/{productId}/waiting - 제품 출고 대기")
  class Outbound {

    String url(Long productId) {
      return "http://localhost:8080/v1/outbounds/products/" + productId + "/waiting";
    }

    final Long productId = 1L;
    final Long quantity = 150L;
    final Long requestQuantity = 100L;
    final String name = "product1";
    final String number = "GS-IH-01";
    final String companyName = "company name";
    final String codeNumber = "code number";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @BeforeEach
      void setup() {
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        productRepository.save(
            Product.builder()
                .productName(name)
                .productNumber(number)
                .codeNumber(codeNumber)
                .companyName(companyName)
                .stock(quantity)
                .category(category)
                .build());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("productId와 quantity가 포함된 정보를 제공하면 200 OK와 data를 전달한다.")
      void outbound() throws Exception {
        // given
        OutboundRequest request = OutboundRequest.builder()
            .quantity(requestQuantity)
            .build();

        // when
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        // then
        OutboundResponse response = OutboundResponse.builder()
            .productId(productId)
            .quantity(requestQuantity)
            .build();
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.OUTBOUND_WAIT))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());

        Product changedProduct = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);
        assertThat(changedProduct.getStock()).isEqualTo(quantity - requestQuantity);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("현재 존재하는 quantity보다 많은 양을 요구하면 Error가 발생한다.")
      void quantityError() throws Exception {
        // given
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        productRepository.save(
            Product.builder()
                .productName(name)
                .productNumber(number)
                .codeNumber(codeNumber)
                .companyName(companyName)
                .stock(quantity)
                .category(category)
                .build());
        OutboundRequest request = OutboundRequest.builder()
            .quantity(quantity + 100L)
            .build();

        // when
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PRODUCT_STOCK_MINUS));
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("productId에 따른 product가 존재하지 않을 때 404 Not Found를 반환한다.")
      void productNotFound() throws Exception {
        // given
        OutboundRequest request = OutboundRequest.builder()
            .quantity(requestQuantity)
            .build();

        // when
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PRODUCT_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PRODUCT_NOT_FOUND));
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 FORBIDDEN을 반환한다.")
      void forbidden() throws Exception {
        //given
        OutboundRequest request = OutboundRequest.builder()
            .quantity(requestQuantity)
            .build();

        //when
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }

    }
  }

  @Nested
  @DisplayName("POST /api/v1/outbounds/product-io/{productIoId}/confirm - 제품 출고 확정")
  class OutboundConfirm {

    String url(Long productIoId) {
      return "http://localhost:8080/v1/outbounds/product-io/" + productIoId + "/confirm";
    }

    final Long productId = 1L;
    final Long productIoId = 1L;
    final Long productQuantity = 50L;
    final Long productIoQuantity = 100L;
    final String name = "product1";
    final String number = "GS-IH-01";
    final String companyName = "company name";
    final String codeNumber = "code number";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @BeforeEach
      void setup() {
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        Product product = productRepository.save(
            Product.builder()
                .productName(name)
                .productNumber(number)
                .codeNumber(codeNumber)
                .companyName(companyName)
                .stock(productQuantity)
                .category(category)
                .build());
        productIoRepository.save(ProductIo.builder()
            .status(ProductStatus.출고대기)
            .quantity(productIoQuantity)
            .product(product)
            .build());
      }

      @Test
      @WithMockUser(roles = "USER")
      @DisplayName("productIoId, productId와 quantity가 포함된 정보를 제공하면 200 OK와 data를 전달한다.")
      void outboundConfirm() throws Exception {
        // given

        // when
        ResultActions actions = getResultActions(url(productIoId), HttpMethod.POST);

        // then
        OutboundUpdateResponse response = OutboundUpdateResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(productIoQuantity)
            .build();
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.OUTBOUND_CONFIRM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());

        // productIo의 status가 변경됐는지 확인
        ProductIo changedProductIo = productIoRepository.findById(productIoId)
            .orElseThrow(ProductIoNotFoundException::new);
        assertThat(changedProductIo.getStatus()).isEqualTo(ProductStatus.출고완료);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("productIo의 status가 출고대기 상태가 아니라면 400 BadRequest Error를 발생시킨다.")
      void productIoStatusError() throws Exception {
        // given
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        Product product = productRepository.save(
            Product.builder()
                .productName(name)
                .productNumber(number)
                .codeNumber(codeNumber)
                .companyName(companyName)
                .stock(productQuantity)
                .category(category)
                .build());
        productIoRepository.save(ProductIo.builder()
            .status(ProductStatus.출고완료)
            .quantity(productIoQuantity)
            .product(product)
            .build());

        // when
        ResultActions actions = getResultActions(url(productIoId), HttpMethod.POST);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.CHANGE_OUTBOUND_STATUS))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("productIo가 존재하지 않으면 403 Not Found Error를 발생시킨다.")
      void productIoNotFound() throws Exception {
        // given

        // when
        ResultActions actions = getResultActions(url(productIoId), HttpMethod.POST);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PRODUCT_IO_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PRODUCT_IO_NOT_FOUND));
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 FORBIDDEN을 반환한다.")
      void forbidden() throws Exception {
        //given

        //when
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST);

        //then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("POST /api/v1/outbounds/product-io/{productIoId}/cancel - 제품 출고 취소")
  class OutboundCancel {

    String url(Long productIoId) {
      return "http://localhost:8080/v1/outbounds/product-io/" + productIoId + "/cancel";
    }

    final Long productId = 1L;
    final Long productIoId = 1L;
    final Long productQuantity = 50L;
    final Long productIoQuantity = 100L;
    final String name = "product1";
    final String number = "GS-IH-01";
    final String companyName = "company name";
    final String codeNumber = "code number";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @BeforeEach
      void setup() {
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        Product product = productRepository.save(
            Product.builder()
                .productName(name)
                .productNumber(number)
                .codeNumber(codeNumber)
                .companyName(companyName)
                .stock(productQuantity)
                .category(category)
                .build());
        productIoRepository.save(ProductIo.builder()
            .status(ProductStatus.출고대기)
            .quantity(productIoQuantity)
            .product(product)
            .build());
      }

      @Test
      @WithMockUser(roles = "USER")
      @DisplayName("productIoId, productId와 quantity가 포함된 정보를 제공하면 200 OK와 data를 전달한다.")
      void outboundConfirm() throws Exception {
        // given

        // when
        ResultActions actions = getResultActions(url(productIoId), HttpMethod.POST);

        // then
        OutboundUpdateResponse response = OutboundUpdateResponse.builder()
            .productIoId(productIoId)
            .productId(productId)
            .quantity(productIoQuantity)
            .build();
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.OUTBOUND_CANCEL))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());

        // productIo의 status가 변경됐는지 확인
        ProductIo changedProductIo = productIoRepository.findById(productIoId)
            .orElseThrow(ProductIoNotFoundException::new);
        assertThat(changedProductIo.getStatus()).isEqualTo(ProductStatus.출고취소);

        // product의 수량이 변경됐는지 확인
        Product changedProduct = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);
        assertThat(changedProduct.getStock()).isEqualTo(productQuantity + productIoQuantity);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("productIo의 status가 출고대기 상태가 아니라면 400 BadRequest Error를 발생시킨다.")
      void productIoStatusError() throws Exception {
        // given
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        Product product = productRepository.save(
            Product.builder()
                .productName(name)
                .productNumber(number)
                .codeNumber(codeNumber)
                .companyName(companyName)
                .stock(productQuantity)
                .category(category)
                .build());
        productIoRepository.save(ProductIo.builder()
            .status(ProductStatus.출고완료)
            .quantity(productIoQuantity)
            .product(product)
            .build());

        // when
        ResultActions actions = getResultActions(url(productIoId), HttpMethod.POST);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.CHANGE_OUTBOUND_STATUS))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("productIo가 존재하지 않으면 403 Not Found Error를 발생시킨다.")
      void productIoNotFound() throws Exception {
        // given

        // when
        ResultActions actions = getResultActions(url(productIoId), HttpMethod.POST);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PRODUCT_IO_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PRODUCT_IO_NOT_FOUND));
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 FORBIDDEN을 반환한다.")
      void forbidden() throws Exception {
        //given

        //when
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST);

        //then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }
    }
  }
}
