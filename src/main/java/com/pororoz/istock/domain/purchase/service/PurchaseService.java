package com.pororoz.istock.domain.purchase.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseBulkServiceRequest;
import com.pororoz.istock.domain.purchase.dto.service.PurchaseBulkServiceResponse;
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
  private final PartIoRepository partIoRepository;

  public PurchaseBulkServiceResponse purchaseBulk(PurchaseBulkServiceRequest request) {
    productRepository.findById(request.getProductId())
        .orElseThrow(ProductNotFoundException::new);
    List<Bom> boms = bomRepository.findByProductId(request.getProductId());
    boms.forEach(bom -> {
      Part part =partRepository.findById(bom.getPart().getId())
          .orElseThrow(PartNotFoundException::new);
      partIoRepository.save(request.toPartIo(part));
        }
    );
    return PurchaseBulkServiceResponse.of(request);
  }
}
