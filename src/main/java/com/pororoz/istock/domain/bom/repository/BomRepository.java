package com.pororoz.istock.domain.bom.repository;

import com.pororoz.istock.domain.bom.entity.Bom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BomRepository extends JpaRepository<Bom, Long> {

  @Query(value = "select b from Bom b "
      + "left join fetch b.part p "
      + "where b.product.id = :productId "
      + "order by b.id",
      countQuery = "select count(b) from Bom b where b.product.id = :productId ")
  Page<Bom> findByProductIdWithPart(Pageable pageable, @Param("productId") Long productId);

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
}
