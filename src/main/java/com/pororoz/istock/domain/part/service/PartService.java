package com.pororoz.istock.domain.part.service;

import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartNameDuplicatedException;
import com.pororoz.istock.domain.part.exception.PartNotFoundException;
import com.pororoz.istock.domain.part.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PartService {

  private final PartRepository partRepository;

  public PartServiceResponse savePart(SavePartServiceRequest request) {
    partRepository.findByPartNameAndSpec(request.getPartName(), request.getSpec()).ifPresent(p -> {
      throw new PartNameDuplicatedException();
    });
    Part part = partRepository.save(request.toPart());
    return PartServiceResponse.of(part);
  }

  public PartServiceResponse deletePart(long partId) {
    Part part = partRepository.findById(partId)
        .orElseThrow(PartNotFoundException::new);
    partRepository.delete(part);
    return PartServiceResponse.of(part);
  }
}
