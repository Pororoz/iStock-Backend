package com.pororoz.istock.domain.product.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.part.entity.PartIo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductIo extends TimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Positive(message = "productIo의 수량은 1 이상이어야 합니다. BOM과 요청 수량을 확인해주세요")
  @Column(columnDefinition = "INT(11) UNSIGNED")
  private long quantity;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(length = 100)
  private ProductStatus status;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  private ProductIo superIo;

  @OneToMany(mappedBy = "productIo")
  private List<PartIo> partIoList;

  @OneToMany(mappedBy = "superIo")
  private List<ProductIo> subAssyIoList;

  @Builder
  public ProductIo(Long id, long quantity, ProductStatus status, Product product, ProductIo superIo,
      List<PartIo> partIoList, List<ProductIo> subAssyIoList) {
    this.id = id;
    this.quantity = quantity;
    this.status = status;
    this.product = product;
    this.superIo = superIo;
    this.partIoList = partIoList == null ? new ArrayList<>() : partIoList;
    this.subAssyIoList = subAssyIoList == null ? new ArrayList<>() : subAssyIoList;
    if (superIo != null) {
      superIo.getSubAssyIoList().add(this);
    }
  }

  public static ProductIo createSubAssyIo(
      Bom bom, ProductIo superIo, Long quantity, ProductStatus status) {
    if (bom.getSubAssy() == null) {
      throw new IllegalArgumentException(
          "BOM에 SubAssy가 없습니다. bomId: " + bom.getId() + ", locationNumber: "
              + bom.getLocationNumber() + ", productId: " + bom.getProduct().getId());
    }
    return ProductIo.builder()
        .product(bom.getSubAssy())
        .quantity(bom.getQuantity() * quantity)
        .status(status)
        .superIo(superIo).build();
  }
}
