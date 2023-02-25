package com.pororoz.istock.domain.bom.dto.response;

import com.pororoz.istock.domain.part.entity.Part;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindBomResponse {
  private Long bomId;
  private String locationNumber;
  private String codeNumber;
  private Long quantity;
  private String memo;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Part part;
}
