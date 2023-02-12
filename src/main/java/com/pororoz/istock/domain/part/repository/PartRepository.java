package com.pororoz.istock.domain.part.repository;

import com.pororoz.istock.domain.part.entity.Part;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {
  Optional<Part> findByPartNameAndSpec(String partName, String spec);

}
