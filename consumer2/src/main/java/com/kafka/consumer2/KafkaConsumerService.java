package com.kafka.consumer2;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "my-topic", groupId = "my-group")
    public void listen(String message) {
    	System.out.println("m: " + message);
    }
}

