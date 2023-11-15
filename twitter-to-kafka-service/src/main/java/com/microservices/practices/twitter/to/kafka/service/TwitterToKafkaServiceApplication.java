package com.microservices.practices.twitter.to.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.practices")
public class TwitterToKafkaServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

    public static void main(String[] args) {
        log.info("Twitter to kafka service started ...");
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }
}
