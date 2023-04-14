package com.pororoz.istock.domain.part.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.pororoz.istock.RepositoryTest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.part.dto.repository.PartPurchaseCount;
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
    List<Part> parts = setUpPart();
    setUpBom(products, parts);
    em.flush();
    em.clear();

    //when
    List<Part> findParts = partRepository.findByProductIdList(
        products.stream().map(Product::getId).toList());

    //then
    assertThat(findParts).hasSize(2);
    assertThat(findParts).usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(parts);
  }

  @Test
  @DisplayName("part id list로 part와 partIo를 조회하여 '구매대기' 부품의 정보를 조회한다.")
  void findPurchaseCountByPartIdList() {
    //given
    List<Part> parts = setUpPart();
    setUpPartIo(parts);

    //when
    List<PartPurchaseCount> partPurchaseCounts = partRepository.findPurchaseCountByPartIdList(
        parts.stream().map(Part::getId).toList());

    //then
    assertThat(partPurchaseCounts).hasSize(2);
    assertThat(partPurchaseCounts.get(0).getId()).isEqualTo(parts.get(0).getId());
    assertThat(partPurchaseCounts.get(0).getPurchaseWaitingCount()).isEqualTo(3);
    assertThat(partPurchaseCounts.get(1).getId()).isEqualTo(parts.get(1).getId());
    assertThat(partPurchaseCounts.get(1).getPurchaseWaitingCount()).isEqualTo(20);
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

  List<Part> setUpPart() {
    Part part1 = em.persist(Part.builder()
        .partName("name1").spec("spec1")
        .build());
    Part part2 = em.persist(Part.builder()
        .partName("name2").spec("spec2")
        .build());
    return List.of(part1, part2);
  }

  void setUpBom(List<Product> products, List<Part> parts) {
    em.persist(Bom.builder()
        .locationNumber("loc1").product(products.get(0)).part(parts.get(0))
        .build());
    em.persist(Bom.builder()
        .locationNumber("loc1").product(products.get(0)).part(parts.get(1))
        .build());
    em.persist(Bom.builder()
        .locationNumber("loc1").product(products.get(1)).part(parts.get(0))
        .build());
    em.persist(Bom.builder()
        .locationNumber("loc1").product(products.get(1)).part(parts.get(1))
        .build());
  }

  // part1 구매대기 3개, part2 구매대기 20개
  void setUpPartIo(List<Part> parts) {
    em.persist(PartIo.builder()
        .quantity(3).status(PartStatus.구매대기).part(parts.get(0))
        .build());
    for (int i = 0; i < 2; i++) {
      em.persist(PartIo.builder()
          .quantity(10).status(PartStatus.구매대기).part(parts.get(1))
          .build());
    }
    em.persist(PartIo.builder()
        .quantity(1).status(PartStatus.생산대기).part(parts.get(1))
        .build());
    em.persist(PartIo.builder()
        .quantity(1).status(PartStatus.구매확정).part(parts.get(1))
        .build());
  }
}