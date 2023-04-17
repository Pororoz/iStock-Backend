package com.pororoz.istock.domain.file.service;

import com.opencsv.CSVWriter;
import com.pororoz.istock.common.entity.TimeEntity;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.entity.Bom;
import com.pororoz.istock.domain.bom.service.BomService;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.repository.CategoryRepository;
import com.pororoz.istock.domain.category.service.CategoryService;
import com.pororoz.istock.domain.file.dto.service.FileServiceResponse;
import com.pororoz.istock.domain.file.exception.InvalidFileException;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.repository.PartRepository;
import com.pororoz.istock.domain.part.service.PartService;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.repository.ProductRepository;
import com.pororoz.istock.domain.product.service.ProductService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

  private final BomService bomService;
  private final PartService partService;
  private final PartRepository partRepository;
  private final ProductService productService;
  private final ProductRepository productRepository;
  private final CategoryService categoryService;
  private final CategoryRepository categoryRepository;

  private final String SUB_ASSAY_ID = "SUB_ASS'Y";

  public FileServiceResponse uploadFile(MultipartFile csvFile, Long productId) {
    productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

    List<String> bomLines = splitBomFile(csvFile);

    Optional<Long> optSubAssayCategoryId = categoryRepository.findByCategoryName(SUB_ASSAY_ID)
        .map(Category::getId);
    Long subAssayCategoryId = optSubAssayCategoryId.orElseGet(
        () -> categoryService.saveCategory(new SaveCategoryServiceRequest(SUB_ASSAY_ID))
            .getCategoryId());

    for (String line : bomLines) {
      List<String> bomData = Arrays.asList(line.split(","));
      bomData = bomData.stream().map(String::trim).collect(Collectors.toList());

      Long quantity =
          "".equals(bomData.get(1)) ? Long.parseLong("0") : Long.parseLong(bomData.get(1));
      String locationNumber = bomData.get(2);
      String partName = bomData.get(3);
      String spec = bomData.get(4);
      long price =
          "".equals(bomData.get(5)) ? Long.parseLong("0") : Long.parseLong(bomData.get(5)); // 단가
      String income = bomData.get(6); // 입고
      long stock =
          "".equals(bomData.get(7)) ? Long.parseLong("0") : Long.parseLong(bomData.get(7)); // 재고
      long buy =
          "".equals(bomData.get(8)) ? Long.parseLong("0") : Long.parseLong(bomData.get(8)); // 구매수량
      String codeNumber = bomData.get(9);

      if (Bom.SUB_ASSY_CODE_NUMBER.equals(codeNumber)) {
        // sub Assay
        Optional<Long> optSubAssayId = productRepository.findByProductNumberAndProductName(
            partName, spec).map(Product::getId); // subAssay인 경우에는 4, 5번째에 있는 항목이 다름
        Long subAssayId = optSubAssayId.orElseGet(() -> productService.saveProduct(
                new SaveProductServiceRequest(partName, spec, codeNumber, 0, "", subAssayCategoryId))
            .getProductId());

        bomService.saveBom(
            new SaveBomServiceRequest(locationNumber, codeNumber, quantity, "", null, subAssayId,
                productId));
      } else {
        Optional<Long> optTargetPartId = partRepository.findByPartNameAndSpec(partName, spec)
            .map(Part::getId);
        Long targetPartId = optTargetPartId.orElseGet(
            () -> partService.savePart(new SavePartServiceRequest(partName, spec, price, stock))
                .getPartId());

        bomService.saveBom(
            new SaveBomServiceRequest(locationNumber, codeNumber, quantity, "", targetPartId, null,
                productId));
      }
    }
    return new FileServiceResponse(productId);
  }

  List<String> splitBomFile(MultipartFile csvFile) {
    List<String> bomLines = new ArrayList<>();
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8));
      String line;
      int lineNumber = 0;

      while ((line = br.readLine()) != null) {
        lineNumber++;
        if (lineNumber < 5) {
          continue;
        }

        String result = line.chars().filter(ch -> ch != ',')
            .collect(StringBuilder::new, StringBuilder::appendCodePoint,
                StringBuilder::append).toString();

        if (!result.equals("")) {
          bomLines.add(line.replaceAll(",", ", "));
        }
      }
      br.close();
    } catch (Exception e) {
      throw new InvalidFileException();
    }
    return bomLines;
  }

  @Transactional(readOnly = true)
  public void exportFile(HttpServletResponse response, List<Long> productIdList)
      throws IOException {
    if (productIdList == null || productIdList.isEmpty()) {
      throw new IllegalArgumentException("product-id-list is empty");
    }
    String now = TimeEntity.formatTime(LocalDateTime.now());
    response.setContentType("text/csv; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Content-Disposition", "attachment; filename=\"bom_" + now + ".csv\"");
    response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}); // BOM 삽입

    try (ServletOutputStream outputStream = response.getOutputStream();
        CSVWriter writer = new CSVWriter(
            new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
      List<String[]> records = new ArrayList<>();
      recordProductCountInfo(records, productIdList);
      records.add(new String[]{""});
      recordPartCountInfo(records, productIdList);
      writer.writeAll(records);
    }
  }

  private void recordProductCountInfo(List<String[]> records, List<Long> productIdList) {
    records.add(new String[]{"No.", "상품", "생산 대기 수량", "구매 대기 수량"});
    productRepository.findWaitingCountByIdList(productIdList).forEach(count -> {
      records.add(
          new String[]{String.valueOf(count.getId()), String.valueOf(count.getProductName()),
              String.valueOf(count.getProductionWaitingCount()),
              String.valueOf(count.getPurchaseWaitingCount())});
    });
  }

  private void recordPartCountInfo(List<String[]> records, List<Long> productIdList) {
    records.add(new String[]{"No.", "partName", "spec", "stock", "구매 수량", "구매 필요 수량"});
    List<Long> partIdList = partRepository.findByProductIdList(productIdList).stream()
        .map(Part::getId).toList();
    partRepository.findPurchaseCountByPartIdList(partIdList).forEach(count -> {
      long requireCount = Math.max(-count.getStock() - count.getPurchaseWaitingCount(), 0);
      records.add(
          new String[]{String.valueOf(count.getId()), String.valueOf(count.getPartName()),
              String.valueOf(count.getSpec()), String.valueOf(count.getStock()),
              String.valueOf(count.getPurchaseWaitingCount()), String.valueOf(requireCount)});
    });
  }
}
