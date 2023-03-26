package com.pororoz.istock.domain.part.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.pororoz.istock.RepositoryTest;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.product.entity.ProductIo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PartIoRepositoryTest extends RepositoryTest {

  @Autowired
  PartIoRepository partIoRepository;

  Part part;

  @BeforeEach
  void setUp() {
    part = em.persist(Part.builder()
        .partName("name")
        .spec("spec").build());
    em.flush();
    em.clear();
  }

  @Nested
  class FindByStatusContaining {

    @Test
    @DisplayName("status의 일부를 포함하는 partIo를 조회한다.")
    void findByStatusContainingWithPart() {
      // given
      PartIo partIo1 = em.persist(PartIo.builder()
          .quantity(10)
          .status(PartStatus.구매대기)
          .part(part).build());
      PartIo partIo2 = em.persist(PartIo.builder()
          .quantity(10)
          .status(PartStatus.생산대기)
          .part(part).build());
      PartIo partIo3 = em.persist(PartIo.builder()
          .quantity(10)
          .status(PartStatus.생산완료)
          .part(part).build());
      em.flush();
      em.clear();


      // when
      Page<PartIo> partIoPage = partIoRepository.findByStatusContainingWithPart(
          "대기", Pageable.unpaged());

      // then
      assertThat(partIoPage.getTotalPages()).isEqualTo(1);
      assertThat(partIoPage.getTotalElements()).isEqualTo(2);

      List<PartIo> content = partIoPage.getContent();
      List<PartIo> expected = List.of(partIo1, partIo2);
      assertThat(content).usingRecursiveComparison()
          .isEqualTo(expected);
    }
  }
}
