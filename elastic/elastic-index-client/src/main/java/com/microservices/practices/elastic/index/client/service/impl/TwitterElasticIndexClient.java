package com.microservices.practices.elastic.index.client.service.impl;

import com.microservices.practices.config.ElasticConfigData;
import com.microservices.practices.elastic.index.client.service.ElasticIndexClient;
import com.microservices.practices.elastic.index.client.util.ElasticIndexUtil;
import com.microservices.practices.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "false")
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticIndexClient.class);

    private final ElasticConfigData elasticConfigData;

    private final ElasticsearchOperations elasticsearchOperations;

    private final ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

    public TwitterElasticIndexClient(ElasticConfigData configData,
                                     ElasticsearchOperations elasticOperations,
                                     ElasticIndexUtil<TwitterIndexModel> indexUtil) {
        this.elasticConfigData = configData;
        this.elasticsearchOperations = elasticOperations;
        this.elasticIndexUtil = indexUtil;
    }

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
        List<IndexedObjectInformation> documentIds = elasticsearchOperations.bulkIndex(
                indexQueries,
                IndexCoordinates.of(elasticConfigData.getIndexName())
        );
        List<String> listOfIndexOperation = documentIds.
                stream().
                map(IndexedObjectInformation::toString).
                collect(Collectors.toList());
        LOG.info("Documents indexed successfully with type: {} and ids: {}", TwitterIndexModel.class.getName(),
                listOfIndexOperation);
        return listOfIndexOperation;
    }
}
