package com.pororoz.istock.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @NotNull
    @Size(min = 4, max = 100)
    @Column(nullable = false)
    private String password;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
}
