package com.microservices.practices.kafka.admin.exception;

public class KafkaClientException extends RuntimeException {
    public KafkaClientException() {
    }

    public KafkaClientException(String message) {
        super(message);
    }

    public KafkaClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaClientException(Throwable cause) {
        super(cause);
    }

    public KafkaClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
