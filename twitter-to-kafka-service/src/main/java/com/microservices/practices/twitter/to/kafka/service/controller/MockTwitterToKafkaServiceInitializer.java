package com.microservices.practices.twitter.to.kafka.service.controller;

import com.microservices.practices.config.TwitterToKafkaServiceConfig;
import com.microservices.practices.twitter.to.kafka.service.exception.TwitterToKafkaServiceException;
import com.microservices.practices.twitter.to.kafka.service.listener.TwitterToKafkaServiceListener;
import com.microservices.practices.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockTwitterToKafkaServiceInitializer implements StreamRunner {

    private static final Logger log = LoggerFactory.getLogger(MockTwitterToKafkaServiceInitializer.class);
    private static final Random RANDOM = new Random();

    private final TwitterToKafkaServiceConfig twitterToKafkaServiceConfig;
    private final TwitterToKafkaServiceListener twitterToKafkaServiceListener;

    private static final String[] WORDS = new String[]{
            "Lorem",
            "ipsum",
            "dolor",
            "sit",
            "amet",
            "consectetuer",
            "adipiscing",
            "elit",
            "Maecenas",
            "porttitor",
            "congue",
            "massa",
            "Fusce",
            "posuere",
            "magna",
            "sed",
            "pulvinar",
            "ultricies",
            "purus",
            "lectus",
            "malesuada",
            "libero"
    };

    private static final String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    public MockTwitterToKafkaServiceInitializer(TwitterToKafkaServiceConfig twitterToKafkaServiceConfig, TwitterToKafkaServiceListener twitterToKafkaServiceListener) {
        this.twitterToKafkaServiceConfig = twitterToKafkaServiceConfig;
        this.twitterToKafkaServiceListener = twitterToKafkaServiceListener;
    }

    @Override
    public void start() throws TwitterException {
        String[] keyWords = twitterToKafkaServiceConfig.getTwitterKeywords().toArray(new String[0]);
        Integer minLength = twitterToKafkaServiceConfig.getMockMinTweetLength();
        Integer maxLength = twitterToKafkaServiceConfig.getMockMaxTweetLength();
        Long sleepTime = twitterToKafkaServiceConfig.getMockSleepMs();
        log.info("Starting mock filtering twitter streams for keywords {}", Arrays.toString(keyWords));
        simulateTwitterStream(keyWords, minLength, maxLength, sleepTime);
    }

    private void simulateTwitterStream(String[] keyWords, Integer minLength, Integer maxLength, Long sleepTime) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (true) {
                    String formattedTweetAsRawJson = getFormattedTweet(keyWords, minLength, maxLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                    twitterToKafkaServiceListener.onStatus(status);
                    sleep(sleepTime);
                }
            } catch (TwitterException e) {
                log.error("Error creating twitter status!", e);
            }
        });
    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new TwitterToKafkaServiceException("Error while sleeping for waiting new status to create!!");
        }
    }

    private String getFormattedTweet(String[] keyWords, Integer minLength, Integer maxLength) {
        String[] params = new String[] {
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keyWords, minLength, maxLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        return formatTweetAsJsonWithParams(params);
    }

    private String formatTweetAsJsonWithParams(String[] params) {
        String tweet = tweetAsRawJson;

        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keyWords, Integer minLength, Integer maxLength) {
        StringBuilder stringBuilder = new StringBuilder();
        int tweetLength = RANDOM.nextInt( maxLength - minLength + 1) + minLength;
        return constructRandomTweet(keyWords, stringBuilder, tweetLength);
    }

    private String constructRandomTweet(String[] keyWords, StringBuilder stringBuilder, int tweetLength) {
        for (int i=0; i< tweetLength; i++) {
            stringBuilder.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
                    if(i == tweetLength/2) {
                        stringBuilder.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
                    }
        }
        return stringBuilder.toString().trim();
    }
}
