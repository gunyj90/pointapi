package com.musinsa.pointapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories("com.musinsa.pointapi.repository")
@SpringBootApplication
public class PointapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointapiApplication.class, args);
    }

}
