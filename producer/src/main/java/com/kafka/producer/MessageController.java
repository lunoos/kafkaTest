package com.kafka.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    private final KafkaProducerService kafkaProducerService;

    public MessageController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam("message") String message,@RequestParam("noOfMessage") int noOfMess) {
        for(int i=0;i<noOfMess; i++) {
        System.out.println("messag: "+(message+" "+i)+" sent ");
    	kafkaProducerService.sendMessage("my-topic", (message+" "+i));
        }
        return "Message sent successfully!";
    }
}

