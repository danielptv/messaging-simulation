package com.acme.dev;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class BannerListener implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${producer.type}")
    private String producerType;
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        System.out.println(Banner.getBanner(producerType));
    }
}
