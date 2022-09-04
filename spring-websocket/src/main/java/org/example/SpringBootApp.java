package org.example;

import org.example.rawWebsocket.ServerWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // org.springframework.boot.autoconfigure.SpringBootApplication
@EnableScheduling //org.springframework.scheduling.annotation
public class SpringBootApp extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(SpringBootApp.class);
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);
    }
}