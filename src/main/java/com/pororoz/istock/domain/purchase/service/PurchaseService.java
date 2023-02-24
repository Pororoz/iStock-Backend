package com.pororoz.istock.domain.purchase.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchasePartServiceResponse;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseProductServiceResponse;
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
  private final PartRepository partRepository;
  private final ProductIoRepository productIoRepository;
  private final PartIoRepository partIoRepository;

  public PurchaseProductServiceResponse purchaseProduct(PurchaseProductServiceRequest request) {
    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);
    ProductIo productIo = productIoRepository.save(request.toProductIo(product));

    List<Bom> boms = bomRepository.findByProductId(request.getProductId());
    boms.forEach(bom -> {
          Part part = partRepository.findById(bom.getPart().getId())
              .orElseThrow(PartNotFoundException::new);
          partIoRepository.save(request.toPartIo(part, productIo));
        }
    );

    return PurchaseProductServiceResponse.of(request);
  }

  public PurchasePartServiceResponse purchasePart(PurchasePartServiceRequest request) {
    Part part = partRepository.findById(request.getPartId())
        .orElseThrow(PartNotFoundException::new);
    partIoRepository.save(request.toPartIo(part));

    return PurchasePartServiceResponse.of(request);
  }
}
