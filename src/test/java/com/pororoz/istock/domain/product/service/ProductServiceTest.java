package com.pororoz.istock.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.product.dto.service.FindProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.SubAssyServiceResponse;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNumberDuplicatedException;
import com.pororoz.istock.domain.product.exception.RegisteredAsSubAssyException;
import com.pororoz.istock.domain.product.exception.SubAssyBomExistException;
import com.pororoz.istock.domain.product.exception.SubAssyNotFoundByProductNameException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @InjectMocks
  ProductService productService;

  @Mock
  BomRepository bomRepository;

  @Mock
  ProductRepository productRepository;

  @Mock
  CategoryRepository categoryRepository;


  final Long id = 1L;
  final String productName = "인덕션 컨트롤러 V1.2";
  final String productNumber = "GS-IH-01";
  final String codeNumber = "";
  final long stock = 15;
  final String companyName = "공신금속";
  final Long categoryId = 1L;
  final Category category = Category.builder().id(categoryId).build();
  final Product product = Product.builder()
      .id(id).productName(productName)
      .productNumber(productNumber).codeNumber(codeNumber)
      .stock(stock).companyName(companyName)
      .category(category)
      .build();

  @Nested
  @DisplayName("product 저장")
  class SaveProduct {

    SaveProductServiceRequest request = SaveProductServiceRequest.builder()
        .productNumber(productNumber).categoryId(categoryId)
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("product를 저장한다.")
      void saveProduct() {
        //given
        Product product = Product.builder()
            .productName(productName).category(category)
            .build();
        ProductServiceResponse response = ProductServiceResponse.builder()
            .productName(productName).categoryId(categoryId)
            .build();

        //when
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(
            Optional.of(category));
        ProductServiceResponse result = productService.saveProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("categoryId에 해당하는 category가 존재하지 않으면 예외가 발생한다.")
      void categoryIdNotExist() {
        //given
        //when
        when(productRepository.findByProductNumber(request.getProductNumber()))
            .thenReturn(Optional.empty());
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        //then
        assertThrows(CategoryNotFoundException.class,
            () -> productService.saveProduct(request));
      }

      @Test
      @DisplayName("같은 product number가 이미 존재하면 예외가 발생한다.")
      void productNameDuplicated() {
        //given
        //when
        when(productRepository.findByProductNumber(request.getProductNumber())).thenReturn(
            Optional.of(mock(Product.class)));

        //then
        assertThrows(ProductNumberDuplicatedException.class,
            () -> productService.saveProduct(request));
      }
    }
  }

  @Nested
  @DisplayName("product 수정")
  class UpdateProduct {

    Category newCategory = Category.builder()
        .id(categoryId + 1).categoryName("new category")
        .build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {


      @Test
      @DisplayName("product를 수정한다.")
      void updateProduct() {
        //given
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("new cnumber")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();
        ProductServiceResponse response = ProductServiceResponse.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("new cnumber")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();

        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCategory.getId())).thenReturn(Optional.of(newCategory));
        when(productRepository.findByProductNumber(anyString())).thenReturn(Optional.empty());
        doNothing().when(bomRepository).updateProductNumber(anyString(), anyString());

        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("완성품을 sub asssay로 수정한다.")
      void changeToSubAssy() {
        //given
        Bom bom = Bom.builder().codeNumber("not sub assy").build();
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("11")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();
        ProductServiceResponse response = ProductServiceResponse.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("11")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();

        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(bomRepository.findByProductId(id)).thenReturn(List.of(bom));
        when(categoryRepository.findById(newCategory.getId())).thenReturn(Optional.of(newCategory));
        when(productRepository.findByProductNumber(anyString())).thenReturn(Optional.empty());
        doNothing().when(bomRepository).updateProductNumber(anyString(), anyString());

        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("Sub assy를 완성품으로 수정한다.")
      void changeToProduct() {
        //given
        Product subAssy = Product.builder()
            .id(id).productName(productName)
            .productNumber(productNumber).codeNumber("11")
            .stock(stock).companyName(companyName)
            .category(category)
            .build();
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("new cnumber")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();
        ProductServiceResponse response = ProductServiceResponse.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("new cnumber")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();

        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(subAssy));
        when(bomRepository.existsByProductNumber(productNumber)).thenReturn(false);
        when(categoryRepository.findById(newCategory.getId())).thenReturn(Optional.of(newCategory));
        when(productRepository.findByProductNumber(anyString())).thenReturn(Optional.empty());
        doNothing().when(bomRepository).updateProductNumber(anyString(), anyString());

        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("product number의 변경이 없다.")
      void notChangeProductNumber() {
        //given
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(id).productNumber(productNumber)
            .productName("new pname").codeNumber("new cnumber")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();
        ProductServiceResponse response = ProductServiceResponse.builder()
            .productId(id).productNumber(productNumber)
            .productName("new pname").codeNumber("new cnumber")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();

        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(newCategory.getId())).thenReturn(Optional.of(newCategory));

        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("id에 해당하는 product가 없으면 에러가 발생한다.")
      void productNotFound() {
        //given
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(2L)
            .build();

        //when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(request));
      }

      @Test
      @DisplayName("변경하려는 품번이 이미 있으면 예외가 발생한다.")
      void duplicateProductNumber() {
        //given
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(1L).productNumber("new")
            .build();
        Product product = Product.builder()
            .id(1L).productNumber("old")
            .build();

        //when
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(productRepository.findByProductNumber(request.getProductNumber()))
            .thenReturn(Optional.of(mock(Product.class)));

        //then
        assertThrows(ProductNumberDuplicatedException.class,
            () -> productService.updateProduct(request));
      }

      @Test
      @DisplayName("categoryId 에 해당하는 category가 없으면 에러가 발생한다.")
      void categoryNotFound() {
        //given
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(1L).productNumber("number")
            .categoryId(2L)
            .build();

        //when
        when(productRepository.findById(request.getProductId())).thenReturn(
            Optional.of(mock(Product.class)));
        when(productRepository.findByProductNumber(request.getProductNumber()))
            .thenReturn(Optional.empty());
        when(categoryRepository.findById(request.getCategoryId())).thenReturn(Optional.empty());

        //then
        assertThrows(CategoryNotFoundException.class, () -> productService.updateProduct(request));
      }

      @Test
      @DisplayName("완성품을 sub assy로 수정하는데, 해당 bom에 sub assy가 존재하면 예외가 발생한다.")
      void changeToSubAssyThenSubAssyBomExist() {
        //given
        Bom bom = Bom.builder().codeNumber("not sub assy").build();
        Bom subAssybom = Bom.builder().codeNumber("11").build();
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("11")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();

        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(bomRepository.findByProductId(id)).thenReturn(List.of(bom, subAssybom));

        //then
        assertThrows(SubAssyBomExistException.class, () -> productService.updateProduct(request));
      }

      @Test
      @DisplayName("Sub assy를 완성품으로 수정하는데, bom에 해당 제품이 있으면 예외가 발생한다.")
      void changeToProductThenProductBomExist() {
        //given
        Product subAssy = Product.builder()
            .id(id).productName(productName)
            .productNumber(productNumber).codeNumber("11")
            .stock(stock).companyName(companyName)
            .category(category)
            .build();
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("new cnumber")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();

        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(subAssy));
        when(bomRepository.existsByProductNumber(productNumber)).thenReturn(true);

        //then
        assertThrows(RegisteredAsSubAssyException.class,
            () -> productService.updateProduct(request));
      }
    }
  }

  @Nested
  @DisplayName("product 삭제")
  class DeleteProduct {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("product를 삭제한다.")
      void deleteProduct() {
        //given
        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(bomRepository.existsByProductNumber(productNumber)).thenReturn(false);
        doNothing().when(productRepository).delete(product);

        //then
        ProductServiceResponse response = ProductServiceResponse.builder()
            .productId(id).productNumber(productNumber)
            .productName(productName).stock(stock)
            .companyName(companyName).codeNumber(codeNumber)
            .categoryId(categoryId)
            .build();
        assertThat(productService.deleteProduct(id)).usingRecursiveComparison().isEqualTo(response);
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("product id에 맞는 product를 찾을 수 없으면 예외가 발생한다.")
      void productNotFoundException() {
        //given
        //when
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));
      }

      @Test
      @DisplayName("삭제하려는 sub assy가 다른 제품의 bom으로 등록되어 있다면 삭제할 수 없다.")
      void deleteSubAssy() {
        //given
        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(bomRepository.existsByProductNumber(productNumber)).thenReturn(true);

        //then
        assertThrows(RegisteredAsSubAssyException.class, () -> productService.deleteProduct(id));
      }
    }
  }

  @Nested
  @DisplayName("product 조회")
  class FindProducts {

    Category subCategory = Category.builder().id(10L).build();
    Part part = Part.builder().build();
    Product subAssy1 = Product.builder().productNumber("assy1").codeNumber("11")
        .category(subCategory).build();
    Product subAssy2 = Product.builder().productNumber("assy2").codeNumber("11")
        .category(subCategory).build();
    Bom assyBom1 = Bom.builder()
        .locationNumber("assy1").quantity(1).productNumber("assy1")
        .codeNumber("11").part(part).build();
    Bom assyBom2 = Bom.builder()
        .locationNumber("assy2").quantity(2).productNumber("assy2")
        .codeNumber("11").part(part).build();
    Bom otherBom = Bom.builder().codeNumber("0").part(part).build();

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

      @Test
      @DisplayName("product를 페이지네이션하여 조회한다.")
      void findProductsPagination() {
        //given
        int page = 0;
        int size = 3;
        long total = 10;

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < size; i++) {
          Product product = Product.builder().id((long) i).category(category).build();
          product.setBoms(List.of(assyBom1, assyBom2, otherBom));
          products.add(product);
        }
        PageImpl<Product> productPage = new PageImpl<>(products, PageRequest.of(page, size), total);

        //when
        //category 조회 -> categoryId, productName, page, size로 product 조회
        // -> 각 product의 bom 조회(bom의 codeNumber가 11)
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryIdWithBoms(any(Pageable.class), eq(categoryId)))
            .thenReturn(productPage);
        when(productRepository.findByProductNumberIn(anyList())).thenReturn(
            List.of(subAssy1, subAssy2));
        Page<FindProductServiceResponse> result =
            productService.findProducts(categoryId, PageRequest.of(page, size));

        //then
        SubAssyServiceResponse subAssyResponse1 = SubAssyServiceResponse.builder()
            .productServiceResponse(ProductServiceResponse.of(subAssy1))
            .quantity(1)
            .build();
        SubAssyServiceResponse subAssyResponse2 = SubAssyServiceResponse.builder()
            .productServiceResponse(ProductServiceResponse.of(subAssy2))
            .quantity(2)
            .build();

        assertThat(result.getTotalPages()).isEqualTo((total + size) / size);
        assertThat(result.getTotalElements()).isEqualTo(10);
        long i = 0;
        for (FindProductServiceResponse findResponse : result.getContent()) {
          ProductServiceResponse productResponse = findResponse.getProductServiceResponse();
          assertThat(productResponse.getProductId()).isEqualTo(i++);
          assertThat(findResponse.getSubAssyServiceResponses()).hasSize(2);
          assertThat(findResponse.getSubAssyServiceResponses())
              .usingRecursiveComparison()
              .ignoringCollectionOrder()
              .isEqualTo(List.of(subAssyResponse1, subAssyResponse2));
        }
      }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {

      @Test
      @DisplayName("categoryId에 해당하는 category가 없으면 예외가 발생한다.")
      void categoryNotFound() {
        //given
        PageRequest pageRequest = PageRequest.of(1, 10);

        //when
        when(categoryRepository.findById(categoryId)).thenThrow(CategoryNotFoundException.class);

        //then
        assertThrows(CategoryNotFoundException.class,
            () -> productService.findProducts(categoryId, pageRequest));
      }

      @Test
      @DisplayName("Sub assy 목록에 bom의 productNumber와 일치하는 product가 없다.")
      void subAssyNotFoundByProductName() {
        //given
        int page = 0;
        int size = 3;
        long total = 10;

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < size; i++) {
          Product product = Product.builder().id((long) i).category(category).build();
          product.setBoms(List.of(assyBom1, assyBom2, otherBom));
          products.add(product);
        }
        PageImpl<Product> productPage = new PageImpl<>(products, PageRequest.of(page, size), total);

        //when
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryIdWithBoms(any(Pageable.class), eq(categoryId)))
            .thenReturn(productPage);
        when(productRepository.findByProductNumberIn(anyList())).thenReturn(
            List.of(subAssy1));

        //then
        assertThrows(SubAssyNotFoundByProductNameException.class,
            () -> productService.findProducts(categoryId, PageRequest.of(page, size)));
      }
    }
  }
}
