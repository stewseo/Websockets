package org.game.deployment.management.stack.data.index_management;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.DataStream;
import co.elastic.clients.elasticsearch.indices.GetDataStreamResponse;
import co.elastic.clients.elasticsearch.indices.IndexTemplate;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.game.deployment.management.stack.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/*
manage the data-streams associated with:
- filebeat.input.filestream.id: java-logs-filestream-id
- filebeat.modules: [elasticsearch, kibana, logstash]

For the required matching index templates containing the mappings and settings used to configure the stream’s backing indices:
- provide parameter values:
- provide query parameter values:

to search and
query
docs

 */
@SuppressWarnings("unused")
public class DataStreamHandler<T extends Comparable<? super T>> extends Handler<T> {

    private static final Logger logger = LoggerFactory.getLogger(DataStreamHandler.class);

    private static final String DATA_STREAM_API_PATH = "/_data_stream"; // path parameter for the data stream api
    public static DataStreamId DATA_STREAM_ID; // a single named resource for requests to store append-only time series data across multiple indices.

    private List<IndexHandler.IndexId> dataStreamBackingIndices; // The hidden, auto-generated backing indices that a data-stream consists of.

    private IndexTemplate indexTemplate;  // Required matching index template containing the mappings and settings used to configure the stream’s backing indices.

    private Set<String> metaKeys;

    public DataStreamHandler() throws IOException {
        DATA_STREAM_ID = DataStreamId.FILEBEAT_DS;
        dataStreamBackingIndices = List.of(IndexHandler.IndexId.DS_FILEBEAT_IDX);
    }

    public DataStreamHandler(DataStreamId dataStreamId) throws IOException {
        DATA_STREAM_ID = dataStreamId;
    }

    public GetResponse<ObjectNode> submitGetRequest(String documentId) throws IOException {
        return client.get(g -> g
                        .index(getDataStreamBackingIndices().get(0).getIndexId()) //
                        .id(documentId), // Unique identifier of the document.
                ObjectNode.class
        );
    }
    public GetDataStreamResponse submitGetDataStreamRequest() throws IOException {
        return client.indices().getDataStream(d -> d
                .name(getDataStreamId())
        );
    }

    public GetDataStreamResponse submitGetDataStreamRequest(String name) throws IOException {
        return client.indices().getDataStream(d -> d
                .name(name)
        );
    }
    public SearchResponse<ObjectNode> submitSearchRequest(String field, String query) throws IOException {
        return client.search(s -> s
                        .index(DATA_STREAM_ID.getDataStreamId())
                        .query(q -> q
                                .match(t -> t
                                        .field(field)
                                        .query(query)
                                )
                        ),
                ObjectNode.class
        );
    }

    public SearchResponse<?> submitSearchRequest(String searchText, Query query) throws IOException {
        return client.search(s -> s
                        .index(DATA_STREAM_ID.getDataStreamId())
                        .query(q -> q
                                .match(t -> t
                                        .field("name")
                                        .query(searchText)
                                )
                        ),
                ObjectNode.class
        );
    }

    public String getDataStreamId() {
        return DATA_STREAM_ID.getDataStreamId();
    }

    private void setDataStreamId(DataStreamId dataStreamId) {
        DATA_STREAM_ID = dataStreamId;
    }

    public List<IndexHandler.IndexId> getDataStreamBackingIndices() {
        return dataStreamBackingIndices;
    }

    public void setDataStreamBackingIndices(List<IndexHandler.IndexId> dataStreamBackingIndices) {
        this.dataStreamBackingIndices = dataStreamBackingIndices;
    }

    @Override
    public String getApiPathParam() {
        return DATA_STREAM_ID.getDataStreamId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataStreamHandler<?> that)) return false;
        return Objects.equals(getDataStreamId(), that.getDataStreamId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataStreamId());
    }

    @Override
    public String toString() {
        return String.format(
                "DataStream %n{data-stream api path = %s}%n, {data-stream.name = %s}%n, {data-stream.id= %s}" ,
                getApiPathParam(),
                DATA_STREAM_ID.name(),
                DATA_STREAM_ID.dataStreamId);
    }

    public static enum DataStreamId {
        FILEBEAT_DS("filebeat-8.4.0"),
        LOGS_ENTERPRISE_SEARCH_API_DS("logs-enterprise_search.api-default"),
        LOGS_ENTERPRISE_SEARCH_AUDIT_DS("logs-enterprise_search.audit-default"),
        ILM_HISTORY_5_DS("ilm-history-5"),

        LOGS_DEPRECATION_ES_DEFAULT_DS(".logs-deprecation.elasticsearch-default"),

        SLM_HISTORY_5_DS(".slm-history-5");
        private final String dataStreamId;

        DataStreamId(String dataStreamId) {
            this.dataStreamId = dataStreamId;
        }

        public String getDataStreamId() {
            return dataStreamId;
        }
    }

    // TODO: change name of alias to fantasyFootballStats-dataStream-id
    // TODO: use scribeJava Api or apache HttpComponents to build a Http Host Connection to the yahoo fantasy football dev api endpoint with an https protocol for an oauth0 access token
    // TODO: send a second request when the access token is expired using the refresh token with headers: and body:
}
