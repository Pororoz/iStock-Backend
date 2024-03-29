package com.pororoz.istock.domain.product.repository;

import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.product.dto.repository.ProductWaitingCount;
import com.pororoz.istock.domain.product.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

  Optional<Product> findByProductNumber(String productNumber);

  Optional<Product> findByProductNumberAndProductName(String productNumber, String productName);

  @Query(value = "select distinct p from Product p "
      + "left join fetch p.boms b "
      + "left join fetch b.subAssy s "
      + "where p.category.id = :categoryId "
      + "order by p.id",
      countQuery = "select count(p) from Product p where p.category.id = :categoryId ")
  Page<Product> findByCategoryIdWithSubAssies(Pageable pageable,
      @Param("categoryId") Long categoryId);

  @Query("select distinct pr from Product pr "
      + "join fetch pr.boms b left join fetch b.part left join fetch b.subAssy "
      + "where pr.id = :id")
  Optional<Product> findByIdWithPartsAndSubAssies(@Param("id") Long id);

  @Query(value = "select distinct pr from Product pr "
      + "left join pr.boms b "
      + "left join b.part pa "
      + "where ((:partId is null or pa.id = :partId) and "
      + "(:partName is null or pa.partName = :partName)) ",
      countQuery = "select distinct pr from Product pr "
          + "left join pr.boms b "
          + "left join b.part pa "
          + "where ((:partId is null or pa.id = :partId) and "
          + "(:partName is null or pa.partName = :partName)) ")
  Page<Product> findByPartIdAndPartNameIgnoreNull(@Param("partId") Long partId,
      @Param("partName") String partName, Pageable pageable);

  boolean existsByCategory(Category category);

  // return 값으로 projection 사용
  @Query("select p.id as id, p.productName as productName, "
      + "sum(case when (cast(pi.status as string) = '생산대기') then pi.quantity else 0 end) as productionWaitingCount, "
      + "sum(case when (cast(pi.status as string) = '구매대기') then pi.quantity else 0 end) as purchaseWaitingCount "
      + "from Product p left join ProductIo pi on pi.product = p "
      + "where p.id in (:idList) "
      + "group by p.id "
      + "order by p.id")
  List<ProductWaitingCount> findWaitingCountByIdList(@Param("idList") List<Long> idList);
}
