package com.pororoz.istock.domain.category.service;


import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.DeleteCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryServiceResponse saveCategory(SaveCategoryServiceRequest request) {
    Category category = request.toCategory(request.getName());
    Category result = categoryRepository.save(category);
    return CategoryServiceResponse.of(result);
  }

  public CategoryServiceResponse deleteCategory(DeleteCategoryServiceRequest request) {
    Category category = categoryRepository.findById(request.getId())
        .orElseThrow(CategoryNotFoundException::new);
    categoryRepository.deleteById(request.getId());
    return CategoryServiceResponse.of(category);
  }

  public CategoryServiceResponse updateCategory(UpdateCategoryServiceRequest request) {
    Category category = categoryRepository.findById(request.getId())
        .orElseThrow(CategoryNotFoundException::new);
    category.update(request.getName());
    return CategoryServiceResponse.of(category);
  }
}
