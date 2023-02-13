package com.pororoz.istock.domain.bom;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.dto.request.SaveBomRequest;
import com.pororoz.istock.domain.bom.dto.request.UpdateBomRequest;
import com.pororoz.istock.domain.bom.dto.response.BomResponse;
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
    long quantity = 3;
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
        long number = 1;
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
      @WithMockUser(roles = "USER")
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
        BomResponse response = BomResponse.builder()
            .bomId(bomId)
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
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 partId를 입력하면 404 Not Found를 반환한다.")
      void partNotFound() throws Exception {
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
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PART_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PART_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 productId를 입력하면 404 Not Found를 반환한다.")
      void productNotFound() throws Exception {
        // given
        String nothing = "1";
        long number = 1;
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
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PRODUCT_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PRODUCT_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("이미 존재하는 조합의 BOM을 저장하면 404 Bad Request를 반환한다.")
      void duplicateBom() throws Exception {
        // given
        String nothing = "1";
        long number = 1;
        Part part = partRepository.save(Part.builder()
            .partName(nothing)
            .spec(nothing)
            .stock(number)
            .price(number)
            .build());
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        Product product = productRepository.save(Product.builder()
            .productName(nothing)
            .productNumber(nothing)
            .codeNumber(nothing)
            .category(category)
            .companyName(nothing)
            .stock(number)
            .build());
        Bom bom = Bom.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .part(part)
            .product(product)
            .build();
        bomRepository.save(bom);

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
            .andExpect(jsonPath("$.status").value(ExceptionStatus.DUPLICATE_BOM))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.DUPLICATE_BOM))
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
        BomResponse response = BomResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        params.add("bomId", Long.toString(bomId));

        // when
        ResultActions actions = deleteWithParams(uri, params);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_BOM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 BOM 값을 삭제 시도하면 404 Not Found를 반환한다.")
      void bomNotFound() throws Exception {
        // given
        params.add("bomId", Long.toString(bomId));

        // when
        ResultActions actions = deleteWithParams(uri, params);

        // then
        actions.andExpect(status().isNotFound())
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

  @Nested
  @DisplayName("PUT /api/v1/bom - 제품 BOM 행 수정 API")
  class UpdateBom {

    Long bomId = 1L;
    String locationNumber = "L5.L4";
    String codeNumber = "";
    long quantity = 3;
    String memo = "";
    Long partId = 1L;
    Long productId = 1L;
    String newLocationNumber = "new location";
    String newCodeNumber = "new code";
    Long newQuantity = 5L;
    String newMemo = "new";
    Long newPartId = 2L;
    Long newProductId = 2L;
    String uri = "http://localhost:8080/v1/bom";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      Part part;
      Product product;
      Category category;

      @BeforeEach
      void setup() {
        String nothing1 = "1";
        String nothing2 = "2";
        long number1 = 1;
        long number2 = 2;
        category = categoryRepository.save(Category.builder().categoryName("카테고리").build());
        Part partFixture1 = Part.builder()
            .id(partId)
            .partName(nothing1)
            .spec(nothing1)
            .stock(number1)
            .price(number1)
            .build();
        Part partFixture2 = Part.builder()
            .id(newPartId)
            .partName(nothing2)
            .spec(nothing2)
            .stock(number2)
            .price(number2)
            .build();
        Product productFixture1 = Product.builder()
            .id(productId)
            .productName(nothing1)
            .productNumber(nothing1)
            .codeNumber(nothing1)
            .category(category)
            .companyName(nothing1)
            .stock(number1)
            .build();
        Product productFixture2 = Product.builder()
            .id(newProductId)
            .productName(nothing2)
            .productNumber(nothing2)
            .codeNumber(nothing2)
            .category(category)
            .companyName(nothing2)
            .stock(number2)
            .build();
        part = partRepository.save(partFixture1);
        partRepository.save(partFixture2);
        product = productRepository.save(productFixture1);
        productRepository.save(productFixture2);
        Bom bom = Bom.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .part(part)
            .product(product)
            .build();
        bomRepository.save(bom);
      }

      @Test
      @WithMockUser(roles = "USER")
      @DisplayName("모든 값을 정상적으로 넣으면 200 OK와 저장한 Bom Data를 반환한다.")
      void updateBom() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_BOM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "USER")
      @DisplayName("locationNumber, partId, productId를 변경하지 않을 경우 수정해도 BOM 값이 수정되고, 200 OK와 저장한 Bom Data를 반환한다.")
      void updateBomWithSameIndex() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(partId)
            .productId(productId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_BOM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "USER")
      @DisplayName("아무 값도 변경하지 않은 요청을 보낼 경우, 200 OK와 저장한 Bom Data를 반환한다.")
      void updateBomNotChange() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_BOM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 BOM의 ID를 입력하면 404 Not Found를 반환한다.")
      void bomNotFound() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BOM_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.BOM_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 partId를 입력하면 404 Not Found를 반환한다.")
      void partNotFound() throws Exception {
        // given
        String nothing1 = "1";
        long number1 = 1;
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        Part partFixture1 = Part.builder()
            .id(partId)
            .partName(nothing1)
            .spec(nothing1)
            .stock(number1)
            .price(number1)
            .build();
        Product productFixture1 = Product.builder()
            .id(productId)
            .productName(nothing1)
            .productNumber(nothing1)
            .codeNumber(nothing1)
            .category(category)
            .companyName(nothing1)
            .stock(number1)
            .build();
        Part part = partRepository.save(partFixture1);
        Product product = productRepository.save(productFixture1);
        Bom bom = Bom.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .part(part)
            .product(product)
            .build();
        bomRepository.save(bom);

        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PART_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PART_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 productId를 입력하면 404 Not Found를 반환한다.")
      void productNotFound() throws Exception {
        // given
        String nothing1 = "1";
        String nothing2 = "2";
        long number1 = 1;
        long number2 = 2;
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        Part partFixture1 = Part.builder()
            .id(partId)
            .partName(nothing1)
            .spec(nothing1)
            .stock(number1)
            .price(number1)
            .build();
        Part partFixture2 = Part.builder()
            .id(newPartId)
            .partName(nothing2)
            .spec(nothing2)
            .stock(number2)
            .price(number2)
            .build();
        Product productFixture1 = Product.builder()
            .id(productId)
            .productName(nothing1)
            .productNumber(nothing1)
            .codeNumber(nothing1)
            .category(category)
            .companyName(nothing1)
            .stock(number1)
            .build();
        Part part = partRepository.save(partFixture1);
        partRepository.save(partFixture2);
        Product product = productRepository.save(productFixture1);
        Bom bom = Bom.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .part(part)
            .product(product)
            .build();
        bomRepository.save(bom);

        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PRODUCT_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PRODUCT_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("locationNumber, partId, productId 중 하나라도 변경하는 경우, 이미 존재하는 조합의 BOM으로 수정하려고 하면 404 Bad Request를 반환한다.")
      void duplicateBom() throws Exception {
        // given
        String nothing1 = "1";
        long number1 = 1;
        String nothing2 = "2";
        long number2 = 2;
        Part part1 = partRepository.save(Part.builder()
            .id(1L)
            .partName(nothing1)
            .spec(nothing1)
            .stock(number1)
            .price(number1)
            .build());
        Part part2 = partRepository.save(Part.builder()
            .id(2L)
            .partName(nothing2)
            .spec(nothing2)
            .stock(number2)
            .price(number2)
            .build());
        Category category = categoryRepository.save(
            Category.builder().categoryName("카테고리").build());
        Product product1 = productRepository.save(Product.builder()
            .productName(nothing1)
            .productNumber(nothing1)
            .codeNumber(nothing1)
            .category(category)
            .companyName(nothing1)
            .stock(number1)
            .build());
        Product product2 = productRepository.save(Product.builder()
            .productName(nothing2)
            .productNumber(nothing2)
            .codeNumber(nothing2)
            .category(category)
            .companyName(nothing2)
            .stock(number2)
            .build());
        Bom bom1 = Bom.builder()
            .id(1L)
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .part(part1)
            .product(product1)
            .build();
        Bom bom2 = Bom.builder()
            .id(2L)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(quantity)
            .memo(memo)
            .part(part2)
            .product(product2)
            .build();
        bomRepository.save(bom1);
        bomRepository.save(bom2);

        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.DUPLICATE_BOM))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.DUPLICATE_BOM))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("API 요청 시, partId의 값으로 null을 전달하면 400 Bad Request를 반환한다.")
      void badRequestPartId() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(null)
            .productId(newProductId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("API 요청 시, productId의 값으로 null을 전달하면 400 Bad Request를 반환한다.")
      void badRequestProductId() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(null)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 403 Forbidden을 반환한다.")
      void forbidden() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }
    }
  }
}
