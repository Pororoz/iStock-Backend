package com.pororoz.istock.domain.part.repository;

import com.pororoz.istock.domain.part.dto.repository.PartPurchaseCount;
import com.pororoz.istock.domain.part.entity.Part;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartRepository extends JpaRepository<Part, Long> {

  Optional<Part> findByPartNameAndSpec(String partName, String spec);

  @Query(value = "SELECT p FROM Part p "
      + "WHERE ((:partId IS NULL OR p.id = :partId) AND "
      + "(:partName IS NULL OR p.partName = :partName) AND "
      + "(:spec IS NULL OR p.spec = :spec))",
      countQuery = "SELECT p FROM Part p "
          + "WHERE ((:partId IS NULL OR p.id = :partId) AND "
          + "(:partName IS NULL OR p.partName = :partName) AND "
          + "(:spec IS NULL OR p.spec = :spec))")
  Page<Part> findByIdAndPartNameAndSpecIgnoreNull(@Param("partId") Long partId,
      @Param("partName") String partName, @Param("spec") String spec,
      Pageable pageable);

  @Query("select distinct part "
      + "from Product prod join Bom b on prod = b.product "
      + "join b.part part "
      + "where prod.id in (:idList)")
  List<Part> findByProductIdList(@Param("idList") List<Long> productIdList);

  @Query("select p.id as id, p.partName as partName, "
      + "p.spec as spec, p.stock as stock, "
      + "sum(case when (cast(pi.status as string) = '구매대기') then pi.quantity else 0 end) as purchaseWaitingCount "
      + "from Part p left join PartIo pi on p = pi.part "
      + "where p.id in (:idList) "
      + "group by p.id "
      + "order by p.id")
  List<PartPurchaseCount> findPurchaseCountByPartIdList(@Param("idList") List<Long> partIdList);
}
