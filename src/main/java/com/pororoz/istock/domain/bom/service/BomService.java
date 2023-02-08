package com.pororoz.istock.domain.bom.service;

import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceResponse;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.exception.NotExistedPart;
import com.pororoz.istock.domain.bom.exception.NotExistedProduct;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BomService {

  private final PartRepository partRepository;
  private final ProductRepository productRepository;
  private final BomRepository bomRepository;

  public SaveBomServiceResponse saveBom(SaveBomServiceRequest request) {
    Part part = partRepository.findById(request.getPartId())
        .orElseThrow(NotExistedPart::new);
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(NotExistedProduct::new);
    Bom result = bomRepository.save(request.toBom(part, product));
    return SaveBomServiceResponse.of(result);
  }
}
