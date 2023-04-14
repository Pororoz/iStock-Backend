package com.pororoz.istock.domain.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.pororoz.istock.RepositoryTest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.dto.repository.ProductWaitingCount;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class ProductRepositoryTest extends RepositoryTest {

  @Autowired
  ProductRepository productRepository;
  @Autowired
  PartRepository partRepository;

  Category category1, category2;
  Part part;

  @BeforeEach
  void setUp() {
    //category 저장
    category1 = em.persist(Category.builder()
        .categoryName("c1").build());
    category2 = em.persist(Category.builder()
        .categoryName("c2").build());

    //part 저장
    part = em.persist(Part.builder()
        .partName("p").spec("p")
        .build());
    em.flush();
    em.clear();
  }

  @Nested
  class FindByCategoryIdWithBoms {

    @Test
    @DisplayName("categoryId에 해당하는 product를 BOM과 함께 페이지네이션하여 조회")
    void findProductsWithParts() {
      //given
      PageRequest page = PageRequest.of(0, 3);

      //product 저장
      em.persist(Product.builder()
          .productName("name").productNumber("numberX")
          .category(category2)
          .build());
      List<Product> products = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        Product product = em.persist(Product.builder()
            .productName("name").productNumber("number" + i)
            .category(category1)
            .build());
        products.add(product);
      }

      //bom 저장, product list에 있는 것은 하나의 bom를 갖는다.
      for (int i = 0; i < products.size(); i++) {
        em.persist(Bom.builder()
            .locationNumber("l" + i).product(products.get(i))
            .part(part).build());
      }
      em.flush();
      em.clear();

      //when
      // product list에 있는 product만 조회된다.
      Page<Product> pages = productRepository.findByCategoryIdWithSubAssies(page,
          category1.getId());

      //then
      assertThat(pages.getTotalElements()).isEqualTo(products.size());
      assertThat(pages.getTotalPages()).isEqualTo(4);
      assertThat(pages.getContent().size()).isEqualTo(page.getPageSize());
      List<Product> contents = pages.getContent();
      for (int i = 0, j = 0; i < page.getPageSize(); i++, j++) {
        Product product = contents.get(i);
        assertThat(product.getProductNumber()).isEqualTo("number" + i);
        assertThat(product.getBoms().size()).isEqualTo(1);

        Bom bom = product.getBoms().get(0);
        assertThat(bom.getLocationNumber()).isEqualTo("l" + j);
      }
    }

    @Test
    @DisplayName("조회할 product가 없다")
    void productListEmpty() {
      //given
      PageRequest page = PageRequest.of(0, 10);
      em.persist(Product.builder()
          .productName("aa").productNumber("number")
          .category(category2)
          .build());

      //when
      Page<Product> pages = productRepository.findByCategoryIdWithSubAssies(page,
          category1.getId());

      //then
      assertThat(pages.getTotalElements()).isEqualTo(0);
      assertThat(pages.getTotalPages()).isEqualTo(0);
      assertThat(pages.getContent().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("bom이 없으면 product만 조회된다.")
    void findProductWithoutParts() {
      //given
      PageRequest page = PageRequest.of(0, 10);
      Product product = em.persist(Product.builder()
          .productName("xxx").productNumber("number")
          .category(category1)
          .build());

      //when
      Page<Product> pages = productRepository.findByCategoryIdWithSubAssies(page,
          category1.getId());

      //then
      assertThat(pages.getTotalPages()).isEqualTo(1);
      assertThat(pages.getTotalElements()).isEqualTo(1);
      assertThat(pages.getContent().size()).isEqualTo(1);
      assertThat(pages.getContent().get(0)).usingRecursiveComparison().isEqualTo(product);
    }
  }

  @Nested
  class FindByPartIdAndPartNameIgnoreNull {

    List<Product> products;
    Part otherPart;

    @BeforeEach
    void setUp() {
      products = new ArrayList<>();
      for (int i = 0; i < 4; i++) {
        Product product = em.persist(Product.builder()
            .productName("name" + i).productNumber("number" + i)
            .codeNumber("code" + i).category(category1)
            .build());
        products.add(product);
      }
      otherPart = em.persist(Part.builder()
          .partName("p").spec("x")
          .build());
      for (int i = 0; i < 2; i++) {
        em.persist(Bom.builder()
            .locationNumber("" + i).part(part)
            .product(products.get(i)).build());
      }
      for (int i = 1; i < 3; i++) {
        em.persist(Bom.builder()
            .locationNumber("" + i).part(otherPart)
            .product(products.get(i)).build());
      }
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("partId, partName으로 product를 조회한다.")
    void findByPartIdAndPartName() {
      //given
      //when
      Page<Product> productPage = productRepository.findByPartIdAndPartNameIgnoreNull(
          part.getId(), part.getPartName(), Pageable.unpaged());

      //then
      assertThat(productPage.getTotalElements()).isEqualTo(2);
      assertThat(productPage.getTotalPages()).isEqualTo(1);
      assertThat(productPage.getContent()).usingRecursiveComparison()
          .ignoringFields("category", "boms")
          .isEqualTo(List.of(products.get(0), products.get(1)));
    }

    @Test
    @DisplayName("partId가 null이면 무시한다.")
    void findProductsIgnorePartIdNull() {
      //given
      //when
      Page<Product> productPage = productRepository.findByPartIdAndPartNameIgnoreNull(
          null, part.getPartName(), Pageable.unpaged());

      //then
      assertThat(productPage.getTotalElements()).isEqualTo(3);
      assertThat(productPage.getTotalPages()).isEqualTo(1);
      assertThat(productPage.getContent()).usingRecursiveComparison()
          .ignoringFields("category", "boms")
          .isEqualTo(List.of(products.get(0), products.get(1), products.get(2)));
    }

    @Test
    @DisplayName("partName이 null이면 무시한다.")
    void findProductsIgnorePartNameNull() {
      //given
      //when
      Page<Product> productPage = productRepository.findByPartIdAndPartNameIgnoreNull(
          part.getId(), null, Pageable.unpaged());

      //then
      assertThat(productPage.getTotalElements()).isEqualTo(2);
      assertThat(productPage.getTotalPages()).isEqualTo(1);
      assertThat(productPage.getContent()).usingRecursiveComparison()
          .ignoringFields("category", "boms")
          .isEqualTo(List.of(products.get(0), products.get(1)));
    }

    @Test
    @DisplayName("partId와 partName가 null이면 전체를 조회한다.")
    void findProductsIgnorePartIdAndPartNameNull() {
      //given
      //when
      Page<Product> productPage = productRepository.findByPartIdAndPartNameIgnoreNull(
          null, null, Pageable.unpaged());

      //then
      assertThat(productPage.getTotalElements()).isEqualTo(4);
      assertThat(productPage.getTotalPages()).isEqualTo(1);
      assertThat(productPage.getContent()).usingRecursiveComparison()
          .ignoringFields("category", "boms")
          .isEqualTo(products);
    }
  }

  @Nested
  class FindByIdWithParts {

    @Test
    @DisplayName("Product, Bom, Part, SubAssy를 join하여 조회한다.")
    void productAndBomAndPartJoin() {
      //given
      Product product = em.persist(Product.builder()
          .codeNumber("code1").productName("name1")
          .productNumber("number").category(category1)
          .build());
      Product subAssy = em.persist(Product.builder()
          .codeNumber("11").productName("name2")
          .productNumber("subAssyNumber").category(category1)
          .build());
      em.persist(Bom.builder()
          .locationNumber("location1")
          .product(product).part(part)
          .build());
      em.persist(Bom.builder()
          .locationNumber("location2").codeNumber("11")
          .product(product).subAssy(subAssy)
          .build());
      em.flush();
      em.clear();

      //when
      Product productWithPartsAndSubAssies = productRepository.findByIdWithPartsAndSubAssies(
              product.getId())
          .orElse(null);

      //then
      assertThat(productWithPartsAndSubAssies).usingRecursiveComparison()
          .ignoringFields("category", "boms.product.category", "boms.subAssy.category")
          .isEqualTo(product);
    }

    @Test
    @DisplayName("연관된 Bom이 없으면 null이 조회된다.")
    void findNullIfBomNotExist() {
      //given
      Product product = em.persist(Product.builder()
          .codeNumber("code1").productName("name1")
          .productNumber("number").category(category1)
          .build());

      //then
      Product productWithParts = productRepository.findByIdWithPartsAndSubAssies(product.getId())
          .orElse(null);

      //then
      assertThat(productWithParts).isNull();
    }
  }

  @Nested
  class FindWaitingCountByIdList {

    @Test
    @DisplayName("productId list를 사용해 생산대기와 구매대기의 개수를 조회한다.")
    void findWaitingCountByIdList() {
      //given
      Product p1 = em.persist(Product.builder().
          productName("p1").productNumber("1").category(category1)
          .build());
      Product p2 = em.persist(Product.builder().
          productName("p2").productNumber("2").category(category1)
          .build());
      // p1에 각각 2개의 구매, 생산 대기 생성
      for (int i = 0; i < 2; i++) {
        em.persist(ProductIo.builder()
            .quantity(1).status(ProductStatus.구매대기).product(p1)
            .build());
        em.persist(ProductIo.builder()
            .quantity(1).status(ProductStatus.생산대기).product(p1)
            .build());
      }
      List<Long> productIdList = List.of(p1.getId(), p2.getId());

      //when
      List<ProductWaitingCount> waitingCounts = productRepository.findWaitingCountByIdList(
          productIdList);

      //then
      List<Long> expectedCounts = List.of(2L, 0L);
      assertThat(waitingCounts).hasSize(2);
      for (int i = 0; i < waitingCounts.size(); i++) {
        ProductWaitingCount waitingCount = waitingCounts.get(i);
        Long expectedCount = expectedCounts.get(i);
        assertThat(waitingCount.getProductionWaitingCount()).isEqualTo(expectedCount);
        assertThat(waitingCount.getPurchaseWaitingCount()).isEqualTo(expectedCount);
      }
    }
  }
}