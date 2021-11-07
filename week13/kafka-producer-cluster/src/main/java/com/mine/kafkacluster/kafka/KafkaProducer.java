package com.mine.kafkacluster.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    private Gson gson = new GsonBuilder().create();

    public void send(String data){
        Message message = new Message();
        message.setId(1001L);
        message.setMsg(data);
        message.setSendTime(new Date());

        kafkaTemplate.send("topic-test", gson.toJson(message));
    }
}
