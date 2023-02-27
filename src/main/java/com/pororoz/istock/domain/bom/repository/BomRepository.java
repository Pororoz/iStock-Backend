package com.pororoz.istock.domain.bom.repository;

import com.pororoz.istock.domain.bom.entity.Bom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BomRepository extends JpaRepository<Bom, Long> {

  Optional<Bom> findByLocationNumberAndProductIdAndPartId(String locationNumber, Long productId,
      Long partId);

  List<Bom> findByProductId(Long productId);

  // (선택)성능 개선
  Boolean existsBySubAssyNumber(String subAssyNumber);

  List<Bom> findBySubAssyNumber(String subAssyNumber);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update Bom b set b.subAssyNumber = :newSubAssyNumber "
      + "where b.subAssyNumber = :oldSubAssyNumber")
  void updateSubAssyNumber(@Param("oldSubAssyNumber") String oldSubAssyNumber,
      @Param("newSubAssyNumber") String newSubAssyNumber);

  @Query("select b from Bom b where b.product.id = :productId and b.subAssyNumber = :subAssyNumber")
  Optional<Bom> findByProductIdAndSubAssyNumber(@Param("productId") Long productId,
      @Param("subAssyNumber") String subAssyNumber);
}
