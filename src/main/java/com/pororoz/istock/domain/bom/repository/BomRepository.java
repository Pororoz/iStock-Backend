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
  Boolean existsByProductNumber(String productNumber);

  List<Bom> findByProductNumber(String productNumber);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update Bom b set b.productNumber = :newProductNumber "
      + "where b.productNumber = :oldProductNumber")
  void updateProductNumber(@Param("oldProductNumber") String oldProductNumber,
      @Param("newProductNumber") String newProductNumber);

  @Query("select b from Bom b where b.product.id = :productId and b.productNumber = :productNumber")
  Optional<Bom> findByProductIdAndProductNumber(@Param("productId") Long productId,
      @Param("productNumber") String productNumber);
}
