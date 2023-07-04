package com.Rst_harohiro;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@ServletComponentScan
@SpringBootApplication
public class Rst_HaruhiroApplication {
    public static void main(String[] args) {
        SpringApplication.run(Rst_HaruhiroApplication.class);
        System.out.println("running!");
    }
}