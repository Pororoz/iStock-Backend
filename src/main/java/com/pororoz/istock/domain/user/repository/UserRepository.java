package com.pororoz.istock.domain.user.repository;

import com.pororoz.istock.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  // JPA Query Method
  Optional<User> findByUsername(String username);

}
