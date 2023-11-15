package com.microservices.practices.twitter.to.kafka.service.runner;

import com.microservices.practices.config.TwitterToKafkaServiceConfig;
import com.microservices.practices.twitter.to.kafka.service.listener.TwitterToKafkaServiceListener;
import jakarta.annotation.PreDestroy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-v2-tweets", havingValue = "true")
public class TwitterToKafkaServiceRunnerImpl implements StreamRunner  {

    private static final Logger log = LoggerFactory.getLogger(TwitterToKafkaServiceRunnerImpl.class);

    private static final String bearer_toekn = "AAAAAAAAAAAAAAAAAAAAAA7GqwEAAAAA%2FPmHPHyr7UrYlHTeEMfgBoTiW08%3Dse5JESElfVRjqUAA53dbKshtulDDK1zNORKa4iNw44zZj5dhgc";

    private final TwitterToKafkaServiceConfig twitterToKafkaServiceConfig;
    private final TwitterToKafkaServiceListener twitterToKafkaServiceListener;

    private TwitterStream twitterStream;

    public TwitterToKafkaServiceRunnerImpl(TwitterToKafkaServiceConfig twitterToKafkaServiceConfig, TwitterToKafkaServiceListener twitterToKafkaServiceListener) {
        this.twitterToKafkaServiceConfig = twitterToKafkaServiceConfig;
        this.twitterToKafkaServiceListener = twitterToKafkaServiceListener;
    }

    @Override
    public void start() throws TwitterException {
        twitterStream = TwitterStreamFactory.getSingleton();
        twitterStream.addListener(twitterToKafkaServiceListener);
        //addFilter();
        try {
            connectStream(bearer_toekn);
        } catch (IOException e) {
            log.error("IOException :Something went wrong while twitter api call .. {}", e.getStackTrace());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            log.error("URISyntaxException :Something went wrong while twitter api call .. {}", e.getStackTrace());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void shutdown() {
        if(twitterStream != null) {
            log.info("Twitter stream shutdown...");
            twitterStream.shutdown();
        }
    }

    private void addFilter() {
        String[] twitterKeyWorks = twitterToKafkaServiceConfig.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(twitterKeyWorks);
        twitterStream.filter(filterQuery);
        log.info("Add Twitter stream filtered keywords {}", Arrays.toString(twitterKeyWorks));
    }

    /*
     * This method calls the sample stream endpoint and streams Tweets from it
     * */
    private static void connectStream(String bearerToken) throws IOException, URISyntaxException {

        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/sample/stream");

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setHeader("Authorization", String.format("Bearer %s", bearerToken));

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (null != entity) {
            BufferedReader reader = new BufferedReader(new InputStreamReader((entity.getContent())));
            String line = reader.readLine();
            while (line != null) {
                log.info("Twitter streaming data - {}", line);
                line = reader.readLine();
            }
        }

    }
}
