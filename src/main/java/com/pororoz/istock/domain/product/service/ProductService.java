package com.pororoz.istock.domain.product.service;

import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNumberDuplicatedException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public ProductServiceResponse saveProduct(SaveProductServiceRequest request) {
    checkProductNumberDuplicated(null, request.getProductNumber());
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    Product product = productRepository.save(request.toProduct(category));
    return ProductServiceResponse.of(product);
  }

  public ProductServiceResponse updateProduct(UpdateProductServiceRequest request) {
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);
    checkProductNumberDuplicated(product.getProductNumber(), request.getProductNumber());
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    product.update(request, category);
    return ProductServiceResponse.of(product);
  }

  public ProductServiceResponse deleteProduct(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(ProductNotFoundException::new);
    productRepository.delete(product);
    return ProductServiceResponse.of(product);
  }

  private void checkProductNumberDuplicated(String oldNumber, String newNumber) {
    if (Objects.equals(oldNumber, newNumber)) {
      return;
    }
    productRepository.findByProductNumber(newNumber).ifPresent(p -> {
      throw new ProductNumberDuplicatedException();
    });
  }
}
