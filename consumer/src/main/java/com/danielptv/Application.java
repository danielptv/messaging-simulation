package com.danielptv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class Application {
    private Application() {

    }

    public static void main(String[] args) {
        final var app = new SpringApplication(Application.class);
        app.run(args);
    }
}
