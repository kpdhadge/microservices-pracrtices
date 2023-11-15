package com.microservices.practices.twitter.to.kafka.service.exception;

public class TwitterToKafkaServiceException extends RuntimeException {

    public TwitterToKafkaServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwitterToKafkaServiceException(Throwable cause) {
        super(cause);
    }

    public TwitterToKafkaServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TwitterToKafkaServiceException(String message) {
        super(message);
    }
}
