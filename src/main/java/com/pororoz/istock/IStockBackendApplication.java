package com.pororoz.istock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IStockBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(IStockBackendApplication.class, args);
  }

}
