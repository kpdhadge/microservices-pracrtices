package com.microservices.practices.twitter.to.kafka.service.controller;

import com.microservices.practices.twitter.to.kafka.service.init.StreamInitialization;
import com.microservices.practices.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TwitterToKafkaServiceInitializer implements CommandLineRunner { //ApplicationRunner or ApplicationListener or EventListener

    private static final Logger log = LoggerFactory.getLogger(TwitterToKafkaServiceInitializer.class);

    private final StreamRunner streamRunner;
    private final StreamInitialization streamInitialization;

    public TwitterToKafkaServiceInitializer(StreamRunner streamRunner, StreamInitialization streamInitialization) {
        this.streamRunner = streamRunner;
        this.streamInitialization = streamInitialization;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Twitter streaming start ......");
        streamInitialization.init();
        streamRunner.start();
    }
}
