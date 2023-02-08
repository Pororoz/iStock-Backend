package com.pororoz.istock.domain.bom.controller;

import com.pororoz.istock.common.dto.ResultDTO;
import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.bom.dto.request.SaveBomRequest;
import com.pororoz.istock.domain.bom.dto.response.BomResponse;
import com.pororoz.istock.domain.bom.dto.service.SaveBomServiceResponse;
import com.pororoz.istock.domain.bom.service.BomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/bom")
@RestController
public class BomController {

  private final BomService bomService;

  @PostMapping
  public ResponseEntity<ResultDTO<BomResponse>> saveBom(@RequestBody SaveBomRequest request) {
    SaveBomServiceResponse serviceDto = bomService.saveBom(request.toService());
    BomResponse response = serviceDto.toResponse();
    return ResponseEntity.ok(
        new ResultDTO<>(ResponseStatus.OK, ResponseMessage.SAVE_BOM, response));
  }
}
