package org.game.myDeployment.stackManagement.ingest;

import co.elastic.clients.elasticsearch.ingest.*;
import jakarta.json.stream.JsonParser;
import org.game.myDeployment.AbstractApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

// manage how to transform data and load it into the cluster
// pipelines to remove or transform fields, extract values from text, and enrich your data before indexing
public class IngestPipelineImpl extends AbstractApiImpl {
    private static final Logger logger = LoggerFactory.getLogger(IngestPipelineImpl.class);
    private String ingestEndpoint = "/_ingest/pipeline"; // ingest pipeline api endpoint id
    private Map<String, Pipeline> activePipelines; // active pipelines
    public IngestPipelineImpl() throws Exception {
        activePipelines = client.ingest().getPipeline().result();
    }

    // curl -X GET /_ingest/<ingest pipeline id>
    public GetPipelineResponse get(String pipelineId) throws Exception {
        if(pipelineId == null) {
            logger.info("method or endpoint null{}", pipelineId);
            throw new Exception("Throw exception");
        }
        GetPipelineResponse pipeline = client.ingest().getPipeline();
        logger.info("Pipeline {}", pipeline);

        return pipeline;
    }

    public String get(String method, String endpoint) throws Exception {
        if(method == null || endpoint == null) {
            logger.debug("method or endpoint null{}", endpoint);
            throw new Exception("Throw exception");
        }
        String getIngestPipeline = String.format(ingestEndpoint, endpoint);
        return sendRequest(method, getIngestPipeline);
    }

    // processors, fields, descriptions, values

    public PutPipelineResponse put(String id, String resource) throws IOException {
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

    public boolean delete(String pipelineId) throws IOException {
        String deleteIngestPipeline = "/_ingest/pipeline/" + pipelineId;
        return sendRequest("DELETE", deleteIngestPipeline) == null;
    }

    public String getIngestEndpoint() {
        return ingestEndpoint;
    }

    public Map<String, Pipeline> getPipelineMappings() {
        return activePipelines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IngestPipelineImpl that)) return false;
        return Objects.equals(ingestEndpoint, that.ingestEndpoint) && Objects.equals(activePipelines, that.activePipelines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingestEndpoint, activePipelines);
    }

    @Override
    public String toString() {
        return "number of ingest pipelines: {}" + activePipelines.size() + " ingest pipeline names: " + activePipelines;
    }
}
