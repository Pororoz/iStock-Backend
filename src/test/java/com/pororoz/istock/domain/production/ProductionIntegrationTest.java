package com.pororoz.istock.domain.production;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.production.dto.request.SaveProductionRequest;
import com.pororoz.istock.domain.production.dto.response.SaveProductionResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

public class ProductionIntegrationTest extends IntegrationTest {

  @Autowired
  BomRepository bomRepository;

  @Autowired
  PartRepository partRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  PartIoRepository partIoRepository;

  @Autowired
  ProductIoRepository productIoRepository;

  String getUri(Long productId) {
    return "/v1/production/products/" + productId + "/waiting";
  }

  Category category;
  Product product1, subAssy1, subAssy2;
  Part part1, part2;

  @BeforeEach
  void setUp() {
    category = categoryRepository.save(Category.builder()
        .categoryName("category1").build());
    product1 = productRepository.save(Product.builder()
        .productName("name1").productNumber("number1")
        .stock(1).codeNumber("0")
        .category(category).build());
    subAssy1 = productRepository.save(Product.builder()
        .productName("sub assy1").productNumber("sub assy1")
        .stock(1).codeNumber("11")
        .category(category).build());
    subAssy2 = productRepository.save(Product.builder()
        .productName("sub assy2").productNumber("sub assy2")
        .stock(2).codeNumber("11")
        .category(category).build());
    part1 = partRepository.save(Part.builder()
        .spec("spec1").partName("name1")
        .stock(1).build());
    part2 = partRepository.save(Part.builder()
        .spec("spec2").partName("name2")
        .stock(2).build());
  }

  Bom saveBom(String locationNumber, long quantity, Part part) {
    return bomRepository.save(Bom.builder()
        .locationNumber(locationNumber).quantity(quantity)
        .product(product1).part(part)
        .build());
  }

  Bom saveSubAssyBom(String locationNumber, long quantity, Product subAssy) {
    return bomRepository.save(Bom.builder()
        .locationNumber(locationNumber).quantity(quantity)
        .subAssy(subAssy).codeNumber("11")
        .product(product1)
        .build());
  }

  ProductIo createProductIo(long quantity, ProductStatus status) {
    return ProductIo.builder()
        .quantity(quantity).status(status)
        .build();
  }

  PartIo createPartIo(long quantity, PartStatus status) {
    return PartIo.builder()
        .quantity(quantity).status(status)
        .build();
  }

  @Nested
  @WithMockUser
  @DisplayName("POST - v1/production/products/{productId}/waiting 제품 생산 대기 저장")
  class SaveWaitProduction {

    Long productId = 1L;
    long quantity = 5L;

    @Test
    @DisplayName("제품 생산 대기 요청을 하면 부품과 subassy의 개수를 줄이고 결과를 반환한다.")
    void saveWaitProduction() throws Exception {
      //given
      Bom bom1 = saveBom("location1", 1, part1);
      Bom bom2 = saveBom("location2", 2, part2);
      Bom subAssyBom1 = saveSubAssyBom("location3", 1, subAssy1);
      Bom subAssyBom2 = saveSubAssyBom("location4", 2, subAssy2);

      SaveProductionRequest request = SaveProductionRequest.builder().quantity(quantity).build();

      //when
      ResultActions actions = getResultActions(getUri(productId), HttpMethod.POST, request);

      //then
      SaveProductionResponse response = SaveProductionResponse.builder().productId(productId)
          .quantity(quantity).build();
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(ResponseStatus.OK))
          .andExpect(jsonPath("$.message").value(ResponseMessage.WAIT_PRODUCTION))
          .andExpect(jsonPath("$.data", equalTo(asParsedJson(response))))
          .andDo(print());
      assertThat(partRepository.findById(part1.getId()).orElseThrow().getStock()).isZero();
      assertThat(partRepository.findById(part2.getId()).orElseThrow().getStock()).isZero();
      assertThat(productRepository.findById(subAssy1.getId()).orElseThrow().getStock()).isZero();
      assertThat(productRepository.findById(subAssy2.getId()).orElseThrow().getStock()).isZero();

      ProductIo productIo = createProductIo(quantity, ProductStatus.생산대기);
      ProductIo subAssyIo1 = createProductIo(subAssyBom1.getQuantity(), ProductStatus.사내출고대기);
      ProductIo subAssyIo2 = createProductIo(subAssyBom2.getQuantity(), ProductStatus.사내출고대기);
      PartIo partIo1 = createPartIo(bom1.getQuantity(), PartStatus.생산대기);
      PartIo partIo2 = createPartIo(bom2.getQuantity(), PartStatus.생산대기);

      assertThat(partIoRepository.findAll()).usingRecursiveComparison()
          .ignoringFields("createdAt", "updatedAt", "id", "part", "productIo")
          .isEqualTo(List.of(partIo1, partIo2));
      assertThat(productIoRepository.findAll()).usingRecursiveComparison()
          .ignoringFields("createdAt", "updatedAt", "id", "product", "superIo")
          .isEqualTo(List.of(productIo, subAssyIo1, subAssyIo2));
    }

    @Test
    @DisplayName("BOM에 part가 null이면 Bad Request가 발생한다.")
    void bomsPartNull() throws Exception {
      //given
      saveBom("location1", 1, part1);
      saveBom("location2", 2, null);

      SaveProductionRequest request = SaveProductionRequest.builder().quantity(quantity).build();

      //when
      ResultActions actions = getResultActions(getUri(productId), HttpMethod.POST, request);

      //then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(ExceptionStatus.RUNTIME_ERROR))
          .andDo(print());
    }
  }
}
