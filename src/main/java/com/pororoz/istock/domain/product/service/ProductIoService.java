package com.pororoz.istock.domain.product.service;

import com.pororoz.istock.domain.product.dto.service.FindProductIoServiceResponse;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductIoService {

  private final ProductIoRepository productIoRepository;

  @Transactional(readOnly = true)
  public Page<FindProductIoServiceResponse> findProductIo(String status, Pageable pageable) {
    Page<ProductIo> productIoPage = productIoRepository.findByStatusContainingWithProduct(
        status, pageable);
    return productIoPage.map(FindProductIoServiceResponse::of);
  }
}
