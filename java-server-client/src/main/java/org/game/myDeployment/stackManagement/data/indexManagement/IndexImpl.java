package org.game.myDeployment.stackManagement.data.indexManagement;

import co.elastic.clients.elasticsearch.cat.IndicesResponse;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetDataStreamResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import org.game.myDeployment.AbstractApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class IndexImpl extends AbstractApiImpl {
    private static final Logger logger = LoggerFactory.getLogger(IndexImpl.class);

    private static String indexId; // IndexApis use endpoint format url/<"my-index-name or my-datastream-name">
    private List<String> listOfIndices;

    public IndexImpl() throws IOException {
        // initialize list of index id's
        listOfIndices = client
                .cat()
                .indices()
                .valueBody()
                .stream()
                .map(IndicesRecord::toString)
                .toList();

        logger.info("List of indicies {}", listOfIndices);
    }

    // Check if a data stream, index, or alias exists
    public BooleanResponse exists(String indexName) throws Exception {
        if (indexName == null || indexName.isBlank()) {
            logger.debug("exists() invoked with null or blank indexName parameter");
            throw new Exception("Throw exception");
        }

        BooleanResponse exists = client.indices().exists(e ->
                e.index(indexName));

        logger.trace("ElasticsearchIndicesClient elasticsearchIndicesClient = client.indices() {}", exists.value());
        return exists;
    }

    public String getIndexTemplate(String indexTemplateName) throws Exception {
        String indexTemplateEndPoint = String.format("/index_template/%s", indexTemplateName);
        if(exists(indexTemplateName).value()) {
            // TODO: parse with logger or use json mapper

            return sendRequest("-X GET", indexTemplateEndPoint);
        }
        return null;
    }

    public IndexResponse getIndex(String indexName) throws Exception {
        IndexResponse indexResponse = client.index(s -> s.index(indexName));
        logger.debug("IndexResponse indexResponse = client.index(s -> s.index(indexName)) result: {} index: {} id: {}", indexResponse.result(), indexResponse.index(), indexResponse.id());
        return indexResponse;
    }


    public boolean createIfNotExists(String indexName) throws IOException {

        CreateIndexResponse createResponse = client.indices() // ElasticSearchIndicesClient
                .create(createIndexBuilder -> createIndexBuilder
                        .index(indexName)
                        .aliases("alias-" + indexName, aliasBuilder -> aliasBuilder
                                .isWriteIndex(true)
                        )
                );
        logger.trace("index:{} shardsAcknowledged:{} acknowledge:{} ",
                createResponse.index(),  // Required - API name: index
                createResponse.shardsAcknowledged(),  // Required - API name: shards_acknowledged
                createResponse.acknowledged()); // Required - API name: acknowledged
        return createResponse.acknowledged();
    }

    public static String getIndexId() {
        return indexId;
    }

    private static void setIndexId(String indexId) {
        IndexImpl.indexId = indexId;
    }

    public List<String> getListOfIndices() {
        return listOfIndices;
    }

    private void setListOfIndices(List<String> listOfIndices) {
        this.listOfIndices = listOfIndices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IndexImpl that)) {
            return false;
        }
        return Objects.equals(listOfIndices, that.listOfIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listOfIndices);
    }

    @Override
    public String toString() {
        return String.format(
                "Number of indices: %d%n " +
                        "index names: %s%n " +
                listOfIndices.size(),
                listOfIndices);
    }
}
