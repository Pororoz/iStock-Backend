package com.pororoz.istock.domain.log.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Log {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(updatable = false)
  private String api;

  @NotNull
  @Column(updatable = false)
  private String product;

  @NotNull
  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;
}
