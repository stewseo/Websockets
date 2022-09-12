package org.game.myDeployment.stackManagement.data.indexManagement;

import co.elastic.clients.elasticsearch.indices.DataStream;
import co.elastic.clients.elasticsearch.indices.DataStreamsStatsResponse;
import co.elastic.clients.elasticsearch.indices.GetDataStreamResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import org.game.myDeployment.stackManagement.data.indexManagement.DataStreamImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DataStreamApiImplTests {
    public static DataStreamImpl datastreamImpl;
    private static List<String> dataStreamIds;
    @BeforeAll
    static void setup() throws IOException {
        datastreamImpl = new DataStreamImpl();
        dataStreamIds = List.of(
                "filebeat-8.4.0",
                "logs-enterprise_search.api-default",
                "logs-enterprise_search.audit-default");
    }
    @Test
    void getDataStreamByIdTest() throws Exception {
        GetDataStreamResponse dataStreamResponse = datastreamImpl.getDatastream();
        assertThat(dataStreamResponse.dataStreams().size()).isEqualTo(4);
        assertThat(dataStreamResponse
                .dataStreams()
                .stream()
                .map(DataStream::name)
                .toList())
                .isEqualTo(dataStreamIds);
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

        int size = datastreamImpl.getDatastreamStats(dataStreamIds.get(0)).dataStreamCount();

        for(int i = 0; i < size; i++){
            DataStreamsStatsResponse stats = datastreamImpl.getDatastreamStats(dataStreamIds.get(i));

            assertThat(stats.shards().toString())
                    .isEqualTo(shardStatistics.get(i));

            assertThat(stats.dataStreams().get(i).toString())
                    .isEqualTo(dataStreamsStatsItems.get(i));

            assertThat(stats.totalStoreSizeBytes())
                    .isEqualTo(3495213);
        }
    }
}