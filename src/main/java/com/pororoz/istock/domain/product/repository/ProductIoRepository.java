package com.pororoz.istock.domain.product.repository;

import com.pororoz.istock.domain.product.entity.ProductIo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductIoRepository extends JpaRepository<ProductIo, Long> {

  @Query("select pi from ProductIo pi "
      + "left join fetch pi.product "
      + "where pi.superIo = :superIo")
  List<ProductIo> findBySuperIoWithProduct(@Param("superIo") ProductIo superIo);
}
