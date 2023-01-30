package com.pororoz.istock.domain.category.service;

import com.pororoz.istock.domain.category.dto.service.CategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.DeleteCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.GetCategoryServiceRequest;
import com.pororoz.istock.domain.category.dto.service.GetCategoryServiceResponse;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Page<GetCategoryServiceResponse> findCategories(GetCategoryServiceRequest request) {
    if (request.getPage() == null && request.getSize() == null) {
      if (request.getName() == null) {
        List<Category> categories = categoryRepository.findAll();
        return new PageImpl<>(categories).map(GetCategoryServiceResponse::of);
      }

      List<Category> categories = categoryRepository.findAllByNameContaining(request.getName());
      return new PageImpl<>(categories).map(GetCategoryServiceResponse::of);
    }

    if (request.getName() == null) {
      return categoryRepository.findAllByNameContaining(request.toPageRequest())
          .map(GetCategoryServiceResponse::of);
    }

    return categoryRepository.findAllByNameContaining(request.getName(), request.toPageRequest())
        .map(GetCategoryServiceResponse::of);
  }

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
