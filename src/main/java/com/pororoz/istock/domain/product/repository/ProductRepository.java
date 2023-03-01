package com.pororoz.istock.domain.product.repository;

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

  @Query(value = "select p from Product p "
      + "left join fetch p.boms b "
      + "where p.category.id = :categoryId "
      + "order by p.id",
      countQuery = "select count(p) from Product p where p.category.id = :categoryId ")
  Page<Product> findByCategoryIdWithBoms(Pageable pageable,
      @Param("categoryId") Long categoryId);

  @Query("select p from Product p where p.id in :ids")
  List<Product> findByIdIn(@Param("ids") List<Long> ids);

  @Query("select distinct pr from Product pr "
      + "join fetch pr.boms b left join fetch b.part ")
  Optional<Product> findByIdWithParts(@Param("id") Long id);

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
}
