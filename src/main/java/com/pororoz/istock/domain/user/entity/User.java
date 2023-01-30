package com.pororoz.istock.domain.user.entity;

import com.pororoz.istock.common.entity.TimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends TimeEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 2, max = 20)
  @Column(length = 50, unique = true, nullable = false)
  private String username;

  @NotNull
  @Size(min = 2, max = 100)
  @Column(nullable = false)
  private String password;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Role role;

  public void update(String password, Role role) {
    this.password = password;
    this.role = role;
  }
}
