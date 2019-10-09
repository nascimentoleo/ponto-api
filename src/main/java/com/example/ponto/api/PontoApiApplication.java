package com.example.ponto.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PontoApiApplication {

  public static void main(final String[] args) {
    SpringApplication.run(PontoApiApplication.class, args);
  }
}
