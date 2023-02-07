package com.pororoz.istock.domain.product.repository;

import com.pororoz.istock.domain.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

  Optional<Product> findByNumber(String productNumber);
}
