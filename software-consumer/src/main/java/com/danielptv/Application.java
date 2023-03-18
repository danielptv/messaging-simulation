package com.danielptv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.danielptv.dev.Banner.TEXT;

@SpringBootApplication(proxyBeanMethods = false)
public class Application {
    private Application() {

    }

    public static void main(String[] args) {
        final var app = new SpringApplication(Application.class);
        app.setBanner((environment, sourceClass, out) -> out.println(TEXT));
        app.run(args);
    }
}
