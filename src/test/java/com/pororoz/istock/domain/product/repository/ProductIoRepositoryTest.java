package com.pororoz.istock.domain.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.pororoz.istock.RepositoryTest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class ProductIoRepositoryTest extends RepositoryTest {

  @Autowired
  ProductIoRepository productIoRepository;

  Category category;
  Product product, subAssy1, subAssy2;

  @BeforeEach
  void setUp() {
    category = em.persist(Category.builder().categoryName("category").build());
    product = em.persist(Product.builder()
        .productName("name")
        .productNumber("number")
        .category(category).build());
    subAssy1 = em.persist(Product.builder()
        .codeNumber("11")
        .productName("sub name1")
        .productNumber("sub number1")
        .category(category).build());
    subAssy2 = em.persist(Product.builder()
        .codeNumber("11")
        .productName("sub name2")
        .productNumber("sub number2")
        .category(category).build());
    em.flush();
    em.clear();
  }

  @Nested
  class FindByStatusContaining {

    @Test
    @DisplayName("status의 일부를 포함하는 productIo를 조회한다.")
    void findByStatusContaining() {
      //given
      ProductIo productIo = em.persist(ProductIo.builder()
          .quantity(1)
          .product(product)
          .status(ProductStatus.생산대기).build());
      ProductIo subAssyIo1 = em.persist(ProductIo.builder()
          .quantity(2)
          .product(subAssy1)
          .superIo(productIo)
          .status(ProductStatus.사내출고대기).build());
      ProductIo subAssyIo2 = em.persist(ProductIo.builder()
          .quantity(3)
          .product(subAssy2)
          .superIo(productIo)
          .status(ProductStatus.사내출고대기).build());
      em.flush();
      em.clear();

      //when
      Page<ProductIo> productIoPage = productIoRepository.findByStatusContainingWithProduct("대기",
          Pageable.unpaged());

      //then
      assertThat(productIoPage.getTotalPages()).isEqualTo(1);
      assertThat(productIoPage.getTotalElements()).isEqualTo(3);

      List<ProductIo> content = productIoPage.getContent();
      List<ProductIo> expected = List.of(productIo, subAssyIo1, subAssyIo2);
      assertThat(content).usingRecursiveComparison()
          .ignoringFields("superIo", "subAssyIoList", "partIoList",
              "product.category", "product.boms")
          .isEqualTo(expected);
    }
  }
}