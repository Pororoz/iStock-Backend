package com.pororoz.istock.domain.category.service;


import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.FindCategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.UpdateCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Transactional(readOnly = true)
  public Page<FindCategoryServiceResponse> findCategories(FindCategoryServiceRequest request,
      Pageable pageable) {
    if (request.getCategoryName() == null) {
      return categoryRepository.findAll(pageable).map(FindCategoryServiceResponse::of);
    }

    return categoryRepository.findAllByCategoryNameContaining(request.getCategoryName(), pageable)
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

  public CategoryServiceResponse deleteCategory(Long categoryId) {
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(CategoryNotFoundException::new);
    checkRelatedEntityAndThrow(category);
    categoryRepository.delete(category);
    return CategoryServiceResponse.of(category);
  }

  void checkRelatedEntityAndThrow(Category category) {
    if (productRepository.existsByCategory(category)) {
      throw new DataIntegrityViolationException(
          ExceptionMessage.CANNOT_DELETE + " 카테고리와 연관된 제품이 존재합니다.");
    }
  }
}
