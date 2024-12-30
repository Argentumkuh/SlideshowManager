package com.kaa.smanager;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class SManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SManagerApplication.class, args);
    }

}
