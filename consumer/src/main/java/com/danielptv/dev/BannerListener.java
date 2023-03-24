package com.danielptv.dev;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class BannerListener implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${consumer.type}")
    private String consumerType;
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        System.out.println(Banner.getBanner(consumerType));
    }
}
