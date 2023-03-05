package com.pororoz.istock.domain.file.service;

import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceRequest;
import com.pororoz.istock.domain.bom.service.BomService;
import com.pororoz.istock.domain.category.dto.service.SaveCategoryServiceRequest;
import com.pororoz.istock.domain.category.entity.Category;
import com.pororoz.istock.domain.category.service.CategoryService;

import com.pororoz.istock.domain.file.dto.service.FileServiceResponse;

import com.pororoz.istock.domain.file.exception.InvalidFileException;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.entity.Part;

import com.pororoz.istock.domain.part.service.PartService;
import com.pororoz.istock.domain.product.dto.service.SaveProductServiceRequest;
import com.pororoz.istock.domain.product.entity.Product;
import com.pororoz.istock.domain.product.exception.ProductNotFoundException;
import com.pororoz.istock.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

  private final BomService bomService;
  private final PartService partService;
  private final ProductService productService;
  private final CategoryService categoryService;
  private final String SUB_ASSAY_ID = "SUB_ASS'Y";
  private final String SUB_ASSAY_CODE_NUMBER = "11";

  public FileServiceResponse uploadFile(MultipartFile csvFile, Long productId)  {
      Product targetProduct = productService.findProductById(productId);
      if(targetProduct == null) {
          throw new ProductNotFoundException();
      }

      List<String> bomLines = new ArrayList<>();

      try {
          BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8));

          String line;
          int lineNumber = 0;

          while ((line = br.readLine()) != null) {
              lineNumber++;
              if(lineNumber < 5) continue;

              bomLines.add(line.replaceAll(",", ", "));
          }
          br.close();
      } catch(Exception e) {
          throw new InvalidFileException();
      }

      for(String line : bomLines) {
          List<String> bomData = Arrays.asList(line.split(","));
          bomData = bomData.stream().map(s -> s.trim()).collect(Collectors.toList());

          Long quantity = "".equals(bomData.get(1)) ? Long.parseLong("0") : Long.parseLong(bomData.get(1));
          String locationNumber = bomData.get(2).length() > 100 ? bomData.get(2).substring(100) : bomData.get(2);
          String partName = bomData.get(3);
          String spec = bomData.get(4);
          Long price = "".equals(bomData.get(5)) ? Long.parseLong("0") : Long.parseLong(bomData.get(5)); // 단가
          String income = bomData.get(6); // 입고
          Long stock = "".equals(bomData.get(7)) ? Long.parseLong("0") : Long.parseLong(bomData.get(7)); // 재고
          Long buy = "".equals(bomData.get(8)) ? Long.parseLong("0") : Long.parseLong(bomData.get(8)); // 구매수량
          String codeNumber = bomData.get(9);

          if(SUB_ASSAY_CODE_NUMBER.equals(codeNumber)) {
              // sub Assay
              Product subAssay = productService.findProductByProductNumberAndProductName(bomData.get(3), bomData.get(4)); // subAssay인 경우에는 4, 5번째에 있는 항목이 다름
              // sub Assay 없으면 추가
              if(subAssay == null) {
                  // category 없으면 추가
                  Category subAssayCategory = categoryService.findCategoryIdByName(SUB_ASSAY_ID);
                  if (subAssayCategory == null) {
                      categoryService.saveCategory(new SaveCategoryServiceRequest(SUB_ASSAY_ID));
                      subAssayCategory = categoryService.findCategoryIdByName(SUB_ASSAY_ID);
                  }
                  productService.saveProduct(new SaveProductServiceRequest(bomData.get(3), bomData.get(4), codeNumber, 0, "", subAssayCategory.getId()));
              }
              bomService.saveBom(new SaveBomServiceRequest(locationNumber, codeNumber, quantity, "", targetProduct.getProductNumber(), null, productId));
          } else {
              Part targetPart = partService.findPartByPartNameAndSpec(partName, spec);
              if (targetPart == null) {
                  partService.savePart(new SavePartServiceRequest(partName, spec, price, stock));
              }
              targetPart = partService.findPartByPartNameAndSpec(partName, spec);
              bomService.saveBom(new SaveBomServiceRequest(locationNumber, codeNumber, quantity, "", null, targetPart.getId(), productId));
          }
      }


      return new FileServiceResponse(productId);
  }


}
