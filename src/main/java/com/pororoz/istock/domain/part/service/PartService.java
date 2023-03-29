package com.pororoz.istock.domain.part.service;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.domain.bom.repository.BomRepository;
import com.pororoz.istock.domain.part.dto.service.FindPartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.UpdatePartServiceRequest;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartDuplicatedException;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartIoRepository;
import com.pororoz.istock.domain.part.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PartService {

  private final BomRepository bomRepository;
  private final PartRepository partRepository;
  private final PartIoRepository partIoRepository;

  public PartServiceResponse savePart(SavePartServiceRequest request) {
    partRepository.findByPartNameAndSpec(request.getPartName(), request.getSpec()).ifPresent(p -> {
      throw new PartDuplicatedException();
    });
    Part part = partRepository.save(request.toPart());
    return PartServiceResponse.of(part);
  }

  public PartServiceResponse deletePart(Long partId) {
    Part part = partRepository.findById(partId)
        .orElseThrow(PartNotFoundException::new);
    checkRelatedEntityAndThrow(part);
    partRepository.delete(part);
    return PartServiceResponse.of(part);
  }

  public PartServiceResponse updatePart(UpdatePartServiceRequest request) {
    Part part = partRepository.findById(request.getPartId())
        .orElseThrow(PartNotFoundException::new);
    partRepository.findByPartNameAndSpec(request.getPartName(), request.getSpec()).ifPresent(p -> {
      if (!p.equals(part)) {
        throw new PartDuplicatedException();
      }
    });

    part.update(request);
    return PartServiceResponse.of(part);
  }

  @Transactional(readOnly = true)
  public Page<PartServiceResponse> findParts(FindPartServiceRequest request, Pageable pageable) {
    Page<Part> parts = partRepository.findByIdAndPartNameAndSpecIgnoreNull(
        request.getPartId(), request.getPartName(),
        request.getSpec(), pageable);
    return parts.map(PartServiceResponse::of);
  }

  void checkRelatedEntityAndThrow(Part part) {
    if (bomRepository.existByPart(part)) {
      throw new DataIntegrityViolationException(
          ExceptionMessage.CANNOT_DELETE + "부품과 연관된 BOM이 존재합니다.");
    }
    if (partIoRepository.existsByPart(part)) {
      throw new DataIntegrityViolationException(
          ExceptionMessage.CANNOT_DELETE + "부품과 연관된 BOM이 존재합니다.");
    }
  }
}
