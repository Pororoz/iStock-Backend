package com.pororoz.istock.domain.product.repository;

import com.pororoz.istock.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
