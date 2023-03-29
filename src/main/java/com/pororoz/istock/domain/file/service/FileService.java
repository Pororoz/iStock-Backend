package com.pororoz.istock.domain.file.service;

import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
  private final String SUB_ASSAY_CODE_NUMBER = "11";

  public FileServiceResponse uploadFile(MultipartFile csvFile, Long productId) {
    productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

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
                StringBuilder::append)
            .toString();

        if (result.equals("")) {
          continue;
        }

        bomLines.add(line.replaceAll(",", ", "));
      }
      br.close();
    } catch (Exception e) {
      throw new InvalidFileException();
    }

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

      if (SUB_ASSAY_CODE_NUMBER.equals(codeNumber)) {
        // sub Assay
        Optional<Long> optSubAssayId = productRepository.findByProductNumberAndProductName(
            bomData.get(3), bomData.get(4)).map(Product::getId); // subAssay인 경우에는 4, 5번째에 있는 항목이 다름
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
}
