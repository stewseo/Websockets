package org.game.deployment.management.stack;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.game.deployment.management.stack.data.index_management.DataStreamHandler;
import org.game.deployment.management.stack.data.index_management.IndexHandler;
import org.game.deployment.management.stack.ingest.IngestPipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

// Initializes instances of:
// an ElasticsearchClient with Transport that will handle:
// http communication to an AWS EC cluster managed by an Elasticsearch service
// adding a header for ApiKey Authentication with an api key encoded to a base64 String: "{ES_API_KEY} ":" {ES_API_KEY_SECRET}"
public abstract class Handler<T extends Comparable<? super T>> implements Comparator<Handler<T>> {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    private static final String protocol = "https";
    private static final String serviceEndpoint = "552e1fdcded8402295b5ff0c5afcc412.us-west-1.aws.found.io";
    private static final String urlAlias = "https://my-deployment-ba6b64.es.us-west-1.aws.found.io"; // initialize cluster endpoint alias with https encoding protocol
    private static final int port = 443;
    private static RestClient httpClient; //Holds all of the variables needed to describe an HTTP connection to a host. This includes remote host, port and protocol.
    protected static JacksonJsonpMapper mapper;
    private static ElasticsearchTransport transport;
    protected static ElasticsearchClient client;

    // initialize an instance of
    static {
        // initialize an instance of RestClient with a HttpHost. Add Default header for authentication
        initHttpClient();

        // initialize an instance of ElasticsearchTransport that will use the RestClient instance and a JSON mapper
        initTransport();

        // initialize an instance of ElasticsearchClient that will use the ElasticsearchTransport instance
        client = new ElasticsearchClient(transport);
    }

    private String getApiPathParam;
    public abstract String getApiPathParam();

    // adding a header for ApiKey Authentication with an api key encoded to a base64 String: "{ES_API_KEY} ":" {ES_API_KEY_SECRET}"
    private static String encodeApiKeyToBase64(String apiKeyId, String apiKeySecret) {
        return java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                        .encodeToString((apiKeyId + ":" + apiKeySecret)
                                .getBytes(StandardCharsets.UTF_8));
    }


    private static void initHttpClient() {
        String apiKeyId = System.getenv("API_KEY_ID");
        String apiKeySecret = System.getenv("API_KEY_SECRET");
        String apiKey = encodeApiKeyToBase64(apiKeyId, apiKeySecret);

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(serviceEndpoint, port, protocol));

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        "ApiKey " + apiKey)};

        builder.setDefaultHeaders(defaultHeaders); //

        httpClient = builder.build();
    }
    private static void initTransport() {
        mapper = new JacksonJsonpMapper();
        // interface that extends Transport, the layer that allows ApiClients to send requests.
        transport = new RestClientTransport(
                httpClient,
                mapper
        );
    }

    protected JsonObject sendRequestUsingRestClient(Request request) throws IOException {
        Response response = httpClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonReader reader = Json.createReader(new StringReader(responseBody));
        return reader.readObject();
    }


    @Override
    public int compare(Handler<T> o1, Handler<T> o2) {
        return o1.getApiPathParam().compareTo(o2.getApiPathParam());
    }

    public static Comparator<Handler<?>> byApiPath =
            (Handler<?> o1, Handler<?> o2)
                    -> o1.getApiPathParam().compareTo(o2.getApiPathParam());

    public String getApiPathParam(Handler<?> o1) {
        String result = null;
        if (o1 instanceof DataStreamHandler<?> other) {
            result = other.getApiPathParam();
        }
        else if (o1 instanceof IndexHandler<?> other) {
            result = other.getApiPathParam();
        }
        else if (o1 instanceof IngestPipelineHandler<?> other) {
            result = other.getApiPathParam();
        }
        return result;
    }

    public JacksonJsonpMapper getJacksonJsonpMapper() {
        return mapper;
    }

}
