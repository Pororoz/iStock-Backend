package com.pororoz.istock.domain.bom.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"location_number", "part_id", "product_id"})
)
public class Bom extends TimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(max = 100)
  @Builder.Default
  @Column(name = "location_number",
      columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '0'")
  private String locationNumber = "0";

  @Size(max = 20)
  @Column(name = "code_number")
  private String codeNumber;

  @NotNull
  @PositiveOrZero
  @Builder.Default
  @Column(columnDefinition = "INT(11) UNSIGNED default 0")
  private long quantity = 0;

  @Size(max = 100)
  @Column(columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin")
  private String productNumber;

  @Size(max = 50)
  private String memo;

  @ManyToOne(fetch = FetchType.LAZY)
  private Part part;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  public void update(Part part, Product product, UpdateBomServiceRequest request) {
    this.locationNumber = request.getLocationNumber();
    this.productNumber = request.getProductNumber();
    this.codeNumber = request.getCodeNumber();
    this.quantity = request.getQuantity();
    this.memo = request.getMemo();
    this.part = part;
    this.product = product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }
}
