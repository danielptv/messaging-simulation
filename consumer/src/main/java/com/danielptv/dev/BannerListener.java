package com.danielptv.dev;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class BannerListener implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${consumer.type}")
    String consumerType;
    @Value("${server.port}")
    String port;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println(Banner.getBanner(consumerType, port));
    }
}
