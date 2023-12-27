package com.ServiceMatch.SM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmApplication {

    public String PORT = System.getenv("PORT");

    public static void main(String[] args) {
        SpringApplication.run(SmApplication.class, args);
    }
}
