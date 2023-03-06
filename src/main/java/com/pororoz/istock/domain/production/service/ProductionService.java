package com.pororoz.istock.domain.production.service;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.exception.ProductIoNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceRequest;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceResponse;
import com.pororoz.istock.domain.production.dto.service.UpdateProductionServiceResponse;
import com.pororoz.istock.domain.production.exception.ProductOrBomNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductionService {

  private final PartIoRepository partIoRepository;
  private final ProductRepository productRepository;
  private final ProductIoRepository productIoRepository;

  public SaveProductionServiceResponse saveWaitingProduction(SaveProductionServiceRequest request) {
    Product product = productRepository.findByIdWithPartsAndSubAssies(request.getProductId())
        .orElseThrow(ProductOrBomNotFoundException::new);
    ProductIo productIo = saveProductIo(request.getQuantity(), product);
    savePartIoAndSubAssyAll(request.getQuantity(), product.getBoms(), productIo);

    return SaveProductionServiceResponse.builder()
        .productId(productIo.getProduct().getId())
        .quantity(productIo.getQuantity())
        .build();
  }

  public UpdateProductionServiceResponse confirmProduction(Long productIoId) {
    ProductIo productIo = productIoRepository.findById(productIoId)
        .orElseThrow(ProductIoNotFoundException::new);
    productIo.getProduct().addStock(productIo.getQuantity());
    productIo.confirmProduction();
    productIo.getPartIoList().forEach(PartIo::confirmPartProduction);
    productIo.getSubAssyIoList().forEach(ProductIo::confirmSubAssyProduction);

    return UpdateProductionServiceResponse.builder()
        .productIoId(productIoId)
        .productId(productIo.getProduct().getId())
        .quantity(productIo.getQuantity()).build();
  }

  private ProductIo saveProductIo(long quantity, Product product) {
    ProductIo productIo = ProductIo.builder()
        .quantity(quantity)
        .status(ProductStatus.생산대기)
        .product(product).build();
    return productIoRepository.save(productIo);
  }

  private void savePartIoAndSubAssyAll(Long quantity, List<Bom> boms, ProductIo productIo) {
    List<PartIo> partIoList = new ArrayList<>();
    List<ProductIo> subAssyIoList = new ArrayList<>();

    for (Bom bom : boms) {
      if (Bom.SUB_ASSY_CODE_NUMBER.equals(bom.getCodeNumber())) {
        ProductIo subAssyIo = ProductIo.createSubAssyIo(bom, productIo, quantity,
            ProductStatus.사내출고대기);
        bom.getSubAssy().subtractStock(bom.getQuantity() * quantity);
        subAssyIoList.add(subAssyIo);
      } else {
        PartIo partIo = PartIo.createPartIo(bom, productIo, quantity, PartStatus.생산대기);
        bom.getPart().subtractStock(bom.getQuantity() * quantity);
        partIoList.add(partIo);
      }
    }
    partIoRepository.saveAll(partIoList);
    productIoRepository.saveAll(subAssyIoList);
  }
}
