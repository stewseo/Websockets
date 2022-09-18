package org.game.deployment.management.stack.ingest;

import co.elastic.clients.elasticsearch.ingest.*;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.game.deployment.management.stack.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.Objects;

// manage how to transform data and load it into the cluster
// pipelines to remove or transform fields, extract values from text, and enrich your data before indexing
public class IngestPipelineHandler<T extends Comparable<? super T>> extends Handler<T> {
    private static final Logger logger = LoggerFactory.getLogger(IngestPipelineHandler.class);
    private String ingestPipelineApiPath = "/_ingest/pipeline";
    public static PipelineId PIPELINE_ID;

    private Map<String, String> pipelineFields;
    public IngestPipelineHandler() throws Exception {
    }

    public JsonObject submitGetPipelineRequest(PipelineId pipelineId) throws IOException {

        GetPipelineResponse pipeline = client
                .ingest()
                .getPipeline(p ->
                        p.id(pipelineId.getPipelineId()));

        logger.info("Pipeline {}", pipeline);
        return Json.createReader(new StringReader(pipeline.toString())).readObject();
    }

    public JsonObject submitGetPipelineRequest(PipelineId pipelineId, String queryParam) throws Exception {
        GetPipelineResponse pipeline = client
                .ingest()
                .getPipeline(p ->
                        p.id(pipelineId.getPipelineId()));

        logger.info("Pipeline {}", pipeline);
        return Json.createReader(new StringReader(pipeline.toString())).readObject();
    }

    // processors, fields, descriptions, values
    public PutPipelineResponse submitPutRequestToIngestPipelineApi(String id, String resource) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        //open an input stream for the JSON resource file
        InputStream input = classloader.getResourceAsStream(resource);

        return client.ingest()
                .putPipeline(putPipelineRequestBuilder ->
                        putPipelineRequestBuilder
                                .id(id)
                                .description("ingest pipeline for spring boot and java trace, debug and info logs")
                                .processors(processorBuilder ->
                                        processorBuilder.withJson(input) // map grok, date
                                )
                );
    }

    public String getIngestPipelineApiPath() {
        return ingestPipelineApiPath;
    }

    public String getPipelineId() {
        return PIPELINE_ID.getPipelineId();
    }

    public void setPipelineId(PipelineId pipelineId) {
        PIPELINE_ID = pipelineId;
    }

    // 2 IngestPipelines are logically equivalent if their Api path parameters and PipelineId enums are equal. "_ingest/pipeline/{pipeline id}"
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IngestPipelineHandler<?> that)) return false;
        return Objects.equals(getApiPathParam(), that.getApiPathParam()) && Objects.equals(PIPELINE_ID, that.getPipelineId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getApiPathParam(), PIPELINE_ID);
    }

    @Override
    public String toString() {
        return String.format(
                "pipeline name: %s" +
                        "%n pipeline id: %s " +
                        PIPELINE_ID.name(),
                getPipelineId());
    }

    @Override
    public String getApiPathParam() {
        return getPipelineId();
    }
    public static enum PipelineId {
        TEST_PIPELINE("test_pipeline"),
        APP_SEARCH_CRAWLER_PIPELINE("app_search_crawler"),
        FLEET_FINAL_PIPELINE("app_search_crawler"),
        ENT_SEARCH_CONNECTOR_PIPELINE("ent_search_connector"),

        //Pipeline for parsing Elasticsearch audit logs
        FILEBEAT_ES_AUDIT_PIPELINE_JSON("filebeat-8.4.0-elasticsearch-audit-pipeline-json"), // in JSON format
        FILEBEAT_ES_AUDIT_PIPELINE_PLAINTEXT("filebeat-8.4.0-elasticsearch-audit-pipeline-plaintext"), // in plaintext format

        //"Pipeline for parsing the Elasticsearch server logs
        FILEBEAT_ES_SERVER_PIPELINE("filebeat-8.4.0-elasticsearch-server-pipeline"),  // Elasticsearch server log file in JSON format.
        FILEBEAT_ES_SERVER_PIPELINE_JSON7("filebeat-8.4.0-elasticsearch-server-pipeline-json-7"), // Elasticsearch 7.0 server log file in JSON format.
        FILEBEAT_ES_SERVER_PIPELINE_JSON8("filebeat-8.4.0-elasticsearch-server-pipeline-json-8"), // Elasticsearch 8.0 server log file in JSON format.
        FILEBEAT_ES_SERVER_PIPELINE_PLAINTEXT("filebeat-8.4.0-elasticsearch-server-pipeline-plaintext"), // Elasticsearch server log file in plaintext format.

        //Pipeline for parsing Kibana audit logs
        FILEBEAT_KIBANA_AUDIT_PIPELINE("filebeat-8.4.0-kibana-audit-pipeline"),
        FILEBEAT_KIBANA_AUDIT_PIPELINE_JSON("filebeat-8.4.0-kibana-audit-pipeline-json"), // in JSON format

        // Pipeline for parsing Kibana logs
        FILEBEAT_KIBANA_LOG_PIPELINE("filebeat-8.4.0-kibana-log-pipeline"), // Pipeline for parsing Kibana logs
        FILEBEAT_KIBANA_LOG_PIPELINE7("filebeat-8.4.0-kibana-log-pipeline-7"),
        FILEBEAT_KIBANA_LOG_PIPELINE_ECS("filebeat-8.4.0-kibana-log-pipeline-ecs"), // Kibana ecs logs

        //Pipeline for parsing logstash node logs
        FILEBEAT_LOGSTASH_LOG_PIPELINE("filebeat-8.4.0-logstash-log-pipeline"),
        FILEBEAT_LOGSTASH_LOG_PIPELINE_JSON("filebeat-8.4.0-logstash-log-pipeline-json"),
        FILEBEAT_LOGSTASH_LOG_PIPELINE_PLAINTEXT("filebeat-8.4.0-logstash-log-pipeline-plaintext");
        private final String pipelineId;

        PipelineId(String pipelineId) {
            this.pipelineId = pipelineId;
        }

        public String getPipelineId() {
            return pipelineId;
        }
    }
}
