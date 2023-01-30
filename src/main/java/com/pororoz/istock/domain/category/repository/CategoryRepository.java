package com.pororoz.istock.domain.category.repository;

import com.pororoz.istock.domain.category.entity.Category;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Page<Category> findAllByNameContaining(String name, Pageable pageable);

  List<Category> findAllByNameContaining(String name);

  Page<Category> findAllByNameContaining(Pageable pageable);
}
