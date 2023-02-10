package com.pororoz.istock.domain.part.service;

import com.pororoz.istock.domain.part.dto.response.PartResponse;
import com.pororoz.istock.domain.part.dto.service.DeletePartServiceResponse;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceRequest;
import com.pororoz.istock.domain.part.dto.service.SavePartServiceResponse;
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

  public SavePartServiceResponse savePart(SavePartServiceRequest request) {
    partRepository.findByPartNameAndSpec(request.getPartName(), request.getSpec()).ifPresent(p -> {
      throw new PartNameDuplicatedException();
    });
    Part part = partRepository.save(request.toPart());
    return SavePartServiceResponse.of(part);
  }

  public DeletePartServiceResponse deletePart(long partId) {
    Part part = partRepository.findByPartId(partId)
        .orElseThrow(PartNotFoundException::new);
    partRepository.delete(part);
    return DeletePartServiceResponse.of(part);
  }
}
