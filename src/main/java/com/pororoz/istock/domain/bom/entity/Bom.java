package com.pororoz.istock.domain.bom.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
  @Size(max = 200)
  @Column(name = "location_number")
  private String locationNumber;

  @NotNull
  @Size(max = 20)
  @Column(name = "code_number")
  private String codeNumber;

  @NotNull
  @Positive
  @Column(columnDefinition = "INT(11) UNSIGNED")
  private long quantity;

  @Size(max = 50)
  private String memo;

  @ManyToOne(fetch = FetchType.LAZY)
  private Part part;

  @ManyToOne(fetch = FetchType.LAZY)
  private Product product;
}
