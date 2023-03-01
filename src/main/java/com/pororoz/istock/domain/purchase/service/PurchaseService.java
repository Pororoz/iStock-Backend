package com.pororoz.istock.domain.purchase.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseService {

  private final ProductRepository productRepository;
  private final BomRepository bomRepository;
  private final ProductIoRepository productIoRepository;
  private final PartIoRepository partIoRepository;

  private final String SUB_ASSY_CODE_NUMBER = "11";

  public PurchaseProductServiceResponse purchaseProduct(PurchaseProductServiceRequest request) {
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);
    ProductIo productIo = productIoRepository.save(ProductIo.builder()
        .quantity(request.getQuantity())
        .status(ProductStatus.valueOf("구매대기"))
        .product(product)
        .build());
    List<Bom> boms = bomRepository.findByProductId(request.getProductId());
    savePartIoAndSubAssyIoAll(request.getQuantity(), productIo, boms);

    return PurchaseProductServiceResponse.of(request);
  }

  void savePartIoAndSubAssyIoAll(Long quantity, ProductIo productIo, List<Bom> boms) {
    List<PartIo> partIoList = new ArrayList<>();
    List<ProductIo> subAssyIoList = new ArrayList<>();

    for (Bom bom : boms) {
      if (SUB_ASSY_CODE_NUMBER.equals(bom.getCodeNumber())) {
        ProductIo subAssyIo = ProductIo.createSubAssyIo(bom, productIo, quantity,
            ProductStatus.외주구매대기);
        subAssyIoList.add(subAssyIo);
      } else {
        PartIo partIo = PartIo.createPartIo(bom, productIo, quantity, PartStatus.구매대기);
        partIoList.add(partIo);
      }
    }
    partIoRepository.saveAll(partIoList);
    productIoRepository.saveAll(subAssyIoList);
  }
}
