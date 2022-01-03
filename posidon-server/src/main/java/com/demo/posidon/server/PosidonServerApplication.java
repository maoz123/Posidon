package com.demo.posidon.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PosidonServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PosidonServerApplication.class);
    }
}
