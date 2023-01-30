package com.pororoz.istock.domain.category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateTimeConverter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty
  @Size(max = 15)
  @Column(unique = true, nullable = false)
  private String name;

  @CreatedDate
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime updatedAt;
}
