package org.example.esRestApis.index;

import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import org.example.esRestApis.AbstractApiHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IndexRequestHandler extends AbstractApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(IndexRequestHandler.class);

    private static String indexId; // IndexApis use endpoint format url/<"my-index-name or my-datastream-name">
    private List<String> listOfIndices;

    private List<String> listOfDataStreams;

    public IndexRequestHandler(){
        // initialize lists with index and data stream reference ids
//        getIndex("kibana_sample_data_ecommerce");
//        getDataStream("");
        listOfIndices = new ArrayList<>();
        listOfDataStreams = new ArrayList<>();
    }

    // Check if a data stream, index, or alias exists
    public boolean exists(String indexName) throws Exception {
        if (indexName == null || indexName.isBlank()) {
            logger.debug("exists() invoked with null or blank indexName parameter");
            throw new Exception("Throw exception");
        }
        return sendRequest("-I", indexName).contains("200");
    }

    public String getIndexTemplate(String indexTemplateName) throws Exception {
        String indexTemplateEndPoint = String.format("/index_template/%s", indexTemplateName);
        if(exists(indexTemplateName)) {
            // TODO: parse with logger or use json mapper
            String result = sendRequest("-X GET", indexTemplateEndPoint);

            return result;
        }
        return null;
    }
//    private String getDataStream(String datastreamId) throws Exception {
//        if(exists(datastreamId)) {
//
//            return sendRequestUsingRestClient("-X GET", datastreamId);
//        }
//        return null;
//    }
    public String getIndex(String indexName) throws Exception {
//        if(exists(indexName)) {
            // TODO: parse with logger or use json mapper
            return sendRequest("-X GET", indexName);
//        }
//        return null;
    }

    //  new ElasticsearchIndicesClient(this.transport, this.transportOptions)
    // create(fn.apply(new CreateIndexRequest.Builder()).build())
    // Builder(String value) {this.index = value}
    public boolean createIfNotExists(String indexName) throws IOException {
        // you must have the create_index or manage index privilege for the target index.
        CreateIndexResponse indexResponse = client.indices().create(c -> c.index(indexName));
        logger.trace("index:{} shardsAcknowledged:{} acknowledge:{} ",
                indexResponse.index(),  // Required - API name: index
                indexResponse.shardsAcknowledged(),  // Required - API name: shards_acknowledged
                indexResponse.acknowledged()); // Required - API name: acknowledged
        return indexResponse.acknowledged();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IndexRequestHandler that)) {
            return false;
        }
        return Objects.equals(listOfIndices, that.listOfIndices) && Objects.equals(listOfDataStreams, that.listOfDataStreams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listOfIndices, listOfDataStreams);
    }

    @Override
    public String toString() {
        return String.format(
                "Number of indices: %d%n " +
                        "index names: %s%n " +
                        "Number of data streams: %d%n " +
                        "data streams: %s",
                listOfIndices.size(),
                listOfIndices,
                listOfDataStreams.size(),
                listOfDataStreams);
    }
}
