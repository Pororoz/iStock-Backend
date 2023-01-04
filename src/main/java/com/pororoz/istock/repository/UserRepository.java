package com.pororoz.istock.repository;

import com.pororoz.istock.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    // JPA Query Method
    User findByUsername(String username);

}
