package com.pororoz.istock.domain.bom;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.dto.request.SaveBomRequest;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

public class BomIntegrationTest extends IntegrationTest {

  @Autowired
  BomRepository bomRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  PartRepository partRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @AfterEach
  void afterEach() {
    databaseCleanup.execute();
  }

  @Nested
  @DisplayName("POST /api/v1/bom - BOM 행 추가 API")
  class SaveBom {

    Long bomId = 1L;
    String locationNumber = "L5.L4";
    String codeNumber = "";
    Long quantity = 3L;
    String memo = "";
    Long partId = 1L;
    Long productId = 1L;
    String uri = "http://localhost:8080/v1/bom";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      Part part;
      Product product;
      Category category;

      @BeforeEach
      void setup() {
        String nothing = "1";
        long number = 1L;
        part = Part.builder()
            .partName(nothing)
            .spec(nothing)
            .stock(number)
            .price(number)
            .build();
        category = categoryRepository.save(Category.builder().categoryName("카테고리").build());
        product = Product.builder()
            .productName(nothing)
            .productNumber(nothing)
            .codeNumber(nothing)
            .category(category)
            .companyName(nothing)
            .stock(number)
            .build();
        partRepository.save(part);
        productRepository.save(product);
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("모든 값을 정상적으로 넣으면 200 OK와 저장한 Bom Data를 반환한다.")
      void saveBom() throws Exception {
        // given
        SaveBomRequest request = SaveBomRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_BOM))
            .andExpect(jsonPath("$.data.bomId").value(bomId))
            .andExpect(jsonPath("$.data.locationNumber").value(locationNumber))
            .andExpect(jsonPath("$.data.codeNumber").value(codeNumber))
            .andExpect(jsonPath("$.data.quantity").value(quantity))
            .andExpect(jsonPath("$.data.memo").value(memo))
            .andExpect(jsonPath("$.data.partId").value(partId))
            .andExpect(jsonPath("$.data.productId").value(productId))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 partId를 입력하면 400 Bad Request를 반환한다.")
      void notExistedPart() throws Exception {
        // given
        SaveBomRequest request = SaveBomRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.NOT_EXISTED_PART))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.NOT_EXISTED_PART))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 productId를 입력하면 400 Bad Request를 반환한다.")
      void notExistedProduct() throws Exception {
        // given
        String nothing = "1";
        long number = 1L;
        Part part = Part.builder()
            .partName(nothing)
            .spec(nothing)
            .stock(number)
            .price(number)
            .build();
        partRepository.save(part);

        SaveBomRequest request = SaveBomRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.NOT_EXISTED_PRODUCT))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.NOT_EXISTED_PRODUCT))
            .andDo(print());
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 403 Forbidden을 반환한다.")
      void forbidden() throws Exception {

      }
    }
  }
}
