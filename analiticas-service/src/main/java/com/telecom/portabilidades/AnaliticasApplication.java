package com.telecom.portabilidades;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AnaliticasApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnaliticasApplication.class, args);
    }
}
