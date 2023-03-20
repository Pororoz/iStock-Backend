package com.pororoz.istock.domain.purchase.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.exception.PartIoNotFoundException;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.InvalidSubAssyTypeException;
import com.pororoz.istock.domain.product.exception.ProductIoNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.UpdateSubAssyOutsourcingServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.UpdatePurchaseServiceResponse;
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
  private final PartRepository partRepository;
  private final BomRepository bomRepository;
  private final ProductIoRepository productIoRepository;
  private final PartIoRepository partIoRepository;

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

  public PurchasePartServiceResponse purchasePart(PurchasePartServiceRequest request) {
    Part part = partRepository.findById(request.getPartId())
        .orElseThrow(PartNotFoundException::new);
    partIoRepository.save(request.toPartIo(part));

    return PurchasePartServiceResponse.of(request);
  }

  public UpdatePurchaseServiceResponse confirmPurchasePart(Long partIoId) {
    PartIo partIo = partIoRepository.findById(partIoId)
        .orElseThrow(PartIoNotFoundException::new);

    partIo.confirmPurchase();
    partIo.getPart().addStock(partIo.getQuantity());

    checkAllPartAndSubAssy(partIo.getProductIo());

    return UpdatePurchaseServiceResponse.of(partIo);
  }

  public UpdatePurchaseServiceResponse cancelPurchasePart(Long partIoId) {
    PartIo partIo = partIoRepository.findById(partIoId)
        .orElseThrow(PartIoNotFoundException::new);

    partIo.cancelPurchase();

    return UpdatePurchaseServiceResponse.of(partIo);
  }

  public UpdateSubAssyOutsourcingServiceResponse confirmSubAssyOutsourcing(Long productIoId) {
    ProductIo productIo = productIoRepository.findById(productIoId)
        .orElseThrow(ProductIoNotFoundException::new);

    checkWhetherSubAssy(productIo);

    productIo.confirmSubAssyOutsourcing();
    productIo.getProduct().addStock(productIo.getQuantity());

    checkAllPartAndSubAssy(productIo.getSuperIo());

    return UpdateSubAssyOutsourcingServiceResponse.of(productIo);
  }

  public UpdateSubAssyOutsourcingServiceResponse cancelSubAssyOutsourcing(Long productIoId) {
    ProductIo productIo = productIoRepository.findById(productIoId)
        .orElseThrow(ProductIoNotFoundException::new);

    checkWhetherSubAssy(productIo);

    productIo.cancelSubAssyOutsourcing();

    return UpdateSubAssyOutsourcingServiceResponse.of(productIo);
  }

  private void savePartIoAndSubAssyIoAll(Long quantity, ProductIo productIo, List<Bom> boms) {
    List<PartIo> partIoList = new ArrayList<>();
    List<ProductIo> subAssyIoList = new ArrayList<>();

    for (Bom bom : boms) {
      if (Bom.SUB_ASSY_CODE_NUMBER.equals(bom.getCodeNumber())) {
        ProductIo subAssyIo = ProductIo.createSubAssyIo(bom, productIo, quantity,
            ProductStatus.외주생산대기);
        subAssyIoList.add(subAssyIo);
      } else {
        PartIo partIo = PartIo.createPartIo(bom, productIo, quantity, PartStatus.구매대기);
        partIoList.add(partIo);
      }
    }
    partIoRepository.saveAll(partIoList);
    productIoRepository.saveAll(subAssyIoList);
  }

  private void checkAllPartAndSubAssy(ProductIo productIo) {
    for (PartIo partIo : partIoRepository.findByProductIoWithPart(productIo)) {
      if (partIo.getStatus() != PartStatus.구매확정) {
        return;
      }
    }
    for (ProductIo subAssyIo : productIoRepository.findBySuperIoWithProduct(productIo)) {
      if (subAssyIo.getStatus() != ProductStatus.외주생산확정) {
        return;
      }
    }
    productIo.completeProductPurchase();
  }

  private void checkWhetherSubAssy(ProductIo productIo) {
    if (productIo.getSuperIo() == null) {
      throw new InvalidSubAssyTypeException();
    }
  }
}
