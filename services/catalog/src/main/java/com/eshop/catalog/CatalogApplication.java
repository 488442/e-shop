package com.eshop.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = "com.eshop")
@EnableEurekaClient
public class CatalogApplication {
  public static void main(String[] args) {
    SpringApplication.run(CatalogApplication.class, args);
  }
}
