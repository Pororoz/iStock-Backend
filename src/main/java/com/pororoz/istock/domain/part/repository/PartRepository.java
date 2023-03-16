package com.pororoz.istock.domain.part.repository;

import com.pororoz.istock.domain.part.entity.Part;
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
}
