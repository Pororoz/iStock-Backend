package com.pororoz.istock.domain.part.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.production.exception.ChangeProductionStatusException;
import com.pororoz.istock.domain.purchase.exception.ChangePurchaseStatusException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PartIo extends TimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Positive(message = "partIo의 수량은 1 이상이어야 합니다. BOM과 요청 수량을 확인해주세요")
  @Column(columnDefinition = "INT(11) UNSIGNED")
  private long quantity;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(length = 100)
  private PartStatus status;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Part part;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_io_id")
  private ProductIo productIo;

  @Builder
  public PartIo(Long id, long quantity, PartStatus status, Part part, ProductIo productIo) {
    this.id = id;
    this.quantity = quantity;
    this.status = status;
    this.part = part;
    this.productIo = productIo;
    if (productIo != null) {
      productIo.getPartIoList().add(this);
    }
  }

  public static PartIo createPartIo(Bom bom, ProductIo productIo, Long quantity,
      PartStatus status) {
    if (bom.getPart() == null) {
      throw new IllegalArgumentException(
          "BOM에 Part가 없습니다. bomId: " + bom.getId() + ", locationNumber: "
              + bom.getLocationNumber() + ", productId: " + bom.getProduct().getId());
    }
    return PartIo.builder()
        .part(bom.getPart())
        .quantity(bom.getQuantity() * quantity)
        .status(status)
        .productIo(productIo).build();
  }

  public void confirmPurchase() {
    if (this.status != PartStatus.구매대기) {
      throw new ChangePurchaseStatusException(PartStatus.구매대기.name(), PartStatus.구매확정.name(),
          "id: " + this.id + ", 상태: " + this.status);
    }
    this.status = PartStatus.구매확정;
  }

  public void cancelPurchase() {
    if (this.status != PartStatus.구매대기) {
      throw new ChangePurchaseStatusException(PartStatus.구매대기.name(), PartStatus.구매취소.name(),
          "id: " + this.id + ", 상태: " + this.status);
    }
    this.status = PartStatus.구매취소;
  }

  public void confirmPartProduction() {
    if (this.status != PartStatus.생산대기) {
      throw new ChangeProductionStatusException(PartStatus.생산대기.name(), PartStatus.생산완료.name(),
          "id: " + this.id + ", 상태: " + this.status);
    }
    this.status = PartStatus.생산완료;
  }

  public void cancelPartProduction() {
    if (this.status != PartStatus.생산대기) {
      throw new ChangeProductionStatusException(PartStatus.생산대기.name(), PartStatus.생산취소.name(),
          "id: " + this.id + ", 상태: " + this.status);
    }
    this.status = PartStatus.생산취소;
  }
}
