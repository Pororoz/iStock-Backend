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
      + "left join fetch p.boms b left join fetch b.part "
      + "where p.category.id = :categoryId "
      + "order by p.id, b.id",
      countQuery = "select count(p) from Product p where p.category.id = :categoryId")
  Page<Product> findProductsWithParts(Pageable pageable, @Param("categoryId") Long categoryId);

  @Query(value = "select p from Product p "
      + "left join fetch p.boms b left join fetch b.part "
      + "where p.category.id = :categoryId "
      + "order by p.id, b.id")
  List<Product> findProductsWithParts(@Param("categoryId") Long categoryId);
}
