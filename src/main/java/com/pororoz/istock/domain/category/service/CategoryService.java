package com.pororoz.istock.domain.category.service;


import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.DeleteCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryServiceResponse saveCategory(
      SaveCategoryServiceRequest saveCategoryServiceRequest) {
    Category category = saveCategoryServiceRequest.toCategory(saveCategoryServiceRequest.getName());
    Category result = categoryRepository.save(category);
    return CategoryServiceResponse.of(result);
  }

  public CategoryServiceResponse deleteCategory(
      DeleteCategoryServiceRequest deleteCategoryServiceRequest) {
    Category category = categoryRepository.findById(deleteCategoryServiceRequest.getId())
        .orElseThrow(CategoryNotFoundException::new);
    categoryRepository.deleteById(deleteCategoryServiceRequest.getId());
    return CategoryServiceResponse.of(category);
  }
}
