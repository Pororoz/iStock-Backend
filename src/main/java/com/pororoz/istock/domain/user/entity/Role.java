package com.pororoz.istock.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor
@Data
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "role")
    private Collection<User> users;

    @Builder
    public Role(String name){
        this.name = name;
    }
}