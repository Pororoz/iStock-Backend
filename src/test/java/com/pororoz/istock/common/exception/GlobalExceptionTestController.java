package com.pororoz.istock.common.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/test")
public class GlobalExceptionTestController {

  @GetMapping("/test-runtime-error")
  public void testRuntimeError() throws RuntimeException {
    throw new RuntimeException();
  }


  @GetMapping("/test-internal-server-error")
  public void testInternalServerError() throws Exception {
    throw new Exception("Error");
  }
}
