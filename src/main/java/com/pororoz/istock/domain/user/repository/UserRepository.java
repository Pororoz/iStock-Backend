package com.pororoz.istock.domain.user.repository;

import com.pororoz.istock.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // JPA Query Method
    Optional<User> findByUsername(String username);

}
