package com.pororoz.istock.domain.file.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Builder
@NoArgsConstructor	// 기본 생성자 추가
@AllArgsConstructor
public class UploadFileServiceRequest {

  private Long productId;
  private MultipartFile csvFile;

}
