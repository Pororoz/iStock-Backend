package com.pororoz.istock.domain.part.repository;

import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.product.entity.ProductIo;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartIoRepository extends JpaRepository<PartIo, Long> {

  List<PartIo> findByProductIo(ProductIo productIo);

  @Query("select pi from PartIo pi "
      + "left join fetch pi.part "
      + "where pi.productIo = :productIo")
  List<PartIo> findByProductIoWithPart(@Param("productIo") ProductIo productIo);

  @Query(value = "select p from PartIo p "
      + "left join fetch p.part "
      + "where (:status is null or (cast(p.status as string) like %:status%)) and"
      + "(:partId is null or p.part.id = :partId)"
      , countQuery = "select p from PartIo p "
      + "where (:status is null or (cast(p.status as string) like %:status%)) and"
      + "(:partId is null or p.part.id = :partId)")
  Page<PartIo> findByStatusContainingAndPartIdWithPart(@Param("status") String status,
      @Param("partId") Long partId, Pageable pageable);

  boolean existsByPart(Part part);
}
