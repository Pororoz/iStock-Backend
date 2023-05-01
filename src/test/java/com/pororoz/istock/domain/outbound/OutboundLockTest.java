package com.pororoz.istock.domain.outbound;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pororoz.istock.IntegrationTest;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.outbound.dto.service.OutboundUpdateServiceRequest;
import com.pororoz.istock.domain.outbound.service.OutboundService;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

public class OutboundLockTest extends IntegrationTest {

  @Autowired
  OutboundService outboundService;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  PartRepository partRepository;

  @Autowired
  BomRepository bomRepository;

  @Autowired
  PartIoRepository partIoRepository;

  @Autowired
  ProductIoRepository productIoRepository;

  @BeforeEach
  void setUp() {
    //product 저장
    Category category = categoryRepository.save(Category.builder().categoryName("카테고리").build());
    List<Product> products = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Product product = Product.builder()
          .productName("p" + i)
          .productNumber("p" + i)
          .stock((int) (Math.random() * 100) + 1)
          .category(category)
          .codeNumber(String.valueOf(2 + i))
          .build();

      products.add(product);
    }
    productRepository.saveAll(products);

    //productIo 저장
    ProductIo productIo = ProductIo.builder()
        .quantity(10)
        .status(ProductStatus.출고대기)
        .product(products.get(0))
        .superIo(null)
        .build();
    productIoRepository.save(productIo);

    ProductIo productIo1 = ProductIo.builder()
        .quantity(10)
        .status(ProductStatus.출고대기)
        .product(products.get(0))
        .superIo(productIo)
        .build();
    productIoRepository.save(productIo1);

    ProductIo productIo2 = ProductIo.builder()
        .quantity(10)
        .status(ProductStatus.출고대기)
        .product(products.get(9))
        .superIo(productIo)
        .build();
    productIoRepository.save(productIo2);

    ProductIo productIo3 = ProductIo.builder()
        .quantity(10)
        .status(ProductStatus.출고대기)
        .product(products.get(9))
        .superIo(productIo)
        .build();
    productIoRepository.save(productIo3);
  }

  @Nested
  @DisplayName("낙관적 락 테스트")
  class LockingTest {

    @Test
    @DisplayName("출고 확정 및 취소가 겹칠 때 낙관적 락 테스트")
    void purchaseConflict() throws InterruptedException {
      // given
      // 스레드 개수 2개
      int numberOfThreads = 2;
      ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

      Future<?> future1 = executorService.submit(() -> outboundService.outboundConfirm(
          OutboundUpdateServiceRequest.builder().productIoId(1L).build()));
      Future<?> future2 = executorService.submit(() -> outboundService.outboundCancel(
          OutboundUpdateServiceRequest.builder().productIoId(1L).build()));
      Future<?> future3 = executorService.submit(() -> outboundService.outboundConfirm(
          OutboundUpdateServiceRequest.builder().productIoId(2L).build()));
      Exception result = new Exception();

      // when
      try {
        future1.get();
        future2.get();
        future3.get();
      } catch (ExecutionException e) {
        result = (Exception) e.getCause();
      }

      // then
      // 동시에 접근하면 Error 발생 => Transaction 때문에 입력 X
      assertTrue(result instanceof OptimisticLockingFailureException);
    }
  }
}
