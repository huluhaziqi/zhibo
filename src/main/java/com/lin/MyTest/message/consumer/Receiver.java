package com.lin.MyTest.message.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    private static final Logger logger= LoggerFactory.getLogger(Receiver.class);

    @KafkaListener(topics = "${kafka.topic.a-topic}")
    public void receive(String payload) {
        logger.info("received payload='{}'", payload);
        try {
            //TODO
        } catch (Exception e) {
            logger.error("received handel error:" + e.getMessage());
        }
    }
}
