package com.restaurante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SistemaRestauranteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaRestauranteApplication.class, args);
    }
}
