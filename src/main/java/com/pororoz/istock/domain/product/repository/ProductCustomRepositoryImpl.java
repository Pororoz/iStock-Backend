package com.pororoz.istock.domain.product.repository;

import static com.pororoz.istock.domain.bom.entity.QBom.bom;
import static com.pororoz.istock.domain.part.entity.QPart.part;
import static com.pororoz.istock.domain.product.entity.QProduct.product;

import com.pororoz.istock.domain.product.entity.Product;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public PageImpl<Product> findProductsWithParts(Pageable pageable, Long categoryId,
      List<String> productSearch) {
    List<Product> products = getProductsWithParts(pageable, categoryId, productSearch);
    Long count = getProductCountWithParts(categoryId, productSearch);

    return new PageImpl<>(products, pageable, count);
  }

  private List<Product> getProductsWithParts(Pageable pageable, Long categoryId,
      List<String> productSearch) {
    JPAQuery<Product> query = jpaQueryFactory
        .selectFrom(product)
        .leftJoin(product.boms, bom).fetchJoin()
        .leftJoin(bom.part, part).fetchJoin()
        .where(product.category.id.eq(categoryId));
    addContainingProductNames(query, productSearch);
    return query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  private Long getProductCountWithParts(Long categoryId, List<String> productSearch) {
    JPAQuery<Long> query = jpaQueryFactory
        .select(product.count())
        .from(product)
        .where(product.category.id.eq(categoryId));
    addContainingProductNames(query, productSearch);
    return query.fetchOne();
  }

  private <T> void addContainingProductNames(JPAQuery<T> query, List<String> productSearch) {
    for (String name : productSearch) {
      query.where(product.productName.containsIgnoreCase(name));
    }
  }
}
