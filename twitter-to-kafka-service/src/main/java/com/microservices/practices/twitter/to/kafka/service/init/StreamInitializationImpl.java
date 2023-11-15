package com.microservices.practices.twitter.to.kafka.service.init;

import com.microservices.practices.config.KafkaConfigData;
import com.microservices.practices.kafka.admin.config.client.KafkaAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StreamInitializationImpl implements StreamInitialization {

    private static final Logger LOG = LoggerFactory.getLogger(StreamInitializationImpl.class);


    private final KafkaConfigData kafkaConfigData;

    private final KafkaAdminClient kafkaAdminClient;

    public StreamInitializationImpl(KafkaConfigData kafkaConfigData, KafkaAdminClient kafkaAdminClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaAdminClient = kafkaAdminClient;
    }

    @Override
    public void init() {
        kafkaAdminClient.createTopics();
        kafkaAdminClient.checkSchemaRegistry();
        LOG.info("Topics with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
    }
}
