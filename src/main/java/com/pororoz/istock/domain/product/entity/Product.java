package com.pororoz.istock.domain.product.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.category.entity.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
  private String name;

  @NotNull
  @Size(min = 1, max = 200)
  private String productNumber;

  @Size(max = 20)
  private String codeNumber;

  @NotNull
  @PositiveOrZero
  @Builder.Default
  @ColumnDefault("0")
  private Long stock = 0L;

  @Size(max = 50)
  private String companyName;

  @ManyToOne(fetch = FetchType.LAZY)
  private Category category;
}
