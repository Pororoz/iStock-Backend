package com.pororoz.istock.domain.outbound.service;

import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceRequest;
import com.pororoz.istock.domain.outbound.dto.service.OutboundServiceResponse;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OutboundService {

  private final ProductRepository productRepository;
  private final ProductIoRepository productIoRepository;

  public OutboundServiceResponse outbound(OutboundServiceRequest request) {
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);
    ProductIo productIo = saveProductIo(request.getQuantity(), product);
    return OutboundServiceResponse.of(productIo);
  }

  private ProductIo saveProductIo(Long quantity, Product product) {
    ProductIo productIo = ProductIo.builder()
        .status(ProductStatus.출고대기)
        .quantity(quantity)
        .product(product)
        .build();
    return productIoRepository.save(productIo);
  }
}
