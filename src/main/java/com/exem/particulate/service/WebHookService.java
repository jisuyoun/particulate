package com.exem.particulate.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebHookService {
    
    @Value("${webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate;

    // @AllArgsConstructor 을 작성할 경우 Value에 값이 주입되지 않아 직접 생성자를 주입
    public WebHookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendWebHook() {
        System.out.println("테스트");

        Map<String, Object> notification = new HashMap<>();
        notification.put("test", "notification");

        restTemplate.postForObject(webhookUrl, notification, String.class);
    }
}
