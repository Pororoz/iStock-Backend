package com.pororoz.istock.domain.part.service;

import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.PartServiceResponse;
import com.pororoz.istock.domain.part.dto.service.UpdatePartServiceRequest;
import com.pororoz.istock.domain.part.entity.Part;
import com.pororoz.istock.domain.part.exception.PartDuplicatedException;
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
      throw new PartDuplicatedException();
    });
    Part part = partRepository.save(request.toPart());
    return PartServiceResponse.of(part);
  }

  public PartServiceResponse deletePart(Long partId) {
    Part part = partRepository.findById(partId)
        .orElseThrow(PartNotFoundException::new);
    partRepository.delete(part);
    return PartServiceResponse.of(part);
  }

  public PartServiceResponse updatePart(UpdatePartServiceRequest request) {
    Part part = partRepository.findById(request.getPartId())
            .orElseThrow(PartNotFoundException::new);
    partRepository.findByPartNameAndSpec(request.getPartName(), request.getSpec()).ifPresent(p->{
      throw new PartDuplicatedException();
    });
    part.update(request);
    return PartServiceResponse.of(part);
  }
}
