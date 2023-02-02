package com.pororoz.istock.domain.product.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductIo extends TimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @PositiveOrZero
  @Column(columnDefinition = "INT(11) UNSIGNED")
  private long quantity;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(length = 100)
  private ProductStatus status;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Product product;
}
