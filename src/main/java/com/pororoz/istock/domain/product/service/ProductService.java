package com.pororoz.istock.domain.product.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.product.dto.service.FindProductByPartServiceRequest;
import com.pororoz.istock.domain.product.dto.service.FindProductWithSubAssyServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNumberDuplicatedException;
import com.pororoz.istock.domain.product.exception.RegisteredAsSubAssyException;
import com.pororoz.istock.domain.product.exception.SubAssyBomExistException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

  private final BomRepository bomRepository;
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
    validateRequest(product, request.getProductNumber(), request.getCodeNumber());
    checkProductNumberDuplicated(product.getProductNumber(), request.getProductNumber());
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(CategoryNotFoundException::new);
    product.update(request, category);
    return ProductServiceResponse.of(product);
  }

  // ?????? BOM??? ???????????? ????????? ?????????????
  public ProductServiceResponse deleteProduct(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(ProductNotFoundException::new);
    if (bomRepository.existsByProductId(product.getId())) {
      throw new RegisteredAsSubAssyException();
    }
    productRepository.delete(product);
    return ProductServiceResponse.of(product);
  }

  @Transactional(readOnly = true)
  public Page<FindProductWithSubAssyServiceResponse> findProductsWithSubAssies(Long categoryId,
      Pageable pageable) {
    categoryRepository.findById(categoryId)
        .orElseThrow(CategoryNotFoundException::new);
    Page<Product> products = productRepository.findByCategoryIdWithSubAssies(pageable, categoryId);
    return products.map(FindProductWithSubAssyServiceResponse::of);
  }

  @Transactional(readOnly = true)
  public Page<ProductServiceResponse> findProductsByPart(FindProductByPartServiceRequest request,
      Pageable pageable) {
    Page<Product> products = productRepository.findByPartIdAndPartNameIgnoreNull(
        request.getPartId(), request.getPartName(), pageable);
    return products.map(ProductServiceResponse::of);
  }


  private void validateRequest(Product existProduct, String newProductNumber,
      String newCodeNumber) {
    if (Objects.equals(existProduct.getProductNumber(), newProductNumber)) {
      return;
    }
    // product->subassy
    // bom??? subassy??? ????????? ?????????.
    if (Bom.SUB_ASSY_CODE_NUMBER.equals(newCodeNumber)) {
      bomRepository.findByProductId(existProduct.getId()).forEach(bom -> {
        if (Bom.SUB_ASSY_CODE_NUMBER.equals(bom.getCodeNumber())) {
          throw new SubAssyBomExistException();
        }
      });
    }
    // subassy->product
    // bom??? ????????? ?????????
    else if (Bom.SUB_ASSY_CODE_NUMBER.equals(existProduct.getCodeNumber())
        && bomRepository.existsByProductId(existProduct.getId())) {
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
}
