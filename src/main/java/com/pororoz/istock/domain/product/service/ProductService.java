package com.pororoz.istock.domain.product.service;

import com.pororoz.istock.common.utils.Pagination;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.dto.service.FindProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.FindProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNumberDuplicatedException;
import com.pororoz.istock.domain.product.exception.RegisteredAsSubAssyException;
import com.pororoz.istock.domain.product.exception.SubAssyBomExistException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

  private final BomRepository bomRepository;
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  private final String SUB_ASSY_CODE_NUMBER = "11";

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
    validateRequest(product, request.getProductNumber(), request.getCodeNumber());
    checkProductNumberDuplicated(product.getProductNumber(), request.getProductNumber());
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    product.update(request, category);
    return ProductServiceResponse.of(product);
  }

  public ProductServiceResponse deleteProduct(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(ProductNotFoundException::new);
    if (bomRepository.existsByProductNumber(product.getProductNumber())) {
      throw new RegisteredAsSubAssyException();
    }
    productRepository.delete(product);
    return ProductServiceResponse.of(product);
  }

  @Transactional(readOnly = true)
  public Page<FindProductServiceResponse> findProducts(FindProductServiceRequest request) {
    categoryRepository.findById(request.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    Page<Product> products = getProductPage(request);
    //subAssy만 필터링
    products.forEach(product -> product.setBoms(
        product.getBoms().stream()
            .filter(bom -> Objects.equals(bom.getCodeNumber(), SUB_ASSY_CODE_NUMBER)).toList()));
    return products.map(FindProductServiceResponse::of);
  }


  private void validateRequest(Product existProduct, String newProductNumber,
      String newCodeNumber) {
    if (Objects.equals(existProduct.getProductNumber(), newProductNumber)) {
      return;
    }
    // product->subassy
    // bom에 subassy가 있으면 안된다.
    if (Objects.equals(newCodeNumber, SUB_ASSY_CODE_NUMBER)) {
      bomRepository.findByProductId(existProduct.getId()).forEach(bom -> {
        if (Objects.equals(bom.getCodeNumber(), SUB_ASSY_CODE_NUMBER)) {
          throw new SubAssyBomExistException();
        }
      });
      return;
    }
    // subassy->product
    // bom에 있으면 안된다
    if (bomRepository.existsByProductNumber(existProduct.getProductNumber())) {
      throw new RegisteredAsSubAssyException();
    }
  }

  private void checkProductNumberDuplicated(String oldNumber, String newNumber) {
    if (Objects.equals(oldNumber, newNumber)) {
      return;
    }
    productRepository.findByProductNumber(newNumber).ifPresent(p -> {
      throw new ProductNumberDuplicatedException();
    });
  }

  private Page<Product> getProductPage(FindProductServiceRequest request) {
    if (request.getPage() == null && request.getSize() == null) {
      List<Product> products = productRepository.findProductsWithBoms(
          request.getCategoryId());
      return new PageImpl<>(products);
    }
    return productRepository.findProductsWithBoms(
        Pagination.toPageRequest(request.getPage(), request.getSize()), request.getCategoryId());
  }
}
