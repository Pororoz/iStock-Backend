package com.pororoz.istock.domain.bom.service;

import com.pororoz.istock.domain.bom.dto.service.DeleteBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.exception.BomNotFoundException;
import com.pororoz.istock.domain.bom.exception.DuplicateBomException;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BomService {

  private final PartRepository partRepository;
  private final ProductRepository productRepository;
  private final BomRepository bomRepository;

  public BomServiceResponse saveBom(SaveBomServiceRequest request) {
    Part part = partRepository.findById(request.getPartId())
        .orElseThrow(PartNotFoundException::new);
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);
    bomRepository.findByLocationNumberAndProductIdAndPartId(request.getLocationNumber(),
        request.getProductId(), request.getPartId()).ifPresent(p -> {
      throw new DuplicateBomException();
    });
    Bom result = bomRepository.save(request.toBom(part, product));
    return BomServiceResponse.of(result);
  }

  public BomServiceResponse deleteBom(DeleteBomServiceRequest request) {
    Bom result = bomRepository.findById(request.getBomId()).orElseThrow(BomNotFoundException::new);
    bomRepository.delete(result);
    return BomServiceResponse.of(result);
  }

  public BomServiceResponse updateBom(UpdateBomServiceRequest request) {
    Bom bom = bomRepository.findById(request.getBomId()).orElseThrow(BomNotFoundException::new);
    Part newPart = partRepository.findById(request.getPartId())
        .orElseThrow(PartNotFoundException::new);
    Product newProduct = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);

    // 인덱스로 묶여진 3가지 요소가 다를 때, 이미 해당 키로 구성된 요소가 있으면 예외처리를 해줘야 한다.
    bomRepository.findByLocationNumberAndProductIdAndPartId(request.getLocationNumber(),
        request.getProductId(), request.getPartId()).ifPresent(p -> {
      if (!p.equals(bom)) {
        throw new DuplicateBomException();
      }
    });

    bom.update(newPart, newProduct, request);
    return BomServiceResponse.of(bom);
  }
}
