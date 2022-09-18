package org.game.deployment.management.stack.data.index_management;

import co.elastic.clients.elasticsearch._types.ExpandWildcard;
import co.elastic.clients.elasticsearch.cat.CountResponse;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexTemplateRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.game.deployment.management.stack.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class IndexHandler<T extends Comparable<? super T>> extends Handler<T> {
    private static final Logger logger = LoggerFactory.getLogger(IndexHandler.class);

    private String CAT_INDICES_API_PATH = "/_cat/indices"; // api path to cat indices methods

    public static IndexId INDEX_ID;

    private List<IndicesRecord> listOfIndices;

    private Map<String, String> indicesRecordFields;

    public IndexHandler() throws Exception {
        INDEX_ID = IndexId.DS_FILEBEAT_IDX;
    }

    public IndexHandler(IndexId indexid){
        INDEX_ID = indexid;
    }


    public IndexResponse submitIndexRequest(String indexId) throws Exception {
        IndexResponse indexResponse = client.index(s -> s.index(indexId));
        logger.debug("IndexResponse indexResponse = client.index(s -> s.index(indexName)) result: {} index: {} id: {}", indexResponse.result(), indexResponse.index(), indexResponse.id());
        return indexResponse;
    }

    protected List<IndicesRecord> getIndicesRecord(String indexId, ExpandWildcard typeOfIndex) throws IOException {
        return client
                .cat()
                .indices(i ->
                        i.index(indexId)
                                .expandWildcards(typeOfIndex))
                .valueBody(); // _/cat/{indexId}?expand_wildcard={typeOfIndex}
    }

    public String submitGetIndexTemplateRequest(String indexTemplateName) throws Exception {

        client.indices().getIndexTemplate(i ->
                i.name(indexTemplateName));
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

    public GetResponse<ObjectNode> get(String indexId, String docId) throws IOException {

        return client.get(i -> // Retrieves the specified JSON document from an index
                i.index(indexId) // Required - The name of the index. API name: index
                        .id(docId), // Document ID. API name: id
                        ObjectNode.class);

    }

    public String getIndexId() {
        return INDEX_ID.getIndexId();
    }

    private void setIndexId(IndexId indexId) {
        INDEX_ID = indexId;
    }

    protected List<IndexId>  getListOfIndices() {
        return Arrays.stream(IndexId.values()).toList();
    }

    public CountResponse getCount(String indexId, String queryParameters) throws IOException {
        return client.cat().count(c -> c.index(indexId)); ///_cat/<index id>?<query parameters>", indexId, parameters
    }

    // Returns a document
    public SearchResponse<ObjectNode> submitSearchRequest(String indexId, String queryParams) throws IOException {

        return client.search(g -> g // fn a function that initializes a builder to create the GetRequest
                        .index(indexId) //Name of the index that contains the document.
                        .query(q -> q // Defines the search definition using the Query DSL.
                        .match(t -> t
                                .query(queryParams)
                        )
                ),
                ObjectNode.class //Node that maps to JSON Object structures in JSON content.
        );
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

    @Override
    public String getApiPathParam() {
        return getIndexId();
    }

    static enum IndexId{
        // indices that back data streams and return a document count > 0
        DS_FILEBEAT_IDX(".ds-filebeat-8.4.0-2022.09.08-000001"), // trace, debug, info and error logs from spring-boot and java projects
        DS_ILM_HISTORY_IDX(".ds-ilm-history-5-2022.09.07-000001"), // index lifecycle management history
        DS_SLM_HISTORY_IDX(".ds-.slm-history-5-2022.09.07-000001"), // snapshot lifecycle management history
        DS_LOGS_DEPRECATION(".ds-.logs-deprecation.elasticsearch-default-2022.09.07-000001"), // deprecation logging
        DS_SEARCH_AUDIT_IDX(".ds-logs-enterprise_search.audit-default-2022.09.07-000001"), // enterprise search api logs including queries and inserts
        DS_SEARCH_API_IDX(".ds-logs-enterprise_search.api-default-2022.09.07-000001"), // logs of events across enterprise search, useful for security-related auditing

        // system indices that return a document count > 0
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
