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
import com.pororoz.istock.domain.bom.entity.Bom;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PRODUCT_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PRODUCT_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("API 요청 시, partId의 값으로 null을 전달하면 400 Bad Request를 반환한다.")
      void badRequestPartId() throws Exception {
        // given
        SaveBomRequest request = SaveBomRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(null)
            .productId(productId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("API 요청 시, productId의 값으로 null을 전달하면 400 Bad Request를 반환한다.")
      void badRequestProductId() throws Exception {
        // given
        SaveBomRequest request = SaveBomRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(null)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 403 Forbidden을 반환한다.")
      void forbidden() throws Exception {
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
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/bom - BOM 행 제거 API")
  class deleteBom {

    Long bomId = 1L;
    String locationNumber = "L5.L4";
    String codeNumber = "";
    Long quantity = 3L;
    String memo = "";
    Long partId = 1L;
    Long productId = 1L;

    MultiValueMap<String, String> params;
    String uri = "http://localhost:8080/v1/bom";

    @BeforeEach
    void setup() {
      params = new LinkedMultiValueMap<>();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      Part part;
      Product product;
      Category category;
      Bom bom;

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
        bom = Bom.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .product(product)
            .part(part)
            .build();
        bomRepository.save(bom);
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하는 BOM을 삭제하면 해당 BOM 데이터 값과 200 OK를 반환한다.")
      void deleteBom() throws Exception {
        // given
        params.add("bomId", Long.toString(bomId));

        // when
        ResultActions actions = deleteWithParams(uri, params);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_BOM))
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
      @DisplayName("존재하지 않는 BOM 값을 삭제 시도하면 400 Bad Request를 반환한다.")
      void bomNotFound() throws Exception {
        // given
        params.add("bomId", Long.toString(bomId));

        // when
        ResultActions actions = deleteWithParams(uri, params);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BOM_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.BOM_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("bomId에 마이너스 값을 넣으면 400 Bad Request를 반환한다.")
      void bomIdMinus() throws Exception {
        // given
        params.add("bomId", Long.toString(-1));

        // when
        ResultActions actions = deleteWithParams(uri, params);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 403 Forbidden을 반환한다.")
      void forbidden() throws Exception {
        // given
        params.add("bomId", Long.toString(bomId));

        // when
        ResultActions actions = deleteWithParams(uri, params);

        // then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }
    }
  }
}
