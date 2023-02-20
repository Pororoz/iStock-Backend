package com.pororoz.istock.domain.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.pororoz.istock.RepositoryTest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
    Page<Product> pages = productRepository.findByCategoryIdWithBoms(page,
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
    Page<Product> pages = productRepository.findByCategoryIdWithBoms(page,
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
    Page<Product> pages = productRepository.findByCategoryIdWithBoms(page,
        category1.getId());

    //then
    assertThat(pages.getTotalPages()).isEqualTo(1);
    assertThat(pages.getTotalElements()).isEqualTo(1);
    assertThat(pages.getContent().size()).isEqualTo(1);
    assertThat(pages.getContent().get(0)).usingRecursiveComparison().isEqualTo(product);
  }

  @Test
  @DisplayName("Product number list에 포함된 product만 조회한다.")
  void findByProductNumbers() {
    //given
    Product a = em.persist(Product.builder()
        .productNumber("a").productName("name")
        .category(category1).build());
    Product aa = em.persist(Product.builder()
        .productNumber("aa").productName("name")
        .category(category1).build());
    Product aaa = em.persist(Product.builder()
        .productNumber("aaa").productName("name")
        .category(category1).build());
    em.flush();
    em.clear();

    //when
    List<Product> products = productRepository.findByProductNumbers(Set.of("aaa", "a"));

    //then
    assertThat(products).hasSize(2);
    assertThat(products).usingRecursiveComparison()
        .ignoringFields("category")
        .ignoringCollectionOrder()
        .isEqualTo(List.of(aaa, a));
    assertThat(products).usingRecursiveComparison()
        .ignoringFields("category")
        .ignoringCollectionOrder()
        .isNotEqualTo(List.of(a, aa));
  }
}