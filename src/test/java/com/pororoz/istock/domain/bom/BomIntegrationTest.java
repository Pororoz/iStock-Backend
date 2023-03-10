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

  @Nested
  @DisplayName("GET /api/v1/bom - BOM ??? ?????? API")
  class FindBom {

    MultiValueMap<String, String> params;

    @BeforeEach
    void setup() {
      params = new LinkedMultiValueMap<>();
    }

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      List<Part> partList;

      List<Product> productList;

      List<Category> categoryList;

      List<Bom> bomList;

      @BeforeEach
      void setup() {
        long number = 1;
        partList = new ArrayList<>();
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
        bomList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
          Part part = Part.builder()
              .partName(Integer.toString(i + 1))
              .spec(Integer.toString(i + 1))
              .stock(number)
              .price(number)
              .build();
          partList.add(part);
        }
        partRepository.saveAll(partList);

        for (int i = 0; i < 2; i++) {
          Category category = categoryRepository.save(
              Category.builder().categoryName("????????????" + (i + 1)).build());
          Product product = Product.builder()
              .productName(Integer.toString(i + 1))
              .productNumber(Integer.toString(i + 1))
              .codeNumber(Integer.toString(i + 1))
              .category(category)
              .companyName(Integer.toString(i + 1))
              .stock(number)
              .build();
          categoryList.add(category);
          productList.add(product);
        }
        productRepository.saveAll(productList);

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
        //bom3
        bomList.add(Bom.builder()
            .codeNumber("2")
            .locationNumber("5")
            .quantity(number)
            .product(productList.get(0))
            .part(partList.get(2))
            .build());
        // bom4
        bomList.add(Bom.builder()
            .codeNumber("3")
            .locationNumber("7")
            .quantity(number)
            .product(productList.get(1))
            .part(partList.get(2))
            .build());
        // bom5
        bomList.add(Bom.builder()
            .codeNumber("4")
            .locationNumber("3")
            .quantity(number)
            .product(productList.get(1))
            .part(partList.get(3))
            .build());
        bomRepository.saveAll(bomList);
      }


      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("productId??? ?????? ???????????? ?????? productId??? ????????? ?????? Bom ????????? ???????????????.")
      void findBom() throws Exception {
        // given
        int page = 0;
        int size = 2;
        params.add("page", "0");
        params.add("size", "2");
        params.add("product-id", "1");

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
              .part(PartResponse.of(bom.getPart()))
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
      @DisplayName("page??? size?????? productId??? ?????? ???????????? default????????? ?????? productId??? ????????? ?????? Bom ????????? ???????????????.")
      void findBomWithoutPageAndSize() throws Exception {
        // given
        int default_page = 0;
        int default_size = 20;
        params.add("product-id", "1");

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
              .part(PartResponse.of(bom.getPart()))
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("???????????? ?????? productId??? ?????? ???????????? 404 Not Found ????????? ????????????.")
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
      @DisplayName("???????????? ?????? ???????????? forbidden??? ????????????.")
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
  @DisplayName("POST /api/v1/bom - BOM ??? ?????? API")
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
    @DisplayName("?????? ?????????")
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
      @DisplayName("?????? ?????? ??????????????? ????????? 200 OK??? ????????? Bom Data??? ????????????.")
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
      @DisplayName("Sub assy BOM??? ???????????? ????????? Bom Data??? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @WithMockUser
      @DisplayName("???????????? ?????? partId??? ???????????? 404 Not Found??? ????????????.")
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
      @DisplayName("???????????? ?????? productId??? ???????????? 404 Not Found??? ????????????.")
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
      @DisplayName("?????? ???????????? ????????? BOM??? ???????????? 404 Bad Request??? ????????????.")
      void duplicateBom() throws Exception {
        // given
        String nothing = "1";
        Part part = savePart("name", "spec");
        Category category = saveCategory("????????????");
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
      @DisplayName("???????????? ?????? ???????????? ???????????? 403 Forbidden??? ????????????.")
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
      @DisplayName("Sub assy??? Sub assy??? BOM?????? ?????????????????? Bad Request??? ????????????.")
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
  @DisplayName("DELETE /api/v1/bom - BOM ??? ?????? API")
  class deleteBom {

    String uri = "http://localhost:8080/v1/bom/";

    @Nested
    @DisplayName("?????? ?????????")
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
      @DisplayName("???????????? BOM??? ???????????? ?????? BOM ????????? ?????? 200 OK??? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @WithMockUser(roles = "ADMIN")
      @DisplayName("???????????? ?????? BOM ?????? ?????? ???????????? 404 Not Found??? ????????????.")
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
      @DisplayName("bomId??? ???????????? ?????? ????????? 400 Bad Request??? ????????????.")
      void bomIdMinus() throws Exception {
        // given
        // when
        ResultActions actions = getResultActions(uri + "-1", HttpMethod.DELETE);

        // then
        actions.andExpect(status().isBadRequest())
            .andDo(print());
      }

      @Test
      @DisplayName("???????????? ?????? ???????????? ???????????? 403 Forbidden??? ????????????.")
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
  @DisplayName("PUT /api/v1/bom - ?????? BOM ??? ?????? API")
  class UpdateBom {

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

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
      @DisplayName("?????? ?????? ??????????????? ????????? 200 OK??? ????????? Bom Data??? ????????????.")
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
      @DisplayName("locationNumber, partId, productId??? ???????????? ?????? ?????? ???????????? BOM ?????? ????????????, 200 OK??? ????????? Bom Data??? ????????????.")
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
      @DisplayName("?????? ?????? ???????????? ?????? ????????? ?????? ??????, 200 OK??? ????????? Bom Data??? ????????????.")
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

      @Test
      @WithMockUser
      @DisplayName("BOM??? sub assy BOM?????? ????????????.")
      void updateToSubAssyBom() throws Exception {
        // given
        Product subAssy = saveSubAssy("sub assy number", "sub assy name", category);

        UpdateBomRequest request = UpdateBomRequest.builder()
            .bomId(bomId)
            .locationNumber(newLocationNumber)
            .subAssyId(subAssy.getId())
            .codeNumber("11")
            .quantity(newQuantity)
            .memo(newMemo)
            .productId(productId)
            .build();
        BomResponse response = BomResponse.builder()
            .bomId(bomId)
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
      @DisplayName("Code number??? 11(sub assy)?????? new code number??? ???????????? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @WithMockUser
      @DisplayName("???????????? ?????? BOM??? ID??? ???????????? 404 Not Found??? ????????????.")
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
      @DisplayName("???????????? ?????? partId??? ???????????? 404 Not Found??? ????????????.")
      void partNotFound() throws Exception {
        // given
        String nothing1 = "1";
        Category category = saveCategory("????????????");
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
      @DisplayName("???????????? ?????? productId??? ???????????? 404 Not Found??? ????????????.")
      void productNotFound() throws Exception {
        // given
        String nothing1 = "1";
        String nothing2 = "2";
        Category category = saveCategory("????????????");
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
      @DisplayName("locationNumber, partId, productId ??? ???????????? ???????????? ??????, ?????? ???????????? ????????? BOM?????? ??????????????? ?????? 404 Bad Request??? ????????????.")
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
        Category category = saveCategory("????????????");
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
      @DisplayName("???????????? ?????? ???????????? ???????????? 403 Forbidden??? ????????????.")
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
      @DisplayName("Sub assy??? BOM??? part?????? sub assy??? ???????????? Bad Request??? ????????????.")
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
      @DisplayName("?????? ?????? BOM?????? ?????? ???????????? subAssyId??? ????????? ??? ??????")
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
