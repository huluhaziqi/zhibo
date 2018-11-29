package com.lin.MyTest.message.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Sender {

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String payload) {
        logger.info("sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }

    public void send(String topic, String key, String payload) {
        logger.info("sending payload='{}' to topic='{}' on key='{}'", payload, topic, key);
        kafkaTemplate.send(topic, key, payload);
    }

    public void send(String topic, int partition, String key, String payload) {
        logger.info("sending payload='{}' to topic='{}' on key='{}' on partition='{}'", payload, topic, key, partition);
        kafkaTemplate.send(topic, partition, key, payload);
    }
}
