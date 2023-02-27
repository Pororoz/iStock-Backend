package com.pororoz.istock.domain.production.service;

import com.pororoz.istock.common.exception.BomAndSubAssyNotMatchedException;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceRequest;
import com.pororoz.istock.domain.production.dto.service.SaveProductionServiceResponse;
import com.pororoz.istock.domain.production.exception.ProductOrBomNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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

  private final String SUB_ASSY_CODE_NUMBER = "11";

  public SaveProductionServiceResponse saveWaitingProduction(SaveProductionServiceRequest request) {
    Product product = productRepository.findByIdWithParts(request.getProductId())
        .orElseThrow(ProductOrBomNotFoundException::new);
    ProductIo productIo = saveProductIo(request.getQuantity(), product);
    savePartIoAll(product.getBoms(), productIo);
    saveSubAssyIoAll(product.getBoms(), productIo);

    return SaveProductionServiceResponse.builder()
        .productId(productIo.getProduct().getId())
        .quantity(productIo.getQuantity())
        .build();
  }

  private ProductIo saveProductIo(long quantity, Product product) {
    ProductIo productIo = ProductIo.builder()
        .quantity(quantity)
        .status(ProductStatus.생산대기)
        .product(product).build();
    return productIoRepository.save(productIo);
  }

  private void savePartIoAll(List<Bom> boms, ProductIo productIo) {
    List<PartIo> partIoList = boms.stream()
        .filter(bom -> !Objects.equals(bom.getCodeNumber(), SUB_ASSY_CODE_NUMBER))
        .map(bom -> {
          Part part = bom.getPart();
          if (part == null) {
            throw new IllegalArgumentException(
                "Bom에 Part가 없습니다. bomId: " + bom.getId() + ", locationNumber: "
                    + bom.getLocationNumber() + ", productId: " + bom.getProduct().getId());
          }
          part.subtractStock(bom.getQuantity());
          return PartIo.builder()
              .quantity(bom.getQuantity())
              .status(PartStatus.생산대기)
              .part(part).productIo(productIo)
              .build();
        }).toList();

    partIoRepository.saveAll(partIoList);
  }

  private void saveSubAssyIoAll(List<Bom> boms, ProductIo productIo) {
    Map<String, Long> subAssyQuantityMap = boms.stream()
        .filter(bom -> Objects.equals(bom.getCodeNumber(), SUB_ASSY_CODE_NUMBER))
        .collect(Collectors.toMap(Bom::getProductNumber, Bom::getQuantity));

    List<ProductIo> subAssyIoList = findSubAssiesByProductNumberOrThrow(
        new ArrayList<>(subAssyQuantityMap.keySet()))
        .stream().map(subAssy -> {
          Long quantity = subAssyQuantityMap.get(subAssy.getProductNumber());
          subAssy.subtractStock(quantity);
          return ProductIo.builder()
              .status(ProductStatus.사내출고대기)
              .quantity(quantity)
              .product(subAssy).superIo(productIo)
              .build();
        }).toList();

    productIoRepository.saveAll(subAssyIoList);
  }

  private List<Product> findSubAssiesByProductNumberOrThrow(List<String> subAssyNumbers) {
    List<Product> subAssies = productRepository.findByProductNumberIn(subAssyNumbers);
    if (subAssies.size() != subAssyNumbers.size()) {
      throw new BomAndSubAssyNotMatchedException();
    }
    return subAssies;
  }
}
