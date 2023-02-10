package com.pororoz.istock.domain.bom.repository;

import com.pororoz.istock.domain.bom.entity.Bom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<Bom, Long> {

  Optional<Bom> findByLocationNumberAndProductIdAndPartId(String locationNumber, long productId,
      long partId);
}
