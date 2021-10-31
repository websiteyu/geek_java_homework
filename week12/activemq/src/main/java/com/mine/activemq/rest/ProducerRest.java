package com.mine.activemq.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;

@RestController
public class ProducerRest {
    private final String SUCCESS = "SUCCESS";


    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private Queue queue;
    @Autowired
    private Topic topic;

    @PostMapping("/queue/test")
    public String sendQueue(@RequestBody String str){
        sendMessage(queue,str);
        return SUCCESS;
    }

    @PostMapping("/topic/test")
    public String sendTopic(@RequestBody String str){
        sendMessage(topic,str);
        return SUCCESS;
    }


    private void sendMessage(Destination destination,final String str){
        jmsMessagingTemplate.convertAndSend(destination,str);
    }
}
