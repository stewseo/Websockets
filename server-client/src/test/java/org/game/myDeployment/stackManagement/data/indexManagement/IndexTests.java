package org.game.myDeployment.stackManagement.data.indexManagement;

import co.elastic.clients.elasticsearch._types.ExpandWildcard;
import co.elastic.clients.elasticsearch.cat.CountResponse;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.game.myDeployment.stackManagement.data.indexManagement.IndexHandler.IndexId.DS_FILEBEAT_IDX;
import static org.junit.jupiter.api.Assertions.*;

// Test response from index api endpoint
public class IndexTests {
    public static IndexHandler indexHandler;

    private static List<String> systemIndices; // indices that are not backing a data stream that return a docs count greater than 0

    private static List<String> dataStreamBackingIndices; // The backing indices of a data stream that return a docs count greater than 0

    private static String FILESTREAM_INPUT_ID = ".ds-filebeat-8.4.0-2022.09.08-000001";

    @BeforeAll
    static void setup() throws IOException {
        indexHandler = new IndexHandler();
        // indices backing data streams
        dataStreamBackingIndices = List.of(".ds-filebeat-8.4.0-2022.09.08-000001", // backing index of DataStream filebeat-8.4.0
                ".ds-.slm-history-5-2022.09.07-000001", // backing index of DataStream slm-history-5
                ".ds-ilm-history-5-2022.09.07-000001"); // backing index of DataStream ilm-history-5

        systemIndices = List.of(".kibana_8.4.1_001",
                ".kibana_task_manager_8.4.1_001",
                ".security-tokens-7");
    }

    @Test
    void indexDoesNotExistTest() throws Exception {

        // for each: should log [%timestamp] TRACE o.g.m.s.d.i.IndexHandler [Test worker] - ElasticsearchIndicesClient elasticsearchIndicesClient = client.indices() false
        for (String index : dataStreamBackingIndices) {
            assertFalse(indexHandler.exists(index.replace(".", "\\s+")).value());
        }

        for (String index : systemIndices) {
            assertFalse(indexHandler.exists(index.replace(".", "\\s+")).value());
        }
    }

    @Test
    void indexExistsTest() throws Exception {
        // for each: [%timestamp] TRACE o.g.m.s.d.i.IndexHandler [Test worker] - ElasticsearchIndicesClient elasticsearchIndicesClient = client.indices() true
        for (String index : dataStreamBackingIndices) {
            assertTrue(indexHandler.exists(index).value());

        }
        for (String index : systemIndices) {
            assertTrue(indexHandler.exists(index).value());
        }
    }

    List<String> numberOfDocsInBackingIndices = List.of("11101", "461", "23");
    List<String> numberOfDocsInIndices = List.of("1269", "26", "18");

    @Test
    void numberOfDocumentsInIndexTest() throws IOException {

        for (int i = 0; i < dataStreamBackingIndices.size(); i++) {
            CountResponse countResponse = indexHandler.getNumberOfDocuments(dataStreamBackingIndices.get(i));
            assertThat(countResponse.valueBody().size()).isEqualTo(1);
            assertThat(countResponse.valueBody().get(0).count()).isEqualTo(numberOfDocsInBackingIndices.get(i));
            assertThat(countResponse.valueBody().get(0).epoch()).isOfAnyClassIn(Long.class); //Verify that the actual value type is long
            assertThat(countResponse.valueBody().get(0).timestamp()).matches("[0-9]+:[0-9]+:[0-9]+");
        }

        for (int i = 0; i < systemIndices.size(); i++) {
            CountResponse countResponse = indexHandler.getNumberOfDocuments(systemIndices.get(i));
            assertThat(countResponse.valueBody().size()).isEqualTo(1);
            assertThat(countResponse.valueBody().get(0).count()).isEqualTo(numberOfDocsInIndices.get(i));
            assertThat(countResponse.valueBody().get(0).epoch()).isOfAnyClassIn(Long.class); //Verify that the actual value type is long
            assertThat(countResponse.valueBody().get(0).timestamp()).matches("[0-9]+:[0-9]+:[0-9]+");
        }
    }

    // metrics are retrieved directly from Lucene which Elasticsearch uses internally
    // to power indexing and search.
    // As a result, all document counts include hidden nested documents.
    @Test
    void indicesRecordTest() throws Exception {

        for (IndexHandler.IndexId indexId : IndexHandler.IndexId.values()) {

            ExpandWildcard typeOfIndex = ExpandWildcard.All; // type of pattern that wildcard patterns can match

            List<IndicesRecord> indicesRecord = indexHandler.getAllIndicesRecord(indexId.getIndexId(), typeOfIndex); // Get Request to _cat indices api

            assertThat(indicesRecord.size()).isEqualTo(1);
            IndicesRecord index = indicesRecord.get(0);

            assertThat(index.status()).isEqualTo("open");

            assertThat(index.health()).isEqualTo("green");

            switch (indexId) {
                case DS_FILEBEAT_IDX -> {
                    assertThat(index.docsCount()).isEqualTo("11101");
                    assertThat(index.storeSize()).isEqualTo("3.3mb");
                    assertThat(index.priStoreSize()).isEqualTo("1.7mb");
                }
                case KIBANA_IDX -> {
                    assertThat(index.docsCount()).isGreaterThan("3111");
                    assertThat(index.storeSize()).isEqualTo("10.4mb");
                    assertThat(index.priStoreSize()).isGreaterThan("4.4mb");
                }
                case KIBANA_TASK_MANAGER_IDX -> {
                    assertThat(index.docsCount()).isGreaterThan("25");
                    assertThat(index.storeSize()).isGreaterThan("40mb");
                    assertThat(index.priStoreSize()).isGreaterThan("415kb");
                }

                case DS_ILM_HISTORY_IDX -> {
                    assertThat(index.docsCount()).isEqualTo("23");
                    assertThat(index.storeSize()).isEqualTo("115.9kb");
                    assertThat(index.priStoreSize()).isEqualTo("57.9kb");
                }
                case DS_SLM_HISTORY_IDX -> {
                    assertThat(index.docsCount()).isGreaterThan("505");
                    assertThat(index.storeSize()).isGreaterThan("407.7kb");
                    assertThat(index.priStoreSize()).isGreaterThan("209.7kb");
                }

                case DS_SEARCH_AUDIT_IDX -> {
                    assertThat(index.docsCount()).isGreaterThan("12");
                    assertThat(index.storeSize()).isGreaterThan("301.5kb");
                    assertThat(index.priStoreSize()).isGreaterThan("158.6kb");
                }
                case DS_SEARCH_API_IDX -> {
                    assertThat(index.docsCount()).isEqualTo("1");
                    assertThat(index.storeSize()).isEqualTo("28.7kb");
                    assertThat(index.priStoreSize()).isEqualTo("14.3kb");
                }

                default -> assertThat(index.index()).isNotNull();
            }
        }
    }

    List<String> docIds = List.of("w8oaG4MBofZLtdMQC5Ro",
            "w8oaG4MBofZLtdMQC5Ro",
            "qcoaG4MBofZLtdMQC5RE",
            "rMoaG4MBofZLtdMQC5Ro",
            "DcoaG4MBofZLtdMQC5WN",
            "VcoaG4MBofZLtdMQA4rg");

    //GET .ds-filebeat-8.4.0-2022.09.08-000001/_doc/w8oaG4MBofZLtdMQC5Ro
    @Test
    void getDocumentByIdTest() throws IOException {
        GetResponse<ObjectNode> response = indexHandler.get(".ds-filebeat-8.4.0-2022.09.08-000001", docIds.get(0));

        assertThat(response.id()).isEqualTo("w8oaG4MBofZLtdMQC5Ro");
        assertThat(response.index()).isEqualTo(".ds-filebeat-8.4.0-2022.09.08-000001");
        ObjectNode node = response.source();
        assertNotNull(node);

        node.fields().forEachRemaining(e -> {
            switch (e.getKey()) {
                case "@timestamp" -> assertThat(e.getValue()).isNotNull(); //matches("(\\d{4}-\\d{2}-\\d{2})[A-Z]+(\\d{2}:\\d{2}:\\d{2}).(\\d{3})[A-Z]

                case "host" -> {
                    assertThat(e.getValue().size()).isEqualTo(7);
                }

                case "agent" -> assertThat(e.getValue().size()).isEqualTo(5);

                case "error" -> {
                    int size = e.getValue().size();
                    assertThat(size).isEqualTo(2);

                    List<String> keys = List.of("message", "type");
                    List<String> values = List.of("Error decoding JSON: invalid character '-' after array element", "json");

                    for(int i = 0; i<size; i++) {
                        assertThat(e.getValue().get(keys.get(i)).asText()).isEqualTo(values.get(i));
                    }
                }

                case "log" -> assertThat(e.getValue().size()).isEqualTo(2);

                case "message" -> assertThat(e.getValue().toString())
                        .isEqualTo("\"[2022-09-07 20:03:07.671] " +
                                "\\u001B[39mTRACE\\u001B[0;39m o.s.b.w.s.f.OrderedRequestContextFilter " +
                                "[http-nio-8080-exec-1] - Cleared thread-bound request context: " +
                                "org.apache.catalina.connector.RequestFacade@76770b3a \"");

                case "input" -> assertThat(e.getValue().size()).isEqualTo(1);

                case "ecs" -> assertThat(e.getValue().size()).isEqualTo(1);
            }
        });
    }

    @Test
    void createOrUpdateTest(){

    }

    @Test
    void upsertTest(){

    }

}