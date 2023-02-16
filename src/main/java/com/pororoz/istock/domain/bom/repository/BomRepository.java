package com.pororoz.istock.domain.bom.repository;

import com.pororoz.istock.domain.bom.entity.Bom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<Bom, Long> {

  Optional<Bom> findByLocationNumberAndProductIdAndPartId(String locationNumber, Long productId,
      Long partId);

  List<Bom> findByProductId(Long productId);

  // (선택)성능 개선
  Boolean existsByProductNumber(String productNumber);
}
