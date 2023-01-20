package com.pororoz.istock.domain.user.repository;

import com.pororoz.istock.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
