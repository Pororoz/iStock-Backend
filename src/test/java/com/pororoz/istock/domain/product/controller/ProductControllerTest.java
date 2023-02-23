package com.pororoz.istock.domain.product.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.ControllerTest;
import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.product.dto.request.SaveProductRequest;
import com.pororoz.istock.domain.product.dto.request.UpdateProductRequest;
import com.pororoz.istock.domain.product.dto.response.FindProductWithSubassyResponse;
import com.pororoz.istock.domain.product.dto.response.ProductResponse;
import com.pororoz.istock.domain.product.dto.response.SubAssyResponse;
import com.pororoz.istock.domain.product.dto.service.FindProductByPartServiceRequest;
import com.pororoz.istock.domain.product.dto.service.FindProductWithSubassyServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.SubAssyServiceResponse;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.product.service.ProductService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class})
class ProductControllerTest extends ControllerTest {

  @MockBean
  ProductService productService;

  String uri = "http://localhost:8080/v1/products";
  Long id = 1L;
  String name = "productName";
  String number = "productNumber";
  String codeNumber = "codeNumber";
  long stock = 1;
  String companyName = "companyName";
  Long categoryId = 1L;
  Category category = Category.builder().id(categoryId).build();

  @Nested
  @DisplayName("product 저장")
  class SaveProduct {

    @Test
    @DisplayName("product를 정상적으로 저장한다.")
    void saveProduct() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder()
          .productName(name).productNumber(number)
          .codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId)
          .build();
      ProductServiceResponse serviceResponse = ProductServiceResponse.builder()
          .productId(id).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();

      //when
      when(productService.saveProduct(any(SaveProductServiceRequest.class))).thenReturn(
          serviceResponse);
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(id).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(category.getId())
          .build();

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.SAVE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("productName은 null이면 예외가 발생한다.")
    void productNameNullException() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder()
          .productName(null).productNumber(number)
          .codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("productNumber는 null이면 예외가 발생한다.")
    void productNumberNullException() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder()
          .productNumber(null).productName(name)
          .codeNumber(codeNumber).stock(stock)
          .companyName(companyName).categoryId(categoryId)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("cagegoryId는 null이면 예외가 발생한다.")
    void categoryIdNullException() throws Exception {
      //given
      SaveProductRequest request = SaveProductRequest.builder()
          .categoryId(null).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("product 수정")
  class ProductUpdate {

    @Test
    @DisplayName("product를 수정한다.")
    void updateProduct() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();
      ProductServiceResponse serviceDto = ProductServiceResponse.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();

      //when
      when(productService.updateProduct(any(UpdateProductServiceRequest.class))).thenReturn(
          serviceDto);
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(category.getId())
          .build();

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.UPDATE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("id가 null이면 예외가 발생한다.")
    void idNullException() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(null).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }

    @Test
    @DisplayName("product name이 null이면 예외가 발생한다.")
    void productNameNullException() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(1L).productName(null)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(categoryId)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }

    @Test
    @DisplayName("categoryId가 null이면 예외가 발생한다.")
    void categoryIdNullException() throws Exception {
      //given
      UpdateProductRequest request = UpdateProductRequest.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(null)
          .build();

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.PUT, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.BAD_REQUEST))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("product 삭제")
  class DeleteProduct {

    @Test
    @DisplayName("product를 삭제한다.")
    void deleteProduct() throws Exception {
      //given
      ProductServiceResponse serviceDto = ProductServiceResponse.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(category.getId())
          .build();

      //when
      when(productService.deleteProduct(1L)).thenReturn(serviceDto);
      ResultActions actions = getResultActions(uri + "/1", HttpMethod.DELETE);

      //then
      ProductResponse response = ProductResponse.builder()
          .productId(1L).productName(name)
          .productNumber(number).codeNumber(codeNumber)
          .stock(stock).companyName(companyName)
          .categoryId(category.getId())
          .build();

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.DELETE_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("product id를 지정하지 않으면 not found가 발생한다.")
    void productIdNull() throws Exception {
      //given
      //when
      ResultActions actions = getResultActions(uri + "/", HttpMethod.DELETE);

      //then
      actions.andExpect(status().isNotFound())
          .andDo(print());
    }

    @Test
    @DisplayName("product id가 음수이면 bad request가 발생한다.")
    void productIdNegative() throws Exception {
      //given
      //when
      ResultActions actions = getResultActions(uri + "/-1", HttpMethod.DELETE);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("product와 subAssy 조회")
  class findProductsWithSubassys {

    String getUri(Long categoryId, Integer page, Integer size) {
      return uri + "?category-id=" + (categoryId == null ? "" : categoryId)
          + "&page=" + (page == null ? "" : page)
          + "&size=" + (size == null ? "" : size);
    }

    @Test
    @DisplayName("product를 조회한다.")
    void findProduct() throws Exception {
      //given
      int page = 1;
      int size = 3;
      String uri = getUri(categoryId, page, size);
      PageRequest pageRequest = PageRequest.of(page, size);
      ProductServiceResponse productServiceResponse = ProductServiceResponse.builder()
          .productId(id).categoryId(categoryId).codeNumber("10").build();
      SubAssyServiceResponse subAssyServiceResponse = SubAssyServiceResponse.builder()
          .productServiceResponse(ProductServiceResponse.builder()
              .productNumber("sub assy number")
              .productName("sub assy name")
              .productId(1L)
              .companyName("company")
              .stock(1).build())
          .quantity(1).build();
      FindProductWithSubassyServiceResponse ServiceDto = FindProductWithSubassyServiceResponse.builder()
          .productServiceResponse(productServiceResponse)
          .subAssyServiceResponses(List.of(subAssyServiceResponse))
          .build();
      Page<FindProductWithSubassyServiceResponse> dtoPage =
          new PageImpl<>(List.of(ServiceDto), pageRequest, 4);

      //when
      when(productService.findProductsWithSubAssys(eq(categoryId), any(Pageable.class)))
          .thenReturn(dtoPage);
      ResultActions actions = getResultActions(uri, HttpMethod.GET);

      //then
      SubAssyResponse subAssyResponse = SubAssyResponse.builder()
          .productNumber("sub assy number")
          .productName("sub assy name")
          .productId(1L)
          .stock(1)
          .quantity(1).build();
      FindProductWithSubassyResponse findProductWithSubassyResponse = FindProductWithSubassyResponse.builder()
          .productId(id).categoryId(categoryId)
          .codeNumber("10")
          .subAssy(List.of(subAssyResponse))
          .build();
      PageResponse<FindProductWithSubassyResponse> response =
          new PageResponse<>(
              new PageImpl<>(List.of(findProductWithSubassyResponse), pageRequest, 4));
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("categoryId가 null이면 bad request가 발생한다.")
    void categoryIdNull() throws Exception {
      //given
      String uri = getUri(null, 1, 3);

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.GET);

      //then
      actions.andExpect(status().isBadRequest())
          .andDo(print());
    }

    @Test
    @DisplayName("page관련 정보에 null이 들어갈 수 있다.")
    void pageableNullable() throws Exception {
      //given
      int page = 0;
      int size = 4;
      String uri = getUri(categoryId, null, null);
      PageRequest pageRequest = PageRequest.of(page, size);
      ProductServiceResponse productServiceResponse = ProductServiceResponse.builder()
          .productId(id).categoryId(categoryId).codeNumber("10").build();
      SubAssyServiceResponse subAssyServiceResponse = SubAssyServiceResponse.builder()
          .productServiceResponse(ProductServiceResponse.builder().productNumber("sub assy number")
              .productName("sub assy name")
              .productId(1L)
              .companyName("company")
              .stock(1).build())
          .quantity(1).build();
      FindProductWithSubassyServiceResponse ServiceDto = FindProductWithSubassyServiceResponse.builder()
          .productServiceResponse(productServiceResponse)
          .subAssyServiceResponses(List.of(subAssyServiceResponse))
          .build();
      Page<FindProductWithSubassyServiceResponse> dtoPage =
          new PageImpl<>(List.of(ServiceDto), pageRequest, size);
      ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);

      //when
      when(productService.findProductsWithSubAssys(eq(categoryId), any(Pageable.class)))
          .thenReturn(dtoPage);
      ResultActions actions = getResultActions(uri, HttpMethod.GET);

      //then
      verify(productService).findProductsWithSubAssys(eq(categoryId), argument.capture());
      assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(Pageable.unpaged());

      SubAssyResponse subAssyRespon = SubAssyResponse.builder()
          .productNumber("sub assy number")
          .productName("sub assy name")
          .productId(1L)
          .stock(1)
          .quantity(1).build();
      FindProductWithSubassyResponse findProductWithSubassyResponse = FindProductWithSubassyResponse.builder()
          .productId(id).categoryId(categoryId)
          .codeNumber("10")
          .subAssy(List.of(subAssyRespon))
          .build();
      PageResponse<FindProductWithSubassyResponse> response =
          new PageResponse<>(
              new PageImpl<>(List.of(findProductWithSubassyResponse), pageRequest, size));
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }
  }

  @Nested
  @DisplayName("part 정보로 product 조회")
  class FindProductsByPart {

    @Test
    @DisplayName("partId와 partName으로 product를 페이지네이션하여 조회한다.")
    void findProductsByPart() throws Exception {
      //given
      long partId = 10L;
      String partName = "part name";
      int page = 0;
      int size = 1;
      String fullUri =
          uri + "?part-id=" + partId + "&part-name=" + partName + "&page=" + page + "&size=" + size;
      Pageable pageable = PageRequest.of(page, size);
      ProductServiceResponse serviceResponse = ProductServiceResponse.builder()
          .productId(id)
          .productName(name).productNumber(number)
          .companyName(companyName).codeNumber(codeNumber)
          .stock(stock).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
          .build();
      Page<ProductServiceResponse> pageResponse = new PageImpl<>(
          List.of(serviceResponse), pageable, 1);
      ArgumentCaptor<FindProductByPartServiceRequest> requestArgument =
          ArgumentCaptor.forClass(FindProductByPartServiceRequest.class);
      ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);

      //when
      when(productService.findProductsByPart(any(FindProductByPartServiceRequest.class),
          any(Pageable.class))).thenReturn(pageResponse);
      ResultActions actions = getResultActions(fullUri, HttpMethod.GET);

      //then
      ProductResponse productResponse = ProductResponse.builder()
          .productId(id)
          .productName(name).productNumber(number)
          .companyName(companyName).codeNumber(codeNumber)
          .stock(stock).build();
      PageResponse<ProductResponse> response = new PageResponse<>(
          new PageImpl<>(List.of(productResponse), pageable, 1));

      verify(productService).findProductsByPart(requestArgument.capture(), pageArgument.capture());
      assertThat(requestArgument.getValue().getPartId()).isEqualTo(partId);
      assertThat(requestArgument.getValue().getPartName()).isEqualTo(partName);
      assertThat(pageArgument.getValue()).usingRecursiveComparison().isEqualTo(pageable);

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("part-id와 part-name에 null이 들어갈 수 있다")
    void partIdAndPartNameNullable() throws Exception {
      //given
      int page = 0;
      int size = 1;
      String fullUri =
          uri + "?part-id=&page=" + page + "&size=" + size;
      Pageable pageable = PageRequest.of(page, size);
      Page<ProductServiceResponse> pageResponse = new PageImpl<>(
          List.of(), pageable, 1);
      ArgumentCaptor<FindProductByPartServiceRequest> requestArgument =
          ArgumentCaptor.forClass(FindProductByPartServiceRequest.class);

      //when
      when(productService.findProductsByPart(any(FindProductByPartServiceRequest.class),
          any(Pageable.class))).thenReturn(pageResponse);
      ResultActions actions = getResultActions(fullUri, HttpMethod.GET);

      //then
      PageResponse<ProductResponse> response = new PageResponse<>(
          new PageImpl<>(List.of(), pageable, 1));

      verify(productService).findProductsByPart(requestArgument.capture(), any(Pageable.class));
      assertThat(requestArgument.getValue().getPartId()).isNull();
      assertThat(requestArgument.getValue().getPartName()).isNull();

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @DisplayName("page 정보에 null이 들어갈 수 있다.")
    void pageableNullable() throws Exception {
      //given
      long partId = 10L;
      String partName = "part name";
      String fullUri =
          uri + "?part-id=" + partId + "&part-name=" + partName + "&size=";
      Pageable pageable = Pageable.unpaged();
      Page<ProductServiceResponse> pageResponse = new PageImpl<>(
          List.of(), pageable, 1);
      ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);

      //when
      when(productService.findProductsByPart(any(FindProductByPartServiceRequest.class),
          any(Pageable.class))).thenReturn(pageResponse);
      ResultActions actions = getResultActions(fullUri, HttpMethod.GET);

      //then
      PageResponse<ProductResponse> response = new PageResponse<>(
          new PageImpl<>(List.of(), pageable, 1));

      verify(productService).findProductsByPart(any(FindProductByPartServiceRequest.class),
          pageArgument.capture());
      assertThat(pageArgument.getValue()).usingRecursiveComparison().isEqualTo(pageable);

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PRODUCT))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }
  }
}