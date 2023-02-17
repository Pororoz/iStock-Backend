package com.pororoz.istock.domain.product;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
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
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.dto.request.SaveProductRequest;
import com.pororoz.istock.domain.product.dto.request.UpdateProductRequest;
import com.pororoz.istock.domain.product.dto.response.FindProductResponse;
import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.dto.response.SubAssyResponse;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

public class ProductIntegrationTest extends IntegrationTest {

  @Autowired
  BomRepository bomRepository;

  @Autowired
  PartRepository partRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CategoryRepository categoryRepository;

  final String uri = "http://localhost:8080/v1/products";
  final String name = "product name";
  final String number = "product number";
  final String codeNumber = "code number";
  final long stock = 10;
  final String companyName = "company name";

  @Nested
  @DisplayName("POST /v1/products - 제품 생성")
  class SaveProduct {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("제품을 저장한다.")
    void saveProductAdmin() throws Exception {
      //given
      databaseCleanup.execute();
      Category category = categoryRepository.save(Category.builder().categoryName("카테고리").build());
      SaveProductRequest request = SaveProductRequest.builder()
          .productName(name).productNumber(number)
          .codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(category.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PRODUCT))
          .andExpect(jsonPath("$.data.productName").value(name))
          .andExpect(jsonPath("$.data.productNumber").value(number))
          .andExpect(jsonPath("$.data.codeNumber").value(codeNumber))
          .andExpect(jsonPath("$.data.stock").value(stock))
          .andExpect(jsonPath("$.data.companyName").value(companyName))
          .andExpect(jsonPath("$.data.categoryId").value(category.getId())).andDo(print());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("user role로 제품을 저장한다.")
    void saveProductUser() throws Exception {
      //given
      databaseCleanup.execute();
      Category category = categoryRepository.save(Category.builder().categoryName("카테고리").build());
      SaveProductRequest request = SaveProductRequest.builder()
          .productName(name).productNumber(number)
          .categoryId(category.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PRODUCT))
          .andExpect(jsonPath("$.data.productName").value(name))
          .andExpect(jsonPath("$.data.productNumber").value(number))
          .andExpect(jsonPath("$.data.codeNumber").value(nullValue()))
          .andExpect(jsonPath("$.data.stock").value(0))
          .andExpect(jsonPath("$.data.companyName").value(nullValue()))
          .andExpect(jsonPath("$.data.categoryId").value(category.getId()))
          .andDo(print());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 유저는 POST api에 접근할 수 없다.")
    void cannotAccessAnonymous() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder().build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isForbidden());
    }

  }

  @Nested
  @DisplayName("PUT /v1/products - 제품 수정")
  class UpdateProduct {

    long newStock = stock + 2;
    String newName = "new product name";
    String newNumber = "new product number";
    String newCodeNumber = "new code number";
    String newCompanyName = "new company name";
    Product product;
    Category category1, category2;

    @BeforeEach
    void setUp() {
      databaseCleanup.execute();
      category1 = categoryRepository.save(Category.builder().categoryName("카테고리1").build());
      category2 = categoryRepository.save(Category.builder().categoryName("카테고리2").build());
      product = productRepository.save(
          Product.builder().productName(name)
              .productNumber(number).codeNumber(codeNumber)
              .companyName(companyName).stock(stock)
              .category(category1)
              .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("제품을 수정한다.")
    void saveProduct() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response)))).andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("stock이 null이면 0으로 수정된다.")
    void defaultStockZeroProduct() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .companyName(newCompanyName).categoryId(category2.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(product.getId())
          .productName(newName).productNumber(newNumber)
          .codeNumber(newCodeNumber).stock(0)
          .companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response)))).andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("완성품을 sub assy로 수정한다.")
    void changeToSubAssy() throws Exception {
      //given
      Part part = partRepository.save(Part.builder().partName("name").spec("spec").build());
      bomRepository.save(Bom.builder()
          .locationNumber("1").product(product).part(part).build());
      bomRepository.save(Bom.builder()
          .locationNumber("2").product(product).part(part).build());
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response)))).andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("Sub assy를 완제품으로 수정한다.")
    void changeToProduct() throws Exception {
      //given
      product = productRepository.save(
          Product.builder().productName(name)
              .productNumber("sub assy").codeNumber("11")
              .companyName(companyName).stock(stock)
              .category(category1)
              .build());
      Part part = partRepository.save(Part.builder().partName("name").spec("spec").build());
      bomRepository.save(Bom.builder()
          .locationNumber("1").product(product).part(part).build());
      bomRepository.save(Bom.builder()
          .locationNumber("2").product(product).part(part).build());
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      actions.andExpect(status().isOk()).andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response)))).andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("완성품이 sub assy를 BOM으로 가지고 있다면, sub assy로 수정할 시 Bad Request가 발생한다.")
    void changeToSubAssyException() throws Exception {
      //given
      Product subAssy = productRepository.save(
          Product.builder().productName(name)
              .productNumber("sub assy").codeNumber("11")
              .companyName(companyName).stock(stock)
              .category(category1)
              .build());
      Part part = partRepository.save(Part.builder().partName("name").spec("spec").build());
      bomRepository.save(Bom.builder()
          .productNumber(subAssy.getProductNumber())
          .codeNumber("11").locationNumber("1")
          .product(product)
          .build());
      bomRepository.save(Bom.builder()
          .locationNumber("2").product(product).part(part)
          .build());
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(product.getId()).productName(newName)
          .productNumber(newNumber).codeNumber("11")
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.SUB_ASSY_BOM_EXIST))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.SUB_ASSY_BOM_EXIST))
          .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("Sub assy를 완성품으로 수정할 때, 해당 제품이 다른 제품의 BOM으로 등록되어 있다면 Bad Request가 발생한다.")
    void changeToProductException() throws Exception {
      //given
      Product subAssy = productRepository.save(
          Product.builder().productName(name)
              .productNumber("sub assy").codeNumber("11")
              .companyName(companyName).stock(stock)
              .category(category1)
              .build());
      Part part = partRepository.save(Part.builder().partName("name").spec("spec").build());
      bomRepository.save(Bom.builder()
          .locationNumber("1").product(subAssy).part(part)
          .build());
      bomRepository.save(Bom.builder()
          .locationNumber("2").product(subAssy).part(part)
          .build());
      bomRepository.save(Bom.builder()
          .productNumber(subAssy.getProductNumber())
          .codeNumber("11").locationNumber("1")
          .product(product)
          .build());
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(subAssy.getId()).productName(newName)
          .productNumber(newNumber).codeNumber(newCodeNumber)
          .stock(newStock).companyName(newCompanyName)
          .categoryId(category2.getId())
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.REGISTERED_AS_SUB_ASSY))
          .andExpect(jsonPath("$.message").value(ExceptionMessage.REGISTERED_AS_SUB_ASSY))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("DELETE /v1/products/{productId} - 제품 삭제")
  class deleteProduct {

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("제품을 정상적으로 삭제한다.")
    void saveProduct() throws Exception {
      //given
      databaseCleanup.execute();
      Category category = categoryRepository.save(
          Category.builder().categoryName("category").build());
      Product product = productRepository.save(Product.builder()
          .productNumber(number).productName(name)
          .stock(stock).companyName(companyName)
          .category(category)
          .build());

      //when
      ResultActions actions = getResultActions(uri + "/" + product.getId().intValue(),
          HttpMethod.DELETE);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(product.getId()).productNumber(number)
          .productName(name).companyName(companyName)
          .stock(stock).categoryId(category.getId())
          .build();
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("GET /v1/products?category-id={categoryId}&page={page}&size={size} - 제품 조회")
  class FindProducts {

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("제품을 subAssy와 함께 페이지네이션하여 1페이지를 조회한다.")
    void findProductWithSubAssy() throws Exception {
      //given
      databaseCleanup.execute();
      Category category = categoryRepository.save(Category.builder().categoryName("카테고리").build());

      //product 저장
      List<Product> products = new ArrayList<>();
      for (int i = 0; i < 11; i++) {
        Product product = productRepository.save(Product.builder()
            .productName("p" + i).productNumber("p" + i)
            .category(category)
            .build());
        products.add(product);
      }

      //part 저장
      Part part = partRepository.save(Part.builder()
          .partName("p").spec("p")
          .build());

      //bom 저장
      bomRepository.save(Bom.builder()
          .codeNumber("11")
          .part(part).product(products.get(5))
          .quantity(10)
          .build());
      for (int i = 0; i < 7; i++) {
        bomRepository.save(Bom.builder()
            .codeNumber("11").locationNumber("" + i)
            .part(part).product(products.get(i))
            .quantity(10)
            .build());
      }
      for (int i = 0; i < 10; i++) {
        bomRepository.save(Bom.builder()
            .codeNumber("10").locationNumber("" + i + 100)
            .part(part).product(products.get(i))
            .build());
      }

      int page = 1;
      int size = 5;
      String fullUri = uri + "?category-id=" + category.getId() + "&page=" + page + "&size=" + size;

      //when
      ResultActions actions = getResultActions(fullUri, HttpMethod.GET);

      //then
      SubAssyResponse subAssyService = SubAssyResponse.builder()
          .productId(part.getId())
          .productName("p").productNumber("p")
          .quantity(10)
          .build();
      List<FindProductResponse> findProductResponses = new ArrayList<>();
      for (int i = 5; i < 10; i++) {
        Product product = products.get(i);
        findProductResponses.add(FindProductResponse.builder()
            .productId(product.getId())
            .productName(product.getProductName())
            .productNumber(product.getProductNumber())
            .stock(product.getStock())
            .companyName(product.getCompanyName())
            .categoryId(product.getCategory().getId())
            .createdAt(TimeEntity.formatTime(product.getCreatedAt()))
            .updatedAt(TimeEntity.formatTime(product.getUpdatedAt()))
            .subAssy(i > 6 ? List.of()
                : (i == 6 ? List.of(subAssyService) : List.of(subAssyService, subAssyService)))
            .build());
      }
      PageResponse<FindProductResponse> response = new PageResponse<>(
          new PageImpl<>(findProductResponses, PageRequest.of(page, size), 11));
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 forbidden이 발생한다.")
    void forbiddenUser() throws Exception {
      // given
      String fullUri = uri + "?category-id=1&page=10&size=10";

      //when
      ResultActions actions = getResultActions(fullUri, HttpMethod.GET);

      //then
      actions.andExpect(status().isForbidden());
    }
  }
}
