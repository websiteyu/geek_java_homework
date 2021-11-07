package com.mine.kafkaconsumer.kafka;

import lombok.Data;

import java.util.Date;

@Data
// 提取到公共代码
public class Message {
    private Long id;
    private String msg;
    private Date sendTime;
}
