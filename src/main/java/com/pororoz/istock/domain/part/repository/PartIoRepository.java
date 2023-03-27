package com.pororoz.istock.domain.part.repository;

import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.product.entity.ProductIo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartIoRepository extends JpaRepository<PartIo, Long> {

  List<PartIo> findByProductIo(ProductIo productIo);

  @Query("select pi from PartIo pi "
      + "left join fetch pi.part "
      + "where pi.productIo = :productIo")
  List<PartIo> findByProductIoWithPart(@Param("productIo") ProductIo productIo);
}
