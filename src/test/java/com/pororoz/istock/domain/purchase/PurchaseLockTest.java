package com.pororoz.istock.domain.purchase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pororoz.istock.common.service.DatabaseCleanup;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.entity.PartStatus;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.entity.ProductIo;
import com.pororoz.istock.domain.product.entity.ProductStatus;
import com.pororoz.istock.domain.product.repository.ProductIoRepository;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.purchase.service.PurchaseService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

@SpringBootTest
public class PurchaseLockTest {

  @Autowired
  PurchaseService purchaseService;

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

  @Autowired
  DatabaseCleanup databaseCleanup;

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

    //part 저장
    List<Part> parts = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Part part = Part.builder()
          .partName("p" + i)
          .spec("p" + i)
          .build();

      parts.add(part);
    }
    partRepository.saveAll(parts);

    //일반 bom
    for (int i = 0; i < 9; i++) {
      Bom bom = Bom.builder()
          .quantity(1)
          .codeNumber("10")
          .locationNumber("" + i + 100)
          .part(parts.get((int) (Math.random() * 9)))
          .product(products.get((int) (Math.random() * 9)))
          .build();

      bomRepository.save(bom);
    }

    //productIo 저장
    ProductIo productIo = ProductIo.builder()
        .quantity(10)
        .status(ProductStatus.구매대기)
        .product(products.get(0))
        .superIo(null)
        .build();
    productIoRepository.save(productIo);

    ProductIo productIo1 = ProductIo.builder()
        .quantity(10)
        .status(ProductStatus.외주생산대기)
        .product(products.get(0))
        .superIo(productIo)
        .build();
    productIoRepository.save(productIo1);

    ProductIo productIo2 = ProductIo.builder()
        .quantity(10)
        .status(ProductStatus.외주생산대기)
        .product(products.get(9))
        .superIo(productIo)
        .build();
    productIoRepository.save(productIo2);

    ProductIo productIo3 = ProductIo.builder()
        .quantity(10)
        .status(ProductStatus.외주생산대기)
        .product(products.get(9))
        .superIo(productIo)
        .build();
    productIoRepository.save(productIo3);

    //partIo 저장
    PartIo partIo = PartIo.builder()
        .quantity(10)
        .status(PartStatus.구매대기)
        .part(parts.get(0))
        .productIo(productIo)
        .build();
    partIoRepository.save(partIo);

    PartIo partIo2 = PartIo.builder()
        .quantity(10)
        .status(PartStatus.구매대기)
        .part(parts.get(0))
        .productIo(productIo)
        .build();
    partIoRepository.save(partIo2);

    PartIo partIo3 = PartIo.builder()
        .quantity(10)
        .status(PartStatus.구매대기)
        .part(parts.get(1))
        .productIo(productIo)
        .build();
    partIoRepository.save(partIo3);
  }

  @AfterEach
  void afterEach() {
    databaseCleanup.execute();
  }

  @Nested
  @DisplayName("낙관적 락 테스트")
  class LockingTest {

    @Test
    @DisplayName("파트 구매 확정 낙관적 락 테스트")
    void purchasePart() throws InterruptedException {
      // given
      // 스레드 개수 3개
      int numberOfThreads = 3;
      ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

      Future<?> future1 = executorService.submit(() -> purchaseService.confirmPurchasePart(1L));
      Future<?> future2 = executorService.submit(() -> purchaseService.confirmPurchasePart(1L));
      Future<?> future3 = executorService.submit(() -> purchaseService.confirmPurchasePart(1L));
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

    @Test
    @DisplayName("파트 구매 확정 및 취소가 겹칠 때 낙관적 락 테스트")
    void purchaseConfilct() throws InterruptedException {
      // given
      // 스레드 개수 3개
      int numberOfThreads = 2;
      ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

      Future<?> future1 = executorService.submit(() -> purchaseService.confirmPurchasePart(1L));
      Future<?> future2 = executorService.submit(() -> purchaseService.cancelPurchasePart(1L));
      Exception result = new Exception();

      // when
      try {
        future1.get();
        future2.get();
      } catch (ExecutionException e) {
        result = (Exception) e.getCause();
      }

      // then
      // 동시에 접근하면 Error 발생 => Transaction 때문에 입력 X
      assertTrue(result instanceof OptimisticLockingFailureException);
    }

    @Test
    @DisplayName("외주생산 확정 낙관적 락 테스트")
    void purchaseSubAssy() throws InterruptedException {
      // given
      int numberOfThreads = 3;
      ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

      Future<?> future1 = executorService.submit(
          () -> purchaseService.confirmSubAssyOutsourcing(2L));
      Future<?> future2 = executorService.submit(
          () -> purchaseService.confirmSubAssyOutsourcing(3L));
      Future<?> future3 = executorService.submit(
          () -> purchaseService.confirmSubAssyOutsourcing(3L));
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

    @Test
    @DisplayName("외주생산 확정 및 취소가 동시에 일어났을 때 낙관적 락 테스트")
    void purchaseSubAssyConfilct() throws InterruptedException {
      // given
      // 스레드 개수 3개
      int numberOfThreads = 3;
      ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

      Future<?> future1 = executorService.submit(
          () -> purchaseService.confirmSubAssyOutsourcing(2L));
      Future<?> future2 = executorService.submit(
          () -> purchaseService.cancelSubAssyOutsourcing(2L));
      Exception result = new Exception();

      // when
      try {
        future1.get();
        future2.get();
      } catch (ExecutionException e) {
        result = (Exception) e.getCause();
      }

      // then
      // 동시에 접근하면 Error 발생 => Transaction 때문에 입력 X
      assertTrue(result instanceof OptimisticLockingFailureException);
    }
  }
}
