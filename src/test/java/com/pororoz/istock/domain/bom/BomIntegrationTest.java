package com.pororoz.istock.domain.bom;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.dto.request.SaveBomRequest;
import com.pororoz.istock.domain.bom.dto.request.UpdateBomRequest;
import com.pororoz.istock.domain.bom.dto.response.BomResponse;
import com.pororoz.istock.domain.bom.dto.response.FindBomResponse;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.part.dto.response.PartResponse;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
  String subAssyCodeNumber = "11";

  Part savePart(String partName, String spec) {
    return partRepository.save(Part.builder()
        .partName(partName)
        .spec(spec)
        .stock(10)
        .price(10).build());
  }

  Product saveProduct(String productNumber, String productName, Category category) {
    return productRepository.save(Product.builder()
        .productNumber(productNumber)
        .productName(productName)
        .category(category).build());
  }

  Category saveCategory(String categoryName) {
    return categoryRepository.save(Category.builder()
        .categoryName(categoryName).build());
  }

  Product saveSubAssy(String productNumber, String productName, Category category) {
    return productRepository.save(Product.builder()
        .codeNumber(subAssyCodeNumber).productNumber(productNumber)
        .productName(productName)
        .category(category).build());
  }


  PartResponse partResponseOf(Part part) {
    return PartResponse.builder()
        .partId(part.getId())
        .partName(part.getPartName())
        .price(part.getPrice())
        .spec(part.getSpec())
        .stock(part.getStock())
        .build();
  }

  ProductResponse productResponseOf(Product product) {
    return ProductResponse.builder()
        .productId(product.getId())
        .productName(product.getProductName())
        .productNumber(product.getProductNumber())
        .codeNumber(product.getCodeNumber())
        .stock(product.getStock())
        .companyName(product.getCompanyName())
        .categoryId(product.getCategory().getId())
        .build();
  }

  @Nested
  @DisplayName("GET /api/v1/bom - BOM 행 조회 API")
  class FindBom {

    MultiValueMap<String, String> params;

    @BeforeEach
    void setup() {
      params = new LinkedMultiValueMap<>();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      List<Bom> bomList;
      List<Part> partList;
      List<Product> productList;

      @BeforeEach
      void setup() {
        long number = 1;
        bomList = new ArrayList<>();
        partList = new ArrayList<>();
        productList = new ArrayList<>();

        Category category = saveCategory("카테고리");
        for (int i = 0; i < 2; i++) {
          Part part = savePart(Integer.toString(i + 1), Integer.toString(i + 1));
          partList.add(part);
        }
        Product subAssy = saveSubAssy("number", "name", category);

        for (int i = 0; i < 2; i++) {
          Product product = saveProduct(Integer.toString(i + 1), Integer.toString(i + 1), category);
          productList.add(product);
        }

        // bom1
        bomList.add(Bom.builder()
            .codeNumber("1")
            .locationNumber("2")
            .quantity(number)
            .product(productList.get(0))
            .part(partList.get(0))
            .build());
        // bom2
        bomList.add(Bom.builder()
            .codeNumber("2")
            .locationNumber("2")
            .quantity(number)
            .product(productList.get(0))
            .part(partList.get(1))
            .build());
        //sub assy bom3
        bomList.add(Bom.builder()
            .codeNumber(subAssyCodeNumber)
            .locationNumber("5")
            .quantity(number)
            .product(productList.get(0))
            .subAssy(subAssy)
            .build());
        bomRepository.saveAll(bomList);
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("productId를 통해 검색하면 해당 productId에 연관된 모든 Bom 정보를 제공해준다.")
      void findBom() throws Exception {
        // given
        int page = 0;
        int size = 2;
        params.add("page", "0");
        params.add("size", "2");
        params.add("product-id", String.valueOf(productList.get(0).getId()));

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.GET, params);

        // then
        List<FindBomResponse> findBomResponseArrayList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
          Bom bom = bomList.get(i);
          findBomResponseArrayList.add(FindBomResponse.builder()
              .bomId(bom.getId())
              .locationNumber(bom.getLocationNumber())
              .codeNumber(bom.getCodeNumber())
              .quantity(bom.getQuantity())
              .memo(bom.getMemo())
              .part(partResponseOf(bom.getPart()))
              .createdAt(TimeEntity.formatTime(bom.getCreatedAt()))
              .updatedAt(TimeEntity.formatTime(bom.getUpdatedAt()))
              .productId(bom.getProduct().getId())
              .build());
        }
        PageResponse<FindBomResponse> response = new PageResponse<>(
            new PageImpl<>(findBomResponseArrayList, PageRequest.of(page, size), 3));
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_BOM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("page와 size없이 productId를 통해 검색하면 default값으로 해당 productId에 연관된 모든 Bom 정보를 제공해준다.")
      void findBomWithoutPageAndSize() throws Exception {
        // given
        int default_page = 0;
        int default_size = 20;
        params.add("product-id", String.valueOf(productList.get(0).getId()));

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.GET, params);

        // then
        List<FindBomResponse> findBomResponseArrayList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
          Bom bom = bomList.get(i);
          findBomResponseArrayList.add(FindBomResponse.builder()
              .bomId(bom.getId())
              .locationNumber(bom.getLocationNumber())
              .codeNumber(bom.getCodeNumber())
              .quantity(bom.getQuantity())
              .memo(bom.getMemo())
              .part(i < 2 ? partResponseOf(bom.getPart()) : null)
              .subAssy(i >= 2 ? productResponseOf(bom.getSubAssy()) : null)
              .createdAt(TimeEntity.formatTime(bom.getCreatedAt()))
              .updatedAt(TimeEntity.formatTime(bom.getUpdatedAt()))
              .productId(bom.getProduct().getId())
              .build());
        }
        PageResponse<FindBomResponse> response = new PageResponse<>(
            new PageImpl<>(findBomResponseArrayList, PageRequest.of(default_page, default_size),
                3));
        actions.andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
            .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_BOM))
            .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
            .andDo(print());
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("존재하지 않는 productId를 통해 검색하면 404 Not Found 에러가 발생한다.")
      void productIdNotFound() throws Exception {
        // given
        params.add("page", "0");
        params.add("size", "2");
        params.add("product-id", "1");

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.GET, params);

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.PRODUCT_NOT_FOUND))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.PRODUCT_NOT_FOUND))
            .andDo(print());
      }

      @Test
      @DisplayName("인증되지 않은 사용자는 forbidden이 발생한다.")
      void forbiddenUser() throws Exception {
        // given
        params.add("page", "0");
        params.add("size", "2");
        params.add("product-id", "1");

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.GET, params);

        // then
        actions.andExpect(status().isForbidden());
      }
    }
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
        part = savePart("part name", "spec");
        category = saveCategory("category name");
        product = saveProduct("product number", "product name", category);
      }

      @Test
      @WithMockUser
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

      @Test
      @WithMockUser
      @DisplayName("Sub assy BOM을 저장하고 저장한 Bom Data를 반환한다.")
      void saveSubAssyBom() throws Exception {
        // given
        Product subAssy = saveSubAssy("sub number", "sub name", category);
        SaveBomRequest request = SaveBomRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .subAssyId(subAssy.getId())
            .memo(memo)
            .productId(product.getId())
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bomId)
            .locationNumber(locationNumber)
            .codeNumber(subAssyCodeNumber)
            .quantity(quantity)
            .subAssyId(subAssy.getId())
            .memo(memo)
            .productId(product.getId())
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
      @WithMockUser
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
      @WithMockUser
      @DisplayName("존재하지 않는 productId를 입력하면 404 Not Found를 반환한다.")
      void productNotFound() throws Exception {
        // given
        savePart("name", "spec");

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
      @WithMockUser
      @DisplayName("이미 존재하는 조합의 BOM을 저장하면 404 Bad Request를 반환한다.")
      void duplicateBom() throws Exception {
        // given
        String nothing = "1";
        Part part = savePart("name", "spec");
        Category category = saveCategory("카테고리");
        Product product = saveProduct(nothing, nothing, category);
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

      @Test
      @WithMockUser
      @DisplayName("Sub assy에 Sub assy를 BOM으로 저장하려하면 Bad Request가 발생한다.")
      void subAssyCannotHaveSubAssy() throws Exception {
        // given
        Category category = saveCategory("c");
        Product superSubAssy = saveSubAssy("super number", "super name", category);
        Product subAssy = saveSubAssy("sub assy number", "sub assy name", category);
        SaveBomRequest request = SaveBomRequest.builder()
            .locationNumber(locationNumber)
            .codeNumber("11")
            .quantity(quantity)
            .subAssyId(subAssy.getId())
            .memo(memo)
            .productId(superSubAssy.getId())
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.SUB_ASSY_CANNOT_HAVE_SUB_ASSY))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.SUB_ASSY_CANNOT_HAVE_SUB_ASSY))
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/bom - BOM 행 제거 API")
  class deleteBom {

    String uri = "http://localhost:8080/v1/bom/";

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      Part part;
      Product product;
      Category category;
      Bom bom;

      @BeforeEach
      void setup() {
        part = savePart("part name", "spec");
        category = saveCategory("category name");
        product = saveProduct("product number", "product name", category);
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
            .bomId(bom.getId())
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(part.getId())
            .productId(product.getId())
            .build();

        // when
        ResultActions actions = getResultActions(uri + bomId, HttpMethod.DELETE);

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
        // when
        ResultActions actions = getResultActions(uri + bomId, HttpMethod.DELETE);

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
        // when
        ResultActions actions = getResultActions(uri + "-1", HttpMethod.DELETE);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("인증되지 않은 사용자가 접근하면 403 Forbidden을 반환한다.")
      void forbidden() throws Exception {
        // given
        // when
        ResultActions actions = getResultActions(uri + bomId, HttpMethod.DELETE);

        // then
        actions.andExpect(status().isForbidden())
            .andDo(print());
      }
    }
  }

  @Nested
  @DisplayName("PUT /api/v1/bom - 제품 BOM 행 수정 API")
  class UpdateBom {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      Bom bom;
      Part part;
      Product product;
      Category category;

      @BeforeEach
      void setup() {
        category = saveCategory("category name");
        savePart("name1", "spec1");
        savePart("name2", "spec2");
        product = saveProduct("number1", "name1", category);
        saveProduct("number2", "name2", category);
        part = savePart("name", "spec");
        bom = bomRepository.save(Bom.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .part(part)
            .product(product)
            .build());
      }

      @Test
      @WithMockUser(roles = "USER")
      @DisplayName("모든 값을 정상적으로 넣으면 200 OK와 저장한 Bom Data를 반환한다.")
      void updateBom() throws Exception {
        // given
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bom.getId())
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bom.getId())
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
            .bomId(bom.getId())
            .locationNumber(locationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(partId)
            .productId(productId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bom.getId())
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
            .bomId(bom.getId())
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .memo(memo)
            .partId(partId)
            .productId(productId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bom.getId())
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

      @Test
      @WithMockUser
      @DisplayName("BOM을 sub assy BOM으로 변경한다.")
      void updateToSubAssyBom() throws Exception {
        // given
        Product subAssy = saveSubAssy("sub assy number", "sub assy name", category);

        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bom.getId())
            .locationNumber(newLocationNumber)
            .subAssyId(subAssy.getId())
            .codeNumber("11")
            .quantity(newQuantity)
            .memo(newMemo)
            .productId(productId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bom.getId())
            .locationNumber(newLocationNumber)
            .subAssyId(subAssy.getId())
            .codeNumber("11")
            .quantity(newQuantity)
            .memo(newMemo)
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
      @WithMockUser
      @DisplayName("Code number를 11(sub assy)에서 new code number로 정상적을 변경한다.")
      void updateToCodeNumber0() throws Exception {
        // given
        Product subAssy = saveSubAssy("sub assy number", "sub assy name", category);
        Bom subAssyBom = bomRepository.save(Bom.builder()
            .codeNumber("11").subAssy(subAssy)
            .product(product)
            .build());
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(subAssyBom.getId())
            .locationNumber(newLocationNumber)
            .codeNumber(newCodeNumber)
            .quantity(newQuantity)
            .memo(newMemo)
            .partId(newPartId)
            .productId(newProductId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(subAssyBom.getId())
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
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @WithMockUser
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
      @WithMockUser
      @DisplayName("존재하지 않는 partId를 입력하면 404 Not Found를 반환한다.")
      void partNotFound() throws Exception {
        // given
        String nothing1 = "1";
        Category category = saveCategory("카테고리");
        Part part = partRepository.save(Part.builder()
            .id(partId)
            .partName(nothing1)
            .spec(nothing1)
            .build());
        Product product = saveProduct(nothing1, nothing1, category);
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
      @WithMockUser
      @DisplayName("존재하지 않는 productId를 입력하면 404 Not Found를 반환한다.")
      void productNotFound() throws Exception {
        // given
        String nothing1 = "1";
        String nothing2 = "2";
        Category category = saveCategory("카테고리");
        Part part = savePart(nothing1, nothing1);
        savePart(nothing2, nothing2);
        Product productFixture1 = Product.builder()
            .id(productId)
            .productName(nothing1)
            .productNumber(nothing1)
            .codeNumber(nothing1)
            .category(category)
            .companyName(nothing1)
            .build();
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
      @WithMockUser
      @DisplayName("locationNumber, partId, productId 중 하나라도 변경하는 경우, 이미 존재하는 조합의 BOM으로 수정하려고 하면 404 Bad Request를 반환한다.")
      void duplicateBom() throws Exception {
        // given
        String nothing1 = "1";
        String nothing2 = "2";
        Part part1 = partRepository.save(Part.builder()
            .id(1L)
            .partName(nothing1)
            .spec(nothing1)
            .build());
        Part part2 = partRepository.save(Part.builder()
            .id(2L)
            .partName(nothing2)
            .spec(nothing2)
            .build());
        Category category = saveCategory("카테고리");
        Product product1 = saveProduct(nothing1, nothing1, category);
        Product product2 = saveProduct(nothing2, nothing2, category);
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

      @Test
      @WithMockUser
      @DisplayName("Sub assy의 BOM을 part에서 sub assy로 수정하면 Bad Request가 발생한다.")
      void subAssyCannotHaveSubAssy() throws Exception {
        // given
        Category category = saveCategory("category");
        Product superSubAssy = saveSubAssy("supser number", "super sub name", category);
        Product subAssy = saveSubAssy("sub assy number", "sub assy name", category);
        Part part = savePart("name", "spec");
        Bom bom = bomRepository.save(Bom.builder()
            .locationNumber(locationNumber)
            .codeNumber(codeNumber)
            .quantity(quantity)
            .part(part)
            .product(superSubAssy)
            .build());
        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bom.getId())
            .locationNumber(locationNumber)
            .codeNumber("11")
            .quantity(quantity)
            .subAssyId(subAssy.getId())
            .memo(memo)
            .productId(superSubAssy.getId())
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.SUB_ASSY_CANNOT_HAVE_SUB_ASSY))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.SUB_ASSY_CANNOT_HAVE_SUB_ASSY))
            .andDo(print());
      }

      @Test
      @WithMockUser
      @DisplayName("제품 내의 BOM에서 이미 존재하는 subAssyId로 수정할 수 없다")
      void cannotUpdateExistProductNumber() throws Exception {
        // given
        Category category = saveCategory("category");
        Product product = saveProduct("number", "name", category);
        Product subAssy1 = saveSubAssy("sub assy number1", "sub assy name1", category);
        Product subAssy2 = saveSubAssy("sub assy number2", "sub assy name2", category);
        Bom bom1 = bomRepository.save(Bom.builder()
            .locationNumber(locationNumber)
            .codeNumber("11")
            .quantity(quantity)
            .subAssy(subAssy1)
            .product(product)
            .build());
        bomRepository.save(Bom.builder()
            .locationNumber(newLocationNumber)
            .codeNumber("11")
            .quantity(quantity)
            .subAssy(subAssy2)
            .product(product)
            .build());

        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bom1.getId())
            .locationNumber(locationNumber)
            .codeNumber("11")
            .quantity(quantity)
            .subAssyId(subAssy2.getId())
            .productId(product.getId())
            .build();

        // when
        ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(ExceptionStatus.BOM_SUB_ASSY_DUPLICATED))
            .andExpect(jsonPath("$.message").value(ExceptionMessage.BOM_SUB_ASSY_DUPLICATED))
            .andDo(print());
      }
    }
  }
}
