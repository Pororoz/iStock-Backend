package com.pororoz.istock.domain.product.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.production.exception.ProductStockMinusException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends TimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(max = 100)
  @Column(columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin")
  private String productName;

  @NotNull
  @Size(max = 100)
  @Column(unique = true, columnDefinition = "VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin")
  private String productNumber;

  @Size(max = 20)
  private String codeNumber;

  @NotNull
  @PositiveOrZero
  @Builder.Default
  @Column(columnDefinition = "INT(11) UNSIGNED default 0")
  private long stock = 0;

  @Size(max = 50)
  @Column(columnDefinition = "VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_bin")
  private String companyName;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Category category;

  @OneToMany(mappedBy = "product")
  @Builder.Default
  private List<Bom> boms = new ArrayList<>();

  public void update(UpdateProductServiceRequest request, Category category) {
    this.productName = request.getProductName();
    this.productNumber = request.getProductNumber();
    this.codeNumber = request.getCodeNumber();
    this.stock = request.getStock();
    this.companyName = request.getCompanyName();
    this.category = category;
  }

  public void subtractStock(long quantity) {
    long subtract = this.stock - quantity;
    if (subtract < 0) {
      throw new ProductStockMinusException(
          "id: " + id + ", 품번: " + productNumber + ", 재고: " + stock + ", 요청 수량: " + quantity);
    }
    this.stock = subtract;
  }
}
