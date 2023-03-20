package com.pororoz.istock.domain.category.repository;

import com.pororoz.istock.domain.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Page<Category> findAllByCategoryNameContaining(String categoryName, Pageable pageable);

  Optional<Category> findByCategoryName(String categoryName);
}
