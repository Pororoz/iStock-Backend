package com.pororoz.istock.domain.purchase;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.request.PurchasePartRequest;
import com.pororoz.istock.domain.purchase.dto.request.PurchaseProductRequest;
import com.pororoz.istock.domain.purchase.dto.response.PurchasePartResponse;
import com.pororoz.istock.domain.purchase.dto.response.PurchaseProductResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

public class PurchaseIntegrationTest extends IntegrationTest {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  BomRepository bomRepository;

  @Autowired
  PartRepository partRepository;

  @Autowired
  CategoryRepository categoryRepository;

  final Long productId = 1L;

  final long quantity = 300L;

  @BeforeEach
  void setUp() {
    Category category = categoryRepository.save(Category.builder().categoryName("카테고리").build());
    //product 저장
    List<Product> products = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Product product = productRepository.save(Product.builder()
          .productName("p" + i).productNumber("p" + i)
          .stock((int) (Math.random() * 100) + 1).category(category)
          .build());
      products.add(product);
    }
    //part 저장
    List<Part> parts = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Part part = partRepository.save(Part.builder()
          .partName("p" + i).spec("p" + i)
          .build());
      parts.add(part);
    }
    //일반 bom
    for (int i = 0; i < 9; i++) {
      bomRepository.save(Bom.builder()
          .quantity(1)
          .codeNumber("10").locationNumber("" + i + 100)
          .part(parts.get((int) (Math.random() * 9)))
          .product(products.get((int) (Math.random() * 9)))
          .build());
    }
  }

  @Nested
  @DisplayName("POST /v1/purchase/product - 제품 자재 일괄 구매")
  class PurchaseProduct {

    String url(Long productId) {
      return String.format("http://localhost:8080/v1/purchase/products/%s/waiting", productId);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @WithMockUser
      @DisplayName("제품 자재 일괄 구매 요청에 성공한다.")
      void purchaseProduct() throws Exception {
        //given
        PurchaseProductRequest request = PurchaseProductRequest.builder()
            .quantity(quantity)
            .build();

        PurchaseProductResponse response = PurchaseProductResponse.builder()
            .productId(productId)
            .quantity(quantity)
            .build();

        //when
        ResultActions actions = getResultActions(url(productId), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.PURCHASE_PRODUCT))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser
      @DisplayName("존재하지 않는 Product를 넘겨주면 구매 요청에 실패한다.")
      void productNotFound() throws Exception {
        //given
        PurchaseProductRequest request = PurchaseProductRequest.builder()
            .quantity(100L)
            .build();

        //when
        ResultActions actions = getResultActions(url(100L), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isNotFound())
            .andDo(print());
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 FORBIDDEN을 반환한다.")
      void forbidden() throws Exception {
        //given
        PurchaseProductRequest request = PurchaseProductRequest.builder()
            .quantity(100L)
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
  @DisplayName("POST /v1/purchase/part - 제품 자재 개별 구매")
  class PurchasePart {

    private String url(Long partId) {
      return String.format("http://localhost:8080/v1/purchase/parts/%s/waiting", partId);
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("제품 자재 개별 구매 요청에 성공한다.")
      void purchasePart() throws Exception {
        //given
        PurchasePartRequest request = PurchasePartRequest.builder()
            .quantity(100L)
            .build();

        PurchasePartResponse response = PurchasePartResponse.builder()
            .partId(1L)
            .quantity(100L)
            .build();

        //when
        ResultActions actions = getResultActions(url(1L), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.PURCHASE_PART))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 부품을 요청하면 구매 요청에 실패한다.")
      void productNotFound() throws Exception {
        //given
        PurchasePartRequest request = PurchasePartRequest.builder()
            .quantity(100L)
            .build();

        //when
        ResultActions actions = getResultActions(url(100L), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isNotFound())
            .andDo(print());
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 FORBIDDEN을 반환한다.")
      void forbidden() throws Exception {
        //given
        PurchasePartRequest request = PurchasePartRequest.builder()
            .quantity(100L)
            .build();

        //when
        ResultActions actions = getResultActions(url(1L), HttpMethod.POST, request);

        //then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }
    }
  }
}
