package com.pororoz.istock.domain.user.repository;

import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Query(value = "update User u set u.password = :password, u.role = :role where u.id = :id")
    void updateUser(@Param("password") String password, @Param("role") Role role, @Param("id") Long id);
}
