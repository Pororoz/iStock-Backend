package com.pororoz.istock.domain.product.repository;

import com.pororoz.istock.domain.product.entity.ProductIo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductIoRepository extends JpaRepository<ProductIo, Long> {

  @Query("select distinct p from ProductIo p "
      + "join fetch p.product "
      + "left join fetch p.subAssyIoList "
      + "left join fetch p.partIoList")
  Optional<ProductIo> findByIdWithProductAndSubAssyIoAndPartIo(Long id);
}
