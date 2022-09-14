package org.game.myDeployment.stackManagement.data.indexManagement;

import co.elastic.clients.elasticsearch._types.ExpandWildcard;
import co.elastic.clients.elasticsearch.cat.CountResponse;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.game.myDeployment.AbstractApiHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class IndexHandler extends AbstractApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(IndexHandler.class);
    private static String indexId; //
    private List<IndicesRecord> listOfIndices;

    public IndexHandler() throws IOException {
        // initialize list of index id's
        listOfIndices = client
                .cat()
                .indices()
                .valueBody();

        logger.info("List of indicies {}", listOfIndices);
    }

    protected List<IndicesRecord> getAllIndicesRecord(String indexId, ExpandWildcard typeOfIndex) throws IOException {
        return client
                .cat()
                .indices(i ->
                        i.index(indexId)
                                .expandWildcards(typeOfIndex))
                .valueBody(); // _/cat/{indexId}?expand_wildcard={typeOfIndex}
    }

    // Check if a data stream, index, or alias exists
    public BooleanResponse exists(String indexId) throws Exception {
        if (indexId == null || indexId.isBlank()) {
            logger.debug("exists() invoked with null or blank indexName parameter");
            throw new Exception("Throw exception");
        }

        BooleanResponse exists = client.indices().exists(e ->
                e.index(indexId));

        logger.trace("ElasticsearchIndicesClient elasticsearchIndicesClient = client.indices() {}", exists.value());
        return exists;
    }

    public IndexResponse getIndex(String indexId) throws Exception {
        IndexResponse indexResponse = client.index(s -> s.index(indexId));
        logger.debug("IndexResponse indexResponse = client.index(s -> s.index(indexName)) result: {} index: {} id: {}", indexResponse.result(), indexResponse.index(), indexResponse.id());
        return indexResponse;
    }

    public String getIndexTemplate(String indexTemplateName) throws Exception {
        String indexTemplateEndPoint = String.format("/index_template/%s", indexTemplateName);
        if(exists(indexTemplateName).value()) {
            // TODO: parse with logger or use json mapper

            return sendRequest("-X GET", indexTemplateEndPoint);
        }
        return null;
    }

    public boolean createIfNotExists(String indexId) throws IOException {

        CreateIndexResponse createResponse = client.indices() // ElasticSearchIndicesClient
                .create(createIndexBuilder -> createIndexBuilder
                        .index(indexId)
                        .aliases("alias-" + indexId, aliasBuilder -> aliasBuilder
                                .isWriteIndex(true)
                        )
                );
        logger.trace("index:{} shardsAcknowledged:{} acknowledge:{} ",
                createResponse.index(),  // Required - API name: index
                createResponse.shardsAcknowledged(),  // Required - API name: shards_acknowledged
                createResponse.acknowledged()); // Required - API name: acknowledged
        return createResponse.acknowledged();
    }

    // get api retrieves the specified JSON document from an index.
    public GetResponse<ObjectNode> get(String indexId, String docId) throws IOException {

        return client.get(i -> // Creates or updates a document in an index.
                i.index(indexId) // Required - The name of the index. API name: index
                        .id(docId), // Document ID. API name: id
                        ObjectNode.class); //Required - Request body.

    }

    public static String getIndexId() {
        return indexId;
    }

    private static void setIndexId(String indexId) {
        IndexHandler.indexId = indexId;
    }

    protected List<IndicesRecord> getListOfIndices() {
        return listOfIndices;
    }

    private void setListOfIndices(List<IndicesRecord> listOfIndices) {
        this.listOfIndices = listOfIndices;
    }

    public CountResponse getCount(String indexId, String parameters) throws IOException {
        return client.cat().count(c -> c.index(indexId));
//        String.format("/_cat/%s?%s", indexId,parameters);
    }

    public CountResponse getNumberOfDocuments(String indexId) throws IOException {
        return client.cat().count(c -> c.index(indexId));
    }

    public SearchResponse<ObjectNode> getDocIds(String indexId, String queryParams) throws IOException {
        SearchResponse<ObjectNode> response = client.search(g -> g // Returns a document. fn a function that initializes a builder to create the GetRequest
                        .index(indexId) //Required - Name of the index that contains the document.
                        .query(q -> q
                        .match(t -> t
                                .query(queryParams)
                        )
                ),
                ObjectNode.class //Node that maps to JSON Object structures in JSON content.
        );


        return response;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IndexHandler that)) {
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

    static enum IndexId{
        // DS enums are for hidden indices that back data streams and return a document count > 0
        DS_FILEBEAT_IDX(".ds-filebeat-8.4.0-2022.09.08-000001"), // trace, debug, info and error logs from spring-boot and java projects
        DS_ILM_HISTORY_IDX(".ds-ilm-history-5-2022.09.07-000001"), // index lifecycle management history
        DS_SLM_HISTORY_IDX(".ds-.slm-history-5-2022.09.07-000001"), // snapshot lifecycle management history

        DS_LOGS_DEPRECATION(".ds-.logs-deprecation.elasticsearch-default-2022.09.07-000001"), // deprecation logging
        DS_SEARCH_AUDIT_IDX(".ds-logs-enterprise_search.audit-default-2022.09.07-000001"), // enterprise search api logs including queries and inserts
        DS_SEARCH_API_IDX(".ds-logs-enterprise_search.api-default-2022.09.07-000001"), // logs of events across enterprise search, useful for security-related auditing

        // hidden system indices that return a document count > 0
        KIBANA_IDX(".kibana_8.4.1_001"), // dashboard and visualization panel logs
        KIBANA_TASK_MANAGER_IDX(".kibana_task_manager_8.4.1_001"), // background tasks that distribute work across multiple Kibana instances

        SECURITY_IDX(".security-7"), // user privileges, roles, credentials, authentication

        SECURITY_TOKENS_IDX(".security-tokens-7"); // access and refresh tokens

        private String indexId;

        IndexId(String indexId) {
            this.indexId = indexId;
        }
        public String getIndexId() {
            return indexId;
        }
    }
    // TODO: enable and configure kafka module and configure yaml in $FILEBEAT_HOME/modules.d/kafka.yml

}
