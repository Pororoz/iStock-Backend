package com.pororoz.istock.domain.product.repository;

import com.pororoz.istock.domain.product.entity.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

  Page<Product> findProductsWithParts(Pageable pageable, Long categoryId,
      List<String> productSearch);
}
