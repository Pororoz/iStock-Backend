package com.pororoz.istock.domain.part.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.pororoz.istock.RepositoryTest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.product.entity.Product;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class PartRepositoryTest extends RepositoryTest {

  @Autowired
  PartRepository partRepository;

  @Test
  @DisplayName("id와 spec이 null이면 무시하고 part를 조회한다.")
  void findByIdAndPartNameAndSpecNull() {
    //given
    List<Part> parts = new ArrayList<>();
    parts.add(em.persist(Part.builder().partName("a").spec("a").build()));
    parts.add(em.persist(Part.builder().partName("a").spec("b").build()));
    em.persist(Part.builder().partName("a").spec("c").build());
    em.flush();
    em.clear();
    Pageable pageable = PageRequest.of(0, 2);

    //when
    Page<Part> page = partRepository.findByIdAndPartNameAndSpecIgnoreNull(null, "a", null,
        pageable);

    //then
    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getContent()).hasSize(2);
    assertThat(page.getContent()).usingRecursiveComparison().isEqualTo(parts);
  }

  @Test
  @DisplayName("모든 파라미터에 유효한 값이 있고, 그 값으로 part를 조회한다.")
  void findByIdAndPartNameAndSpecNotNull() {
    //given
    List<Part> parts = new ArrayList<>();
    parts.add(em.persist(Part.builder().partName("a").spec("a").build()));
    parts.add(em.persist(Part.builder().partName("a").spec("b").build()));
    em.persist(Part.builder().partName("a").spec("c").build());
    em.flush();
    em.clear();
    Pageable pageable = PageRequest.of(0, 2);

    //when
    Page<Part> page = partRepository.findByIdAndPartNameAndSpecIgnoreNull(parts.get(1).getId(),
        parts.get(1).getPartName(), parts.get(1).getSpec(), pageable);

    //then
    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getTotalPages()).isEqualTo(1);
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getContent()).usingRecursiveComparison().isEqualTo(List.of(parts.get(1)));
  }

  @Test
  @DisplayName("product id list로 product, bom, part를 join하여 part를 조회한다.")
  void findByProductIdList() {
    //given
    Category category = em.persist(Category.builder().categoryName("c").build());
    List<Product> products = setUpProduct(category);
    Part part1 = em.persist(Part.builder()
        .partName("name1").spec("spec1")
        .build());
    Part part2 = em.persist(Part.builder()
        .partName("name2").spec("spec2")
        .build());
    setUpBom(products.get(0), products.get(1), part1, part2);
    em.flush();
    em.clear();

    //when
    List<Part> parts = partRepository.findByProductIdList(
        products.stream().map(Product::getId).toList());

    //then
    List<Part> expected = List.of(part1, part2);
    assertThat(parts).hasSize(2);
    assertThat(parts).usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expected);
  }

  List<Product> setUpProduct(Category category) {
    Product product1 = em.persist(Product.builder()
        .productName("name1").productNumber("number1").category(category)
        .build());
    Product product2 = em.persist(Product.builder()
        .productName("name2").productNumber("number2").category(category)
        .build());
    Product product3 = em.persist(Product.builder()
        .productName("name3").productNumber("number3").category(category)
        .build());
    return List.of(product1, product2, product3);
  }

  void setUpBom(Product product1, Product product2, Part part1, Part part2) {
    em.persist(Bom.builder()
        .locationNumber("loc1").product(product1).part(part1)
        .build());
    em.persist(Bom.builder()
        .locationNumber("loc1").product(product1).part(part2)
        .build());
    em.persist(Bom.builder()
        .locationNumber("loc1").product(product2).part(part1)
        .build());
    em.persist(Bom.builder()
        .locationNumber("loc1").product(product2).part(part2)
        .build());
  }

  // part1 구매대기 3개, part2 구매대기 20개
  void setUpPartIo(Part part1, Part part2) {
    em.persist(PartIo.builder()
        .quantity(3).status(PartStatus.구매대기).part(part1)
        .build());
    for (int i = 0; i < 2; i++) {
      em.persist(PartIo.builder()
          .quantity(10).status(PartStatus.구매대기).part(part2)
          .build());
    }
    em.persist(PartIo.builder()
        .quantity(1).status(PartStatus.생산대기).part(part2)
        .build());
    em.persist(PartIo.builder()
        .quantity(1).status(PartStatus.구매확정).part(part2)
        .build());
  }
}