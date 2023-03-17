package com.danielptv;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class Application {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
