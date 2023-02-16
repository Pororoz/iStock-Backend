package com.pororoz.istock.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pororoz.istock.common.utils.Pagination;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.exception.CategoryNotFoundException;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.product.dto.service.FindProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.FindProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.SubAssyServiceResponse;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNumberDuplicatedException;
import com.pororoz.istock.domain.product.exception.RegisteredAsSubAssayException;
import com.pororoz.istock.domain.product.exception.SubAssayBomExistException;
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
        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("완성품을 sub asssay로 수정한다.")
      void changeToSubAssay() {
        //given
        Bom bom = Bom.builder().codeNumber("not sub assay").build();
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
        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("Sub assay를 완성품으로 수정한다.")
      void changeToProduct() {
        //given
        Product subAssay = Product.builder()
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
        when(productRepository.findById(id)).thenReturn(Optional.of(subAssay));
        when(bomRepository.existsByProductNumber(productNumber)).thenReturn(false);
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
      @DisplayName("완성품을 sub assay로 수정하는데, 해당 bom에 sub assay가 존재하면 예외가 발생한다.")
      void changeToSubAssayThenSubAssayBomExist() {
        //given
        Bom bom = Bom.builder().codeNumber("not sub assay").build();
        Bom subAssaybom = Bom.builder().codeNumber("11").build();
        UpdateProductServiceRequest request = UpdateProductServiceRequest.builder()
            .productId(id).productNumber("new pnumber")
            .productName("new pname").codeNumber("11")
            .stock(stock + 1).companyName("new cname")
            .categoryId(newCategory.getId())
            .build();

        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(bomRepository.findByProductId(id)).thenReturn(List.of(bom, subAssaybom));

        //then
        assertThrows(SubAssayBomExistException.class, () -> productService.updateProduct(request));
      }

      @Test
      @DisplayName("Sub assay를 완성품으로 수정하는데, bom에 해당 제품이 있으면 예외가 발생한다.")
      void changeToProductThenProductBomExist() {
        //given
        Product subAssay = Product.builder()
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
        when(productRepository.findById(id)).thenReturn(Optional.of(subAssay));
        when(bomRepository.existsByProductNumber(productNumber)).thenReturn(true);

        //then
        assertThrows(RegisteredAsSubAssayException.class,
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
      @DisplayName("삭제하려는 sub assay가 다른 제품의 bom으로 등록되어 있다면 삭제할 수 없다.")
      void deleteSubAssay() {
        //given
        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(bomRepository.existsByProductNumber(productNumber)).thenReturn(true);

        //then
        assertThrows(RegisteredAsSubAssayException.class, () -> productService.deleteProduct(id));
      }
    }
  }

  @Nested
  @DisplayName("product 조회")
  class FindProducts {

    Part part = Part.builder().build();
    Bom assyBom = Bom.builder().codeNumber("11").part(part).build();
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

        FindProductServiceRequest request = FindProductServiceRequest.builder()
            .categoryId(categoryId)
            .page(page).size(size)
            .build();

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < size; i++) {
          Product product = Product.builder().id((long) i).category(category).build();
          product.setBoms(List.of(assyBom, otherBom));
          products.add(product);
        }
        PageImpl<Product> productPage = new PageImpl<>(products, PageRequest.of(page, size), total);

        //when
        //category 조회 -> categoryId, productName, page, size로 product 조회
        // -> 각 product의 bom 조회(bom의 codeNumber가 11) -> bom의 part 조회
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.findProductsWithParts(any(Pageable.class), eq(categoryId)))
            .thenReturn(productPage);
        Page<FindProductServiceResponse> result = productService.findProducts(request);

        //then
        SubAssyServiceResponse subAssyResponse = SubAssyServiceResponse.builder().build();

        assertThat(result.getTotalPages()).isEqualTo((total + size) / size);
        assertThat(result.getTotalElements()).isEqualTo(10);
        long i = 0;
        for (FindProductServiceResponse findResponse : result.getContent()) {
          ProductServiceResponse productResponse = findResponse.getProductServiceResponse();
          assertThat(productResponse.getProductId()).isEqualTo(i++);
          assertThat(findResponse.getSubAssyServiceResponses().size()).isEqualTo(1);
          assertThat(findResponse.getSubAssyServiceResponses().get(0))
              .usingRecursiveComparison().isEqualTo(subAssyResponse);
        }
      }

      @Test
      @DisplayName("page와 size가 null이면 product를 전체 조회한다.")
      void findProductAllWithoutPageable() {
        //given
        long total = 10;

        FindProductServiceRequest request = FindProductServiceRequest.builder()
            .categoryId(categoryId)
            .page(null).size(null)
            .build();

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < total; i++) {
          Product product = Product.builder().id((long) i).category(category).build();
          product.setBoms(List.of(assyBom, otherBom));
          products.add(product);
        }

        //when
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.findProductsWithParts(categoryId))
            .thenReturn(products);
        Page<FindProductServiceResponse> result = productService.findProducts(request);

        //then
        SubAssyServiceResponse subAssyResponse = SubAssyServiceResponse.builder().build();

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(total);
        long i = 0;
        for (FindProductServiceResponse findResponse : result.getContent()) {
          ProductServiceResponse productResponse = findResponse.getProductServiceResponse();
          assertThat(productResponse.getProductId()).isEqualTo(i++);
          assertThat(findResponse.getSubAssyServiceResponses().size()).isEqualTo(1);
          assertThat(findResponse.getSubAssyServiceResponses().get(0))
              .usingRecursiveComparison().isEqualTo(subAssyResponse);
        }
      }

      @Test
      @DisplayName("page만 null이면 default 값으로 product를 페이지네이션하여 조회한다.")
      void findProductWithPageNull() {
        //given
        int size = 3;
        long total = 10;

        FindProductServiceRequest request = FindProductServiceRequest.builder()
            .categoryId(categoryId)
            .page(null).size(size)
            .build();

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < total; i++) {
          Product product = Product.builder().id((long) i).category(category).build();
          products.add(product);
        }
        PageImpl<Product> productPage = new PageImpl<>(products,
            PageRequest.of(Pagination.DEFAULT_PAGE, size), total);

        //when
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.findProductsWithParts(any(Pageable.class), eq(categoryId)))
            .thenReturn(productPage);
        Page<FindProductServiceResponse> result = productService.findProducts(request);

        //then
        assertThat(result.getTotalPages()).isEqualTo((total + size) / size);
        assertThat(result.getTotalElements()).isEqualTo(total);
        long i = 0;
        for (FindProductServiceResponse findResponse : result.getContent()) {
          ProductServiceResponse productResponse = findResponse.getProductServiceResponse();
          assertThat(productResponse.getProductId()).isEqualTo(i++);
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
        FindProductServiceRequest request = FindProductServiceRequest.builder()
            .categoryId(1L).size(10).page(1)
            .build();
        //when
        when(categoryRepository.findById(1L)).thenThrow(CategoryNotFoundException.class);

        //then
        assertThrows(CategoryNotFoundException.class, () -> productService.findProducts(request));
      }
    }
  }
}