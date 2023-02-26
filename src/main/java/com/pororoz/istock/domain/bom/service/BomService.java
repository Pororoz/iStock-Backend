package com.pororoz.istock.domain.bom.service;

import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.exception.BomNotFoundException;
import com.pororoz.istock.domain.bom.exception.DuplicateBomException;
import com.pororoz.istock.domain.bom.exception.InvalidProductBomException;
import com.pororoz.istock.domain.bom.exception.InvalidSubAssyBomException;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.Objects;
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

  private final String SUB_ASSY_CODE_NUMBER = "11";

  public BomServiceResponse saveBom(SaveBomServiceRequest request) {
    validateRequest(request.getCodeNumber(), request.getProductNumber(), request.getPartId());
    Part part = findPartById(request.getPartId());
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);
    checkDuplicateBom(request.getLocationNumber(), request.getProductId(), request.getPartId(),
        null);
    Bom result = bomRepository.save(request.toBom(part, product));
    return BomServiceResponse.of(result);
  }

  public BomServiceResponse deleteBom(Long bomId) {
    Bom result = bomRepository.findById(bomId).orElseThrow(BomNotFoundException::new);
    bomRepository.delete(result);
    return BomServiceResponse.of(result);
  }

  public BomServiceResponse updateBom(UpdateBomServiceRequest request) {
    validateRequest(request.getCodeNumber(), request.getProductNumber(), request.getPartId());
    Bom existBom = bomRepository.findById(request.getBomId())
        .orElseThrow(BomNotFoundException::new);
    Part part = findPartById(request.getPartId());
    Product newProduct = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);

    checkDuplicateBom(request.getLocationNumber(), request.getProductId(), request.getPartId(),
        existBom.getId());
    existBom.update(part, newProduct, request);
    return BomServiceResponse.of(existBom);
  }

  private void validateRequest(String codeNumber, String productNumber, Long partId) {
    if (Objects.equals(codeNumber, SUB_ASSY_CODE_NUMBER)) {
      validateSubAssyBom(productNumber, partId);
      return;
    }
    validateProductBom(productNumber, partId);
  }

  // sub assy는 또다른 sub assy의 bom이 될 수 없다.
  private void validateSubAssyBom(String productNumber, Long partId) {
    if (productNumber == null || partId != null) {
      throw new InvalidSubAssyBomException();
    }
    Product superProduct = productRepository.findByProductNumber(productNumber)
        .orElseThrow(ProductNotFoundException::new);
    if (Objects.equals(superProduct.getCodeNumber(), SUB_ASSY_CODE_NUMBER)) {
      throw new InvalidSubAssyBomException();
    }
  }

  private void validateProductBom(String productNumber, Long partId) {
    if (productNumber != null || partId == null) {
      throw new InvalidProductBomException();
    }
  }

  private Part findPartById(Long partId) {
    return partId == null ? null
        : partRepository.findById(partId).orElseThrow(PartNotFoundException::new);
  }

  // 인덱스로 묶여진 3가지 요소가 다를 때, 이미 해당 키로 구성된 요소가 있으면 예외가 발생한다.
  private void checkDuplicateBom(String locationNumber, Long productId, Long partId,
      Long existBomId) {
    bomRepository.findByLocationNumberAndProductIdAndPartId(locationNumber, productId, partId)
        .ifPresent(bom -> {
          if (existBomId == null || !existBomId.equals(bom.getId())) {
            throw new DuplicateBomException();
          }
        });
  }
}
