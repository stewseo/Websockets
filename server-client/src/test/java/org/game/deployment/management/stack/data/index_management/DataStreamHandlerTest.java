package org.game.deployment.management.stack.data.index_management;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldAndFormat;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.indices.GetDataStreamResponse;
import co.elastic.clients.elasticsearch.ingest.simulate.Document;
import co.elastic.clients.json.JsonpDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DataStreamHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(DataStreamHandlerTest.class);
    private static DataStreamHandler<?> dataHandler;

    @BeforeAll
    static void beforeAll() throws IOException {
        dataHandler = new DataStreamHandler<>();
    }
    // submit indexing and search requests directly to a data stream.
    private static final String expectedName = "filebeat-8.4.0";
    private static final String documentId = "w8oaG4MBofZLtdMQC5Ro";
    List<String> expectedLogFields = List.of(
            "@timestamp",
            "host",
            "ip",
            "mac",
            "hostname",
            "id",
            "error",
            "log",
            "message",
            "input");

    @Test
    void submitGetRequestTest() throws IOException {
        GetResponse<ObjectNode> response = dataHandler.submitGetRequest(documentId);

        assertTrue(response.found());
        ObjectNode json = response.source();

        logger.info(" ObjectNode json = response.source() {}", json);

        assert json != null;
        assertEquals(json.getNodeType().name(), "OBJECT");
        assertEquals(json.size(), 8);

        for(int i =0; i<json.size();i++){
            logger.info(expectedLogFields.get(i), json.get(i)); //Iterator<Map.Entry<String, JsonNode>> fields()
        }
        assertThat(json.get("@timestamp").asText())
                .isEqualTo("2022-09-08T03:17:12.593Z");

        assertThat(json.get("host").get("hostname").asText())
                .isEqualTo("stews");

        assertThat(json.get("host").get("id"))
                .isNotNull();

        assertThat(json.get("log").toString())
                .isEqualTo("{\"file\":{\"path\":\"C:\\\\Users\\\\seost\\\\repositories\\\\WebSockets\\\\spring-websocket\\\\logs\\\\spring-boot-websocket.log\"},\"offset\":558254}");

        assertThat(json.get("input").toString())
                .isEqualTo("{\"type\":\"filestream\"}");

        assertThat(json.get("message").toString()).
                isEqualTo("\"[2022-09-07 20:03:07.671] \\u001B[39mTRACE\\u001B[0;39m o.s.b.w.s.f.OrderedRequestContextFilter [http-nio-8080-exec-1] - Cleared thread-bound request context: org.apache.catalina.connector.RequestFacade@76770b3a \"");

        assertThat(json.get("error").toString())
                .isEqualTo("{\"message\":\"Error decoding JSON: invalid character '-' after array element\",\"type\":\"json\"}");

    }

    @Test
    void submitGetDataStreamRequestTest() throws IOException {
        GetDataStreamResponse response = dataHandler.submitGetDataStreamRequest();

        assertNotNull(response);
        assertEquals(response.dataStreams().size(),1);
        assertEquals(response.dataStreams().get(0).toString(),"" +
                "DataStream: {\"name\":\"filebeat-8.4.0\"," + // name of data stream
                "\"timestamp_field\":{\"name\":\"@timestamp\"}," +
                "\"indices\":[{\"index_name\":\".ds-filebeat-8.4.0-2022.09.08-000001\"," + // backing index
                "\"index_uuid\":\"rqTIVxkdTxCU8-k2ukAVcg\"}]," +
                "\"generation\":1," +
                "\"template\":\"filebeat-8.4.0\"," + // matching index template
                "\"hidden\":false," +
                "\"replicated\":false," +
                "\"system\":false," +
                "\"status\":\"green\"," +
                "\"ilm_policy\":\"filebeat\"," + // index lifecycle policy
                "\"allow_custom_routing\":false}");
    }

    private List<FieldAndFormat> fields;
    private SourceConfig source;
    private JsonpDeserializer<?> tDocumentSerializer;
    private Map<String, Aggregation> aggregations;

    @Test
    void submitSearchRequestTest() throws IOException {
        dataHandler = new DataStreamHandler<>();
        SearchResponse<ObjectNode> response = dataHandler.submitSearchRequest("@timestamp", "2022-09-08T03:17:12.593Z");
        assertThat(response).isNotNull();
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            logger.info("There are " + total.value() + " results");
        } else {
            logger.info("There are more than " + total.value() + " results");
        }
        List<Hit<ObjectNode>> hits = response.hits().hits();
        for (Hit<ObjectNode> hit : hits) {
            ObjectNode node = hit.source();
            logger.info("index: "+ hit.index() + "\nid: " + hit.id() + ", score " + hit.score());

            logger.info("fields from spring boot error log document: " + node.get("host").get("hostname") +
                    "\ndefault number of fields appended in Spring Boot log: " + node.get("host").size() +
                    "\ninput type: " + node.get("input") +
                     "\nscore " + hit.score());
        }
    }

    // test that the stream automatically routes the request to backing indices that store the stream’s data
    @Test
    void streamRoutesRequestToBackingIndicesTest(){

    }

    @Test
    void backingIndicesStoreStreamsDataTest() {

    }

}