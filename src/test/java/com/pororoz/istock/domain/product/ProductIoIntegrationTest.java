package com.pororoz.istock.domain.product;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.dto.PageResponse;
import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.dto.response.FindProductIoResponse;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
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

public class ProductIoIntegrationTest extends IntegrationTest {

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ProductIoRepository productIoRepository;

  Product product, subAssy;

  @BeforeEach
  void setUp() {
    Category category = categoryRepository.save(
        Category.builder().categoryName("category").build());
    product = productRepository.save(Product.builder()
        .productName("name1").productNumber("number1")
        .codeNumber("1").category(category)
        .build());
    subAssy = productRepository.save(Product.builder()
        .productName("name2").productNumber("number2")
        .codeNumber("11").category(category)
        .build());
  }

  String getUri(int page, int size, String status) {
    return "/v1/product-io?page=" + page + "&size=" + size + "&status=" + status;
  }

  @Nested
  @DisplayName("GET /v1/product-io - 제품 IO 조회")
  class FindProductIo {

    @Test
    @WithMockUser
    @DisplayName("상태에 '완료'가 들어간 제품의 2페이지를 조회한다.")
    void findProductIoConfirm() throws Exception {
      //given
      int page = 1, size = 1;
      String uri = getUri(page, size, "완료");
      ProductIo productIo = productIoRepository.save(ProductIo.builder()
          .status(ProductStatus.생산완료)
          .quantity(1)
          .product(product).build());
      ProductIo subAssyIo = productIoRepository.save(ProductIo.builder()
          .status(ProductStatus.사내출고완료)
          .quantity(2)
          .product(subAssy)
          .superIo(productIo).build());

      //when
      ResultActions actions = getResultActions(uri, HttpMethod.GET);

      //then
      FindProductIoResponse findProductIoResponse = FindProductIoResponse.builder()
          .productIoId(subAssyIo.getId())
          .quantity(subAssyIo.getQuantity())
          .status(ProductStatus.사내출고완료)
          .createdAt(TimeEntity.formatTime(subAssyIo.getCreatedAt()))
          .updatedAt(TimeEntity.formatTime(subAssyIo.getUpdatedAt()))
          .superIoId(productIo.getId())
          .productId(subAssy.getId())
          .productName(subAssy.getProductName())
          .productNumber(subAssy.getProductNumber()).build();

      PageResponse<FindProductIoResponse> response = new PageResponse<>(
          new PageImpl<>(List.of(findProductIoResponse), PageRequest.of(page, size), 2)
      );

      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.FIND_PRODUCT_IO))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 유저는 접근할 수 없다.")
    void cannotAccessAnonymous() throws Exception {
      //given
      //when
      ResultActions actions = getResultActions(getUri(0, 0, ""), HttpMethod.GET);

      //then
      actions.andExpect(status().isForbidden());
    }
  }

}
