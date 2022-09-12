package org.game.myDeployment.stackManagement.data.indexManagement;

import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// Test cases for ES Index Apis
public class IndexTests {
    public static IndexImpl indexClientImpl;

    @BeforeAll
    static void setup() throws IOException {
        indexClientImpl = new IndexImpl();
    }
    @Test
    void indexDoesNotExistTest() throws Exception {
        String index = "test";
        assertFalse(indexClientImpl.exists(index).value());
    }
    @Test
    void indexExistsTest() throws Exception {
        String index = "filebeat-8.4.0";
        assertTrue(indexClientImpl.exists(index).value());
    }
    @Test
    void createIndexIfNotExistsTest() throws IOException {
        String testIndexId = "my-index00001";
        assertFalse(indexClientImpl.createIfNotExists(testIndexId));
    }

    @Test
    void getAllIndicesTest() throws Exception {
        assertThat(indexClientImpl.getListOfIndices().size()).isEqualTo(5);
        assertThat(indexClientImpl.getListOfIndices()).isEqualTo(List.of("""
                IndicesRecord: {"health":"green","status":"open","index":"kibana_sample_data_ecommerce","uuid":"xVxT1y1oQ6O61mvsl0U-2g","pri":"1","rep":"1","docs.count":"4675","docs.deleted":"0","store.size":"8.8mb","pri.store.size":"4.4mb"}",
                IndicesRecord: {"health":"green","status":"open","index":"my-index00001","uuid":"stiMtX0rT0OqSEfSThd3dA","pri":"1","rep":"1","docs.count":"0","docs.deleted":"0","store.size":"450b","pri.store.size":"225b"}",
                IndicesRecord: {"health":"green","status":"open","index":".ds-logs-enterprise_search.api-default-2022.09.07-000001","uuid":"OA7R1K9VSKKCZ6z9_c2ugg","pri":"1","rep":"1","docs.count":"1","docs.deleted":"0","store.size":"28.7kb","pri.store.size":"14.3kb"}",
                IndicesRecord: {"health":"green","status":"open","index":".ds-logs-enterprise_search.audit-default-2022.09.07-000001","uuid":"xRXXJV71Q2SaFMxyiR7S5A","pri":"1","rep":"1","docs.count":"11","docs.deleted":"0","store.size":"237.4kb","pri.store.size":"126.6kb"}",
                IndicesRecord: {"health":"green","status":"open","index":".ds-filebeat-8.4.0-2022.09.08-000001","uuid":"rqTIVxkdTxCU8-k2ukAVcg","pri":"1","rep":"1","docs.count":"11101","docs.deleted":"0","store.size":"3.3mb","pri.store.size":"1.7mb"}
                """));
    }
    // Test creating List<String> indexTemplates

    @Test
    void deleteIndexTest() {

    }
    //TODO: create Watcher to track filebeat and metricbeat indices
    @Test
    void getBackingIndicesTest() throws Exception {
        List<String> list = indexClientImpl.getListOfIndices().stream().filter(e->e.contains(".ds-filebeat-8.4.0-2022.09.08-000001")).toList();
//        JsonObject jsonObject = indexClientImpl.getJacksonJsonpMapper().jsonProvider().createReader(new StringReader(list.get(0))).readObject();
        assertThat(list.toString()).isEqualTo("" +
                "IndicesRecord: {" +
                "\"health\":\"green\"," +
                "\"status\":\"open\"," +
                "\"index\":\".ds-filebeat-8.4.0-2022.09.08-000001\"," +
                "\"uuid\":\"rqTIVxkdTxCU8-k2ukAVcg\",\"pri\":\"1\",\"rep\":\"1\"," +
                "\"docs.count\":\"11101\",\"docs.deleted\":\"0\",\"store.size\":\"3.3mb\"," +
                "\"pri.store.size\":\"1.7mb\"}");
    }
    //Indices backing datastreams
}