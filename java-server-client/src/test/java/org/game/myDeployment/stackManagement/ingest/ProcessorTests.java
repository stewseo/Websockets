package org.game.myDeployment.stackManagement.ingest;

import co.elastic.clients.elasticsearch.ingest.GetPipelineResponse;
import co.elastic.clients.json.JsonpDeserializer;
import jakarta.json.stream.JsonParser;
import org.game.myDeployment.AbstractApiImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ProcessorTests extends AbstractApiImpl {
    public static IngestPipelineImpl ingestPipeline;

    @BeforeAll
    static void beforeAll() throws Exception {
        ingestPipeline = new IngestPipelineImpl();
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
                                                .patternDefinitions(Map.of("IP","match alias")
                                                ))));
    }
    // get all ingest pipelines
    @Test
    void getPipelinesTest() throws Exception {
        GetPipelineResponse pipelineResponse = ingestPipeline.get("");
        assertThat(client.ingest().getPipeline()).isEqualTo("test");
    }

    @Test
    void getGrokPatternTest() throws IOException {
        assertThat(client.ingest().processorGrok().patterns().size()).isEqualTo(5);
    }

    @Test
    void createIngestPipelineTest(){
    }

    void parseResourceTest() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        FileReader file = new FileReader(new File("C:\\Users\\seost\\repositories\\WebSockets\\java-server-client\\src\\test\\resources\\processors.json"));

//        PutPipelineResponse actual = ingestPipeline.put("websocket-handler-logs-pipeline","processors.json");
        assertThat(file).isNotNull();


//
//
//        assertThat(input).isNotNull();
//
        JsonpDeserializer<List<String>> deser = JsonpDeserializer.arrayDeserializer(JsonpDeserializer.stringDeserializer());
//
        assertThat(deser).isNotNull();
//
        JsonParser parser = mapper.jsonProvider().createParser(file);
//
//        List<String> list = deser.deserialize(parser, mapper);
//
//        list.forEach(field -> {
//            assertThat(field).isEqualTo("test");
//        });
    }
}