package com.pororoz.istock.domain.category.repository;

import com.pororoz.istock.domain.category.entity.Category;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  PageImpl<Category> findAllByName(PageRequest pageRequest);
}
