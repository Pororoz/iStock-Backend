package com.pororoz.istock.domain.part.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.part.dto.service.UpdatePartServiceRequest;
import com.pororoz.istock.domain.production.exception.PartStockMinusException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
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
    @UniqueConstraint(columnNames = {"part_name", "spec"})
)
public class Part extends TimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(max = 100)
  @Column(name = "part_name", columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin")
  private String partName;

  @NotNull
  @Size(max = 100)
  @Column(columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin")
  private String spec;

  @NotNull
  @PositiveOrZero
  @Builder.Default
  @Column(columnDefinition = "INT(11) UNSIGNED default 0")
  private long price = 0;

  @NotNull
  @PositiveOrZero
  @Builder.Default
  @Column(columnDefinition = "INT(11) UNSIGNED default 0")
  private long stock = 0;

  @Version
  private Long version;

  public void update(UpdatePartServiceRequest request) {
    this.partName = request.getPartName();
    this.spec = request.getSpec();
    this.price = request.getPrice();
    this.stock = request.getStock();
  }

  public void addStock(long quantity) {
    if (quantity < 0) {
      throw new IllegalArgumentException("0 이상만 stock에 더할 수 있습니다.");
    }
    this.stock += quantity;
  }

  public void subtractStock(long quantity) {
    long subtract = this.stock - quantity;
    if (subtract < 0) {
      throw new PartStockMinusException(
          "id: " + id + ", 부품명: " + partName + ", 재고: " + stock + ", 요청 수량: " + quantity);
    }
    this.stock = subtract;
  }
}
