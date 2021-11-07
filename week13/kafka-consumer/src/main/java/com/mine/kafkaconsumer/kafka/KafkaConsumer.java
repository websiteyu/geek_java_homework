package com.mine.kafkaconsumer.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaConsumer {

    Gson gson = new GsonBuilder().create();

    @KafkaListener(groupId = "consumer-group-test",topics = {"topic-test"})
    public void consumer(ConsumerRecord<?, ?> record){
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object o = kafkaMessage.get();
            log.info("receive {}",o);
        }
    }
}
