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
import com.pororoz.istock.domain.outbound.dto.response.OutboundResponse;
import com.pororoz.istock.domain.product.entity.Product;
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
        Category category = categoryRepository.save(Category.builder().categoryName("카테고리").build());
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
        assertThat(changedProduct.getStock()).isEqualTo(quantity-requestQuantity);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

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
}
