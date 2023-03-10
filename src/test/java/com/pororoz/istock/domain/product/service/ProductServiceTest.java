package com.pororoz.istock.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import com.pororoz.istock.domain.product.dto.service.FindProductByPartServiceRequest;
import com.pororoz.istock.domain.product.dto.service.FindProductWithSubAssyServiceResponse;
import com.pororoz.istock.domain.product.dto.service.ProductServiceResponse;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.dto.service.SubAssyServiceResponse;
import com.pororoz.istock.domain.product.dto.service.UpdateProductServiceRequest;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.exception.ProductNumberDuplicatedException;
import com.pororoz.istock.domain.product.exception.RegisteredAsSubAssyException;
import com.pororoz.istock.domain.product.exception.SubAssyBomExistException;
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
  final String productName = "????????? ???????????? V1.2";
  final String productNumber = "GS-IH-01";
  final String codeNumber = "";
  final long stock = 15;
  final String companyName = "????????????";
  final Long categoryId = 1L;
  final Category category = Category.builder().id(categoryId).build();
  final Product product = Product.builder()
      .id(id).productName(productName)
      .productNumber(productNumber).codeNumber(codeNumber)
      .stock(stock).companyName(companyName)
      .category(category)
      .build();

  @Nested
  @DisplayName("product ??????")
  class SaveProduct {

    SaveProductServiceRequest request = SaveProductServiceRequest.builder()
        .productNumber(productNumber).categoryId(categoryId)
        .build();

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("product??? ????????????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("categoryId??? ???????????? category??? ???????????? ????????? ????????? ????????????.")
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
      @DisplayName("?????? product number??? ?????? ???????????? ????????? ????????????.")
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
  @DisplayName("product ??????")
  class UpdateProduct {

    Category newCategory = Category.builder()
        .id(categoryId + 1).categoryName("new category")
        .build();

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {


      @Test
      @DisplayName("product??? ????????????.")
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

        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("???????????? sub asssay??? ????????????.")
      void changeToSubAssy() {
        //given
        Bom bom = Bom.builder().codeNumber("not sub assy").part(mock(Part.class)).build();
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

        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("Sub assy??? ??????????????? ????????????.")
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
        when(bomRepository.existsByProductId(id)).thenReturn(false);
        when(categoryRepository.findById(newCategory.getId())).thenReturn(Optional.of(newCategory));
        when(productRepository.findByProductNumber(anyString())).thenReturn(Optional.empty());

        ProductServiceResponse result = productService.updateProduct(request);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
      }

      @Test
      @DisplayName("product number??? ????????? ??????.")
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("id??? ???????????? product??? ????????? ????????? ????????????.")
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
      @DisplayName("??????????????? ????????? ?????? ????????? ????????? ????????????.")
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
      @DisplayName("categoryId ??? ???????????? category??? ????????? ????????? ????????????.")
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
      @DisplayName("???????????? sub assy??? ???????????????, ?????? bom??? sub assy??? ???????????? ????????? ????????????.")
      void changeToSubAssyThenSubAssyBomExist() {
        //given
        Part part = Part.builder().build();
        Product subAssy = Product.builder().build();
        Bom bom = Bom.builder().codeNumber("not sub assy").part(part).build();
        Bom subAssybom = Bom.builder().codeNumber("11").subAssy(subAssy).build();
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
      @DisplayName("Sub assy??? ??????????????? ???????????????, bom??? ?????? ????????? ????????? ????????? ????????????.")
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
        when(bomRepository.existsByProductId(id)).thenReturn(true);

        //then
        assertThrows(RegisteredAsSubAssyException.class,
            () -> productService.updateProduct(request));
      }
    }
  }

  @Nested
  @DisplayName("product ??????")
  class DeleteProduct {

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("product??? ????????????.")
      void deleteProduct() {
        //given
        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(bomRepository.existsByProductId(id)).thenReturn(false);
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
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("product id??? ?????? product??? ?????? ??? ????????? ????????? ????????????.")
      void productNotFoundException() {
        //given
        //when
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));
      }

      @Test
      @DisplayName("??????????????? sub assy??? ?????? ????????? bom?????? ???????????? ????????? ????????? ??? ??????.")
      void deleteSubAssy() {
        //given
        //when
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(bomRepository.existsByProductId(id)).thenReturn(true);

        //then
        assertThrows(RegisteredAsSubAssyException.class, () -> productService.deleteProduct(id));
      }
    }
  }

  @Nested
  @DisplayName("product??? subAssy ??????")
  class FindProductsWithSubassies {

    Category subCategory = Category.builder().id(10L).build();
    Part part = Part.builder().build();
    Product subAssy1 = Product.builder().id(100L).productNumber("assy1").codeNumber("11")
        .category(subCategory).build();
    Product subAssy2 = Product.builder().id(101L).productNumber("assy2").codeNumber("11")
        .category(subCategory).build();
    Bom assyBom1 = Bom.builder()
        .locationNumber("assy1").quantity(1).subAssy(subAssy1)
        .codeNumber("11").build();
    Bom assyBom2 = Bom.builder()
        .locationNumber("assy2").quantity(2).subAssy(subAssy2)
        .codeNumber("11").build();
    Bom otherBom = Bom.builder().codeNumber("0").part(part).build();

    @Nested
    @DisplayName("?????? ?????????")
    class SuccessCase {

      @Test
      @DisplayName("product??? ???????????????????????? ????????????.")
      void findProductsPagination() {
        //given
        int page = 0;
        int size = 3;
        long total = 10;

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < size; i++) {
          Product product = Product.builder().id((long) i).category(category).build();
          product.getBoms().add(assyBom1);
          product.getBoms().add(assyBom2);
          product.getBoms().add(otherBom);
          products.add(product);
        }
        PageImpl<Product> productPage = new PageImpl<>(products, PageRequest.of(page, size), total);

        //when
        //category ?????? -> categoryId, productName, page, size??? product ??????
        // -> ??? product??? bom ??????(bom??? codeNumber??? 11)
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryIdWithSubAssies(any(Pageable.class), eq(categoryId)))
            .thenReturn(productPage);
        Page<FindProductWithSubAssyServiceResponse> result =
            productService.findProductsWithSubAssies(categoryId, PageRequest.of(page, size));

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
        for (FindProductWithSubAssyServiceResponse findResponse : result.getContent()) {
          ProductServiceResponse productResponse = findResponse.getProductServiceResponse();
          assertThat(productResponse.getProductId()).isEqualTo(i++);
          assertThat(findResponse.getSubAssyServiceResponses())
              .usingRecursiveComparison()
              .ignoringCollectionOrder()
              .isEqualTo(List.of(subAssyResponse1, subAssyResponse2));
        }
      }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class FailCase {

      @Test
      @DisplayName("categoryId??? ???????????? category??? ????????? ????????? ????????????.")
      void categoryNotFound() {
        //given
        PageRequest pageRequest = PageRequest.of(1, 10);

        //when
        when(categoryRepository.findById(categoryId)).thenThrow(CategoryNotFoundException.class);

        //then
        assertThrows(CategoryNotFoundException.class,
            () -> productService.findProductsWithSubAssies(categoryId, pageRequest));
      }
    }
  }

  @Nested
  @DisplayName("part ????????? product ??????")
  class FindProductByPart {

    Long partId = 1L;
    String partName = "partName";

    @Test
    @DisplayName("part ????????? product??? ???????????????????????? ????????????.")
    void findProductByPart() {
      //given
      int page = 0;
      int size = 3;
      FindProductByPartServiceRequest request = FindProductByPartServiceRequest.builder()
          .partId(partId).partName(partName).build();
      Pageable pageable = PageRequest.of(page, size);
      Product product = Product.builder()
          .id(1L).productName(productName)
          .productNumber(productNumber).codeNumber(codeNumber)
          .companyName(companyName).stock(stock)
          .category(category).boms(List.of())
          .build();
      Page<Product> products = new PageImpl<>(List.of(product), pageable, 1);
      ProductServiceResponse response = ProductServiceResponse.builder()
          .productId(1L).productName(productName)
          .productNumber(productNumber).codeNumber(codeNumber)
          .companyName(companyName).stock(stock)
          .categoryId(categoryId)
          .build();

      //when
      when(productRepository.findByPartIdAndPartNameIgnoreNull(eq(partId),
          eq(partName), any(Pageable.class)))
          .thenReturn(products);

      //then
      Page<ProductServiceResponse> result = productService.findProductsByPart(request,
          pageable);
      assertThat(result.getTotalPages()).isEqualTo(1);
      assertThat(result.getTotalElements()).isEqualTo(1);
      assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(List.of(response));
    }
  }
}
