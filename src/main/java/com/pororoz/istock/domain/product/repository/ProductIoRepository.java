package com.pororoz.istock.domain.product.repository;

import com.pororoz.istock.domain.product.entity.ProductIo;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductIoRepository extends JpaRepository<ProductIo, Long> {

  @Query("select p from ProductIo p "
      + "left join fetch p.product "
      + "where p.superIo = :superIo")
  List<ProductIo> findBySuperIoWithProduct(@Param("superIo") ProductIo superIo);

  @Query(value = "select p from ProductIo p "
      + "left join fetch p.product "
      + "where cast(p.status as string) like %:status%"
      , countQuery = "select p from ProductIo p "
      + "where cast(p.status as string) like %:status%")
  Page<ProductIo> findByStatusContainingWithProduct(@Param("status") String status,
      Pageable pageable);
}
