package com.pororoz.istock.domain.category.service;


import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.DeleteCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

  private final CategoryRepository categoryRepository;

  @Transactional(readOnly=true)
  public Page<FindCategoryServiceResponse> findCategories(FindCategoryServiceRequest request) {
    if (request.getCategoryName() == null) {
      return categoryRepository.findAll(request.toPageRequest())
          .map(FindCategoryServiceResponse::of);
    }

    return categoryRepository.findAllByCategoryNameContaining(request.getCategoryName(), request.toPageRequest())
        .map(FindCategoryServiceResponse::of);
  }

  public CategoryServiceResponse saveCategory(
      SaveCategoryServiceRequest saveCategoryServiceRequest) {
    Category category = saveCategoryServiceRequest.toCategory();
    Category result = categoryRepository.save(category);
    return CategoryServiceResponse.of(result);
  }

  public CategoryServiceResponse updateCategory(UpdateCategoryServiceRequest request) {
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    category.update(request.getCategoryName());
    return CategoryServiceResponse.of(category);
  }

  public CategoryServiceResponse deleteCategory(
      DeleteCategoryServiceRequest deleteCategoryServiceRequest) {
    Category category = categoryRepository.findById(deleteCategoryServiceRequest.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    categoryRepository.deleteById(deleteCategoryServiceRequest.getCategoryId());
    return CategoryServiceResponse.of(category);
  }
}
