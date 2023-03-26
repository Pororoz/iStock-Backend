package com.pororoz.istock.domain.part.service;

import com.pororoz.istock.domain.part.dto.service.FindPartIoServiceResponse;
import com.pororoz.istock.domain.part.entity.PartIo;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PartIoService {

  private final PartIoRepository partIoRepository;

  @Transactional(readOnly = true)
  public Page<FindPartIoServiceResponse> findPartIo(String status, Pageable pageable) {
    Page<PartIo> partIoPage = partIoRepository.findByStatusContainingWithPart(
        status, pageable);
    return partIoPage.map(FindPartIoServiceResponse::of);
  }
}
