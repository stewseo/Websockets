package org.game.deployment.stackManagement.ingest;

import org.game.deployment.AbstractApiHandler;
import org.game.deployment.management.stack.ingest.IngestPipelineHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ProcessorTests extends AbstractApiHandler {
    private static IngestPipelineHandler ingestPipeline;
    @BeforeAll
    static void beforeAll() throws Exception {
        ingestPipeline = new IngestPipelineHandler();
    }
    @Test
    void grokProcessPatternDefinitionTest() throws IOException {

        client.ingest().putPipeline(putPipelineRequestBuilder ->
                //Missing required property 'GrokProcessor.patternDefinitions'
                // (JSON path: ['filebeat-8.4.0-logstash-slowlog-pipeline-plaintext'].processors[1].grok) (line no=1, column no=946, offset=-1)
                putPipelineRequestBuilder
                        .id("filebeat-8.4.0-logstash-slowlog-pipeline-plaintext")
                        .processors(processors -> //Processors used to perform transformations on documents before indexing.
                                processors.grok(grokProcessor -> // A map of pattern-name and pattern tuples defining custom patterns to be used by the current processor
                                        grokProcessor
                                                .field("message")
                                                .patterns("[\"%{IP:client} %{WORD:method} %{URIPATHPARAM:request} %{NUMBER:bytes:int} %{NUMBER:duration:double}\"]")
                                                .patternDefinitions(Map.of("IP","")
                                                ))));
    }

}