package com.pororoz.istock.domain.bom.service;

import com.pororoz.istock.domain.bom.dto.service.BomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.FindBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.FindBomServiceResponse;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.exception.BomNotFoundException;
import com.pororoz.istock.domain.bom.exception.BomSubAssyNumberDuplicatedException;
import com.pororoz.istock.domain.bom.exception.DuplicateBomException;
import com.pororoz.istock.domain.bom.exception.InvalidProductBomException;
import com.pororoz.istock.domain.bom.exception.InvalidSubAssyBomException;
import com.pororoz.istock.domain.bom.exception.SubAssyCannotHaveSubAssyException;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  @Transactional(readOnly = true)
  public Page<FindBomServiceResponse> findBomList(FindBomServiceRequest request,
      Pageable pageable) {
    productRepository.findById(request.getProductId()).orElseThrow(ProductNotFoundException::new);
    return bomRepository.findByProductIdWithPart(pageable, request.getProductId())
        .map(FindBomServiceResponse::of);
  }

  public BomServiceResponse saveBom(SaveBomServiceRequest request) {
    validateRequest(request.getCodeNumber(), request.getPartId(), request.getSubAssyId());
    Part part = findPartById(request.getPartId());
    Product subAssy = findSubAssyById(request.getSubAssyId());
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);
    checkDuplicateBom(request.getLocationNumber(), request.getProductId(), request.getSubAssyId(),
        request.getPartId(), null);
    if (SUB_ASSY_CODE_NUMBER.equals(request.getCodeNumber())) {
      if (SUB_ASSY_CODE_NUMBER.equals(product.getCodeNumber())) {
        throw new SubAssyCannotHaveSubAssyException();
      }
      checkDuplicateSubAssy(request.getProductId(), request.getSubAssyId(), null);
    }
    Bom result = bomRepository.save(request.toBom(product, subAssy, part));
    return BomServiceResponse.of(result);
  }

  public BomServiceResponse deleteBom(long bomId) {
    Bom result = bomRepository.findById(bomId).orElseThrow(BomNotFoundException::new);
    bomRepository.delete(result);
    return BomServiceResponse.of(result);
  }

  public BomServiceResponse updateBom(UpdateBomServiceRequest request) {
    validateRequest(request.getCodeNumber(), request.getPartId(), request.getSubAssyId());
    Bom existBom = bomRepository.findById(request.getBomId())
        .orElseThrow(BomNotFoundException::new);
    Part part = findPartById(request.getPartId());
    Product subAssy = findSubAssyById(request.getSubAssyId());
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);
    checkDuplicateBom(request.getLocationNumber(), request.getProductId(), request.getSubAssyId(),
        request.getPartId(), existBom.getId());
    if (SUB_ASSY_CODE_NUMBER.equals(request.getCodeNumber())) {
      if (SUB_ASSY_CODE_NUMBER.equals(product.getCodeNumber())) {
        throw new SubAssyCannotHaveSubAssyException();
      }
      // subAssy가 바뀔 경우 중복된 subAssy가 있는지 확인
      if (!subAssy.equals(existBom.getSubAssy())) {
        checkDuplicateSubAssy(request.getProductId(), request.getSubAssyId(), existBom.getId());
      }
    }
    existBom.update(product, subAssy, part, request);
    return BomServiceResponse.of(existBom);
  }

  private void validateRequest(String codeNumber, Long partId, Long subAssyId) {
    if (SUB_ASSY_CODE_NUMBER.equals(codeNumber)) {
      validateSubAssyBom(partId, subAssyId);
      return;
    }
    validateProductBom(partId, subAssyId);
  }

  // product number는 sub assy의 품명이어야 한다.
  private void validateSubAssyBom(Long partId, Long subAssyId) {
    if (subAssyId == null || partId != null) {
      throw new InvalidSubAssyBomException();
    }
  }

  private void validateProductBom(Long partId, Long subAssyId) {
    if (subAssyId != null || partId == null) {
      throw new InvalidProductBomException();
    }
  }

  private Part findPartById(Long partId) {
    return partId == null ? null
        : partRepository.findById(partId).orElseThrow(PartNotFoundException::new);
  }

  private Product findSubAssyById(Long subAssyId) {
    if (subAssyId == null) {
      return null;
    }
    Product subAssy = productRepository.findById(subAssyId)
        .orElseThrow(ProductNotFoundException::new);
    if (!SUB_ASSY_CODE_NUMBER.equals(subAssy.getCodeNumber())) {
      throw new InvalidSubAssyBomException();
    }
    return subAssy;
  }

  // 인덱스로 묶여진 4가지 요소가 다를 때, 이미 해당 키로 구성된 요소가 있으면 예외가 발생한다.
  private void checkDuplicateBom(String locationNumber, Long productId, Long subAssyId, Long partId,
      Long existBomId) {
    bomRepository.findByLocationNumberAndProductIdAndSubAssyIdAndPartId(locationNumber,
        productId, subAssyId, partId).ifPresent(bom -> {
      if (existBomId == null || !existBomId.equals(bom.getId())) {
        throw new DuplicateBomException();
      }
    });
  }

  //하나에 제품 내에서 bom의 sub assy가 겹치면 안된다.
  private void checkDuplicateSubAssy(Long productId, Long subAssyId, Long existBomId) {
    bomRepository.findByProductIdAndSubAssyId(productId, subAssyId).ifPresent(bom -> {
      if (existBomId == null || !existBomId.equals(bom.getId())) {
        throw new BomSubAssyNumberDuplicatedException();
      }
    });
  }
}
