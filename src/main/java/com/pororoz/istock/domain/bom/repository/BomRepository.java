package com.pororoz.istock.domain.bom.repository;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.Part;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BomRepository extends JpaRepository<Bom, Long> {

  @Query(value = "select b from Bom b "
      + "left join fetch b.part "
      + "left join fetch b.subAssy "
      + "where b.product.id = :productId "
      + "order by b.id",
      countQuery = "select count(b) from Bom b where b.product.id = :productId ")
  Page<Bom> findByProductIdWithPartAndSubAssy(Pageable pageable,
      @Param("productId") Long productId);

  Optional<Bom> findByLocationNumberAndProductIdAndSubAssyIdAndPartId(String locationNumber,
      Long productId, Long subAssyId, Long partId);

  List<Bom> findByProductId(Long productId);

  Optional<Bom> findByProductIdAndSubAssyId(Long productId, Long subAssyId);

  boolean existsByProductId(Long subAssyId);

  boolean existByPart(Part part);
}
