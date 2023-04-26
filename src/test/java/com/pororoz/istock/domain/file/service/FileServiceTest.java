package com.pororoz.istock.domain.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pororoz.istock.domain.file.dto.PartPurchaseCountImpl;
import com.pororoz.istock.domain.file.dto.ProductWaitingCountImpl;
import com.pororoz.istock.domain.part.dto.repository.PartPurchaseCount;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.product.dto.repository.ProductWaitingCount;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

  @Nested
  @DisplayName("csv 파일 내보내기")
  class ExportFile {

    @InjectMocks
    FileService fileService;

    @Mock
    PartRepository partRepository;

    @Mock
    ProductRepository productRepository;

    @Nested
    class SuccessCase {

      @Test
      @DisplayName("id list를 입력받아 csv에 제품 개수 정보와 부품 개수 정보를 입력한다.")
      void writeCountInfoWithIdList() throws IOException {
        //given
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<Long> productIdList = List.of(1L, 2L, 3L);
        List<ProductWaitingCount> productCounts = List.of(
            new ProductWaitingCountImpl(1L, "Product1", 10, 20),
            new ProductWaitingCountImpl(2L, "Product2", 5, 3)
        );

        List<Part> partList = List.of(
            new Part(3L, "Part1", "Spec1", 1000L, 30L, null),
            new Part(4L, "Part2", "Spec2", 2000L, -15L, null));
        List<PartPurchaseCount> purchaseCountList = List.of(
            new PartPurchaseCountImpl(3L, "Part1", "Spec1", 30L, 29L),
            new PartPurchaseCountImpl(4L, "Part2", "Spec2", -15L, 12L));

        // when
        when(productRepository.findWaitingCountByIdList(productIdList)).thenReturn(productCounts);
        when(partRepository.findByProductIdList(productIdList)).thenReturn(partList);
        when(partRepository.findPurchaseCountByPartIdList(anyList())).thenReturn(purchaseCountList);
        fileService.exportFile(response, productIdList);

        // then
        assertThat(response.getContentType()).isEqualTo("text/csv; charset=UTF-8");
        assertThat(response.getContentAsString()).contains("No.", "partName", "spec", "stock",
            "구매 수량", "구매 필요 수량");
        assertThat(response.getContentAsString()).contains("No.", "상품", "생산 대기 수량", "구매 대기 수량");
        assertThat(response.getContentAsString()).contains("1", "Product1", "10", "20");
        assertThat(response.getContentAsString()).contains("2", "Product2", "5", "3");
        assertThat(response.getContentAsString()).contains("3", "Part1", "Spec1", "30", "29", "0");
        assertThat(response.getContentAsString()).contains("4", "Part2", "Spec2", "-15", "12", "3");
      }
    }

    @Nested
    class FailCase {

      @Test
      @DisplayName("빈 id 배열이 입력되면 예외가 발생한다.")
      void emptyList() {
        List<Long> productIdList = new ArrayList<>();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> fileService.exportFile(response, productIdList));

        // then
        assertThat("product-id-list is empty").isEqualTo(exception.getMessage());

        verify(productRepository, never()).findWaitingCountByIdList(any());
        verify(partRepository, never()).findByProductIdList(any());
        verify(partRepository, never()).findPurchaseCountByPartIdList(any());
      }

      @Test
      @DisplayName("id 배열이 null이면 예외가 발생한다.")
      void nullList() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> fileService.exportFile(response, null));

        // then
        assertThat("product-id-list is empty").isEqualTo(exception.getMessage());

        verify(productRepository, never()).findWaitingCountByIdList(any());
        verify(partRepository, never()).findByProductIdList(any());
        verify(partRepository, never()).findPurchaseCountByPartIdList(any());
      }
    }
  }
}