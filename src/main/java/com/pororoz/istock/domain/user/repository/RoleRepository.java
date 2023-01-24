package com.pororoz.istock.domain.user.repository;

import com.pororoz.istock.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
