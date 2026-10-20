package com.example.dwaggerdemo;

import com.haigle.dwagger.spring.annotations.EnableDwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDwagger
@SpringBootApplication
public class DwaggerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DwaggerDemoApplication.class, args);
    }
}
