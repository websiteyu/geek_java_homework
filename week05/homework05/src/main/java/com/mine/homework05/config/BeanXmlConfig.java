package com.mine.homework05.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = "classpath:beansConfig.xml")
public class BeanXmlConfig {

}
