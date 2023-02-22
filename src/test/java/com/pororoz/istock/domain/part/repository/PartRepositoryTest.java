package com.pororoz.istock.domain.part.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.pororoz.istock.RepositoryTest;
import com.pororoz.istock.domain.part.entity.Part;
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
}