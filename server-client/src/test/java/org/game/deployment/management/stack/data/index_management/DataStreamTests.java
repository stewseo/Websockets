package org.game.deployment.stackManagement.data.indexManagement;

import co.elastic.clients.elasticsearch.indices.DataStream;
import co.elastic.clients.elasticsearch.indices.DataStreamsStatsResponse;
import co.elastic.clients.elasticsearch.indices.GetDataStreamResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.game.deployment.management.stack.data.index_management.DataStreamHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataStreamTests {
    private static DataStreamHandler datastreamImpl;
    private static String dataStreamIds;
    @BeforeAll
    static void setup() throws IOException {
        datastreamImpl = new DataStreamHandler();
        dataStreamIds = "filebeat-8.4.0";
    }
    @Test
    void getDataStreamByIdTest() throws Exception {
        GetDataStreamResponse dataStreamResponse = datastreamImpl.getDatastream();

        assertThat(dataStreamResponse
                .dataStreams()
                .size())
                .isEqualTo(4);

        assertThat(dataStreamResponse
                .dataStreams()
                .stream()
                .map(DataStream::name)
                .filter(name ->
                        name.equals(dataStreamIds))
                .toList()
                .size())
                .isEqualTo(1);
    }

    @Test
    void getDataStreamStatsTest() throws Exception {
        List<String> shardStatistics = List.of(
                "ShardStatistics: " +
                        "{\"failed\":0.0," +
                        "\"successful\":2.0," +
                        "\"total\":2.0}");

        List<String> dataStreamsStatsItems = List.of("DataStreamsStatsItem: " +
                "{\"backing_indices\":1," +
                "\"data_stream\":\"filebeat-8.4.0\"," +
                "\"store_size_bytes\":3495213," +
                "\"maximum_timestamp\":1662607032669}");

        List<Integer> totalStoreSizeBytes = List.of(3495213);

        int size = datastreamImpl.getDatastreamStats(dataStreamIds).dataStreamCount();

        for(int i = 0; i < size; i++){
            DataStreamsStatsResponse stats = datastreamImpl.getDatastreamStats(dataStreamIds);

            assertThat(stats.shards().toString())
                    .isEqualTo(shardStatistics.get(i));

            assertThat(stats.dataStreams().get(i).toString())
                    .isEqualTo(dataStreamsStatsItems.get(i));

            assertThat(stats.totalStoreSizeBytes())
                    .isEqualTo(3495213);
        }
    }

    @Test
    void getTotalNumberOfDocsTest() throws IOException {
        ObjectNode json = datastreamImpl.getTotalNumberOfDocs(".ds-filebeat-8.4.0-2022.09.08-000001");
        assertThat(json.size()).isEqualTo(1);
    }
}