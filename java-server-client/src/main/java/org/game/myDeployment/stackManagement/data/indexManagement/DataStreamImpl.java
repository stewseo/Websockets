package org.game.myDeployment.stackManagement.data.indexManagement;

import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.indices.DataStream;
import co.elastic.clients.elasticsearch.indices.DataStreamsStatsResponse;
import co.elastic.clients.elasticsearch.indices.GetDataStreamResponse;
import co.elastic.clients.elasticsearch.indices.data_streams_stats.DataStreamsStatsItem;
import org.game.myDeployment.AbstractApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


public class DataStreamImpl extends AbstractApiImpl {

    private static final Logger logger = LoggerFactory.getLogger(DataStreamImpl.class);
    private List<DataStream> listOfDataStreams; // DataStreams Objects referencing: My Deployment/ElasticSearch/Data/Index Management/DataStream
    private static String myFilebeatInputId = "filebeat-8.4.0"; // datastream id for all Java/SpringBoot logs
    @SuppressWarnings("GrazieInspection")
    private DataStreamsStatsItem dataStreamStatsItem; // instance vars: int backingIndices, DataStream dataStream, String storeSize, int storeSizeBytes, long maximumTimestamp


    public GetDataStreamResponse get(String id) throws IOException {
        return client.indices().getDataStream(d -> d.name(id));
    }
    public DataStreamImpl() throws IOException {
        listOfDataStreams = client.indices().getDataStream().dataStreams(); // initialize list of DataStream Objects
        DataStreamsStatsResponse response = getDataStreamStats();
        dataStreamStatsItem = response
                .dataStreams()
                .stream()
                .filter(dataStreams ->
                        dataStreams.dataStream()
                                .contains(myFilebeatInputId))
                .toList()
                .get(0);

//        logger.info("GetDataStreamResponse dataStreamResponse = client.indices().getDataStream() {}", listOfDataStreams);
    }

    public GetDataStreamResponse getDatastream() throws Exception {
        return client.indices().getDataStream();// curl -X GET /_datastream
    }

    public DataStreamsStatsResponse getDataStreamStats() throws IOException {
        return client.indices().dataStreamsStats();
    }
    public DataStreamsStatsResponse getDatastreamStats(String dataStreamId) throws Exception {
        DataStreamsStatsResponse dataStreamsStatsResponse = client.indices().dataStreamsStats(e ->
                e.name(dataStreamId)
        );
        List<DataStreamsStatsItem> listOfDataStreamStatItems = dataStreamsStatsResponse.dataStreams();
        String totalStoreSizes = dataStreamsStatsResponse.totalStoreSizes();
        int backingIndices = dataStreamsStatsResponse.backingIndices();
        ShardStatistics dataStreamShardStats = dataStreamsStatsResponse.shards();

        return dataStreamsStatsResponse;
    }

    @Override
    public String toString() {
        return "DataStreamApiImpl{" +
                "listOfDataStreams=" + listOfDataStreams +
                '}';
    }



    // TODO: change name of alias to fantasyFootballStats-dataStream-id
    // TODO: use scribeJava Api or apache HttpComponents to build a Http Host Connection to the yahoo fantasy football dev api endpoint with an https protocol for an oauth0 access token
    // TODO: send a second request when the access token is expired using the refresh token with headers: and body:
}
