package com.pororoz.istock.domain.bom.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.bom.dto.service.UpdateBomServiceRequest;
import com.pororoz.istock.domain.bom.exception.InvalidProductBomException;
import com.pororoz.istock.domain.bom.exception.InvalidSubAssyBomException;
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
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"location_number", "part_id", "product_id", "sub_assy_id"})
)
public class Bom extends TimeEntity {

  public static final String SUB_ASSY_CODE_NUMBER = "11";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(max = 255)
  @Column(name = "location_number",
      columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '0'")
  private String locationNumber;

  @Size(max = 20)
  @Column(name = "code_number")
  private String codeNumber;

  @NotNull
  @PositiveOrZero
  @Column(columnDefinition = "INT(11) UNSIGNED default 0")
  private long quantity;

  @Size(max = 50)
  private String memo;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "part_id")
  private Part part;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_assy_id")
  private Product subAssy;

  @Version
  private long version;

  @Builder
  public Bom(Long id, String locationNumber, String codeNumber, long quantity, String memo,
      Product product, Part part, Product subAssy) {
    validateParameters(codeNumber, part, subAssy);
    this.id = id;
    this.locationNumber = locationNumber == null ? "0" : locationNumber;
    this.codeNumber = codeNumber;
    this.quantity = quantity;
    this.memo = memo;
    this.product = product;
    this.part = part;
    this.subAssy = subAssy;
    if (product != null && !product.getBoms().contains(this)) {
      product.getBoms().add(this);
    }
  }

  public void update(Product product, Product subAssy, Part part, UpdateBomServiceRequest request) {
    validateParameters(request.getCodeNumber(), part, subAssy);
    if (product != null && this.product != product && !product.getBoms().contains(this)) {
      product.getBoms().add(this);
    }
    this.locationNumber = request.getLocationNumber();
    this.codeNumber = request.getCodeNumber();
    this.quantity = request.getQuantity();
    this.memo = request.getMemo();
    this.part = part;
    this.subAssy = subAssy;
    this.product = product;
  }

  private void validateParameters(String codeNumber, Part part, Product subAssy) {
    if (SUB_ASSY_CODE_NUMBER.equals(codeNumber)) {
      validateSubAssyBom(part, subAssy);
      return;
    }
    validateProductBom(part, subAssy);
  }

  private void validateSubAssyBom(Part part, Product subAssy) {
    if (subAssy == null || part != null) {
      throw new InvalidSubAssyBomException();
    }
  }

  private void validateProductBom(Part part, Product subAssy) {
    if (subAssy != null || part == null) {
      throw new InvalidProductBomException();
    }
  }
}
