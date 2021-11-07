package com.mine.kafkacluster;

import com.mine.kafkacluster.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KafkaClusterApplicationTests {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    void contextLoads() {
        kafkaProducer.send("sssss");
    }

}
