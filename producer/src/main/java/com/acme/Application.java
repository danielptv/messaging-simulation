package com.acme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
@SuppressWarnings("FinalClass")
public class Application {
    private Application() {

    }

    public static void main(final String[] args) {
        final var app = new SpringApplication(Application.class);
        app.run(args);
    }
}
