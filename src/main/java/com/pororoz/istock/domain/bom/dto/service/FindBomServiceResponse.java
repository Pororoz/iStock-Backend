package com.pororoz.istock.domain.bom.dto.service;

import com.pororoz.istock.domain.part.entity.Part;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindBomServiceResponse {

  private Long bomId;
  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  List<Part> partList;
}
