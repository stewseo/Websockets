package org.game.myDeployment;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

// Creates a single instance of a RestClient, TransportClient to be used by an ApiClient
// Reads a deserialized Response and appends lines to files matching glob pattern at path: $PROJECT_HOME/logs/*
// Decodes a Json String to Java Objects
// TODO: Encode Java Objects to a Json String
public abstract class AbstractApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractApiHandler.class);

    private static final String urlAlias = "https://my-deployment-ba6b64.es.us-west-1.aws.found.io"; // initialize cluster endpoint alias with https encoding protocol

    private static final int port = 443;

    private static String apiKeyAuth; // declare api key to be used for authentication

    private static RestClient httpClient;

    private static JacksonJsonpMapper mapper;

    private static ElasticsearchTransport transport;

    protected static ElasticsearchClient client;

    static {

        // initialize and build a RestClient instance with a HttpHost and a Default header for authentication
        initHttpClient();

        // initialize a Transport instance with the RestClient
        initTransport();

        // initialize a client with transport
        client = new ElasticsearchClient(transport);
    }

    private static Header[] defaultHeaders;

    private static void initAuthHeader() {
        // set the HTTP request header to Authorization: ApiKey $ENCODED_API_KEY
        String apiKeyId = System.getenv("API_KEY_ID");
        String apiKeySecret = System.getenv("API_KEY_SECRET");
        apiKeyAuth =
                java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                        .encodeToString((apiKeyId + ":" + apiKeySecret) // the Base64-encoding of the UTF-8 representation of the id and api_key joined by a colon (:)
                                .getBytes(StandardCharsets.UTF_8));
        defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        "ApiKey " + apiKeyAuth)};

    }

    private static void initHttpClient() {

        initAuthHeader();

        final String protocol = "https";
        final String serviceEndpoint = "552e1fdcded8402295b5ff0c5afcc412.us-west-1.aws.found.io";

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(serviceEndpoint, port, protocol));

        builder.setDefaultHeaders(defaultHeaders);

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

    protected String sendRequest(String method, String apiEndpoint) throws IOException {
        // valid request format:
        // curl -I <url>/<id> -H <authorization header>
        // curl -H <authorization> -I <url>/<id>
        // curl -I -H <authorization> <url>/<id>
        StringBuilder curl;
        //Add authorization header
        if(method != null) {
            curl = new StringBuilder(String.format(
                "curl -H \"Authorization: ApiKey %s\" %s ", apiKeyAuth, method));
        }

        else {
            curl = new StringBuilder(String.format(
                    "curl -H \"Authorization: ApiKey %s\" ", apiKeyAuth));
        }

        curl.append(urlAlias).append(apiEndpoint);

        Process process = Runtime.getRuntime().exec(curl.toString());

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        logger.info("executing curl {} ", response);

        return response;
    }

    protected JsonObject sendRequestUsingRestClient(Request request) throws IOException {

        Response response = httpClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonReader reader = Json.createReader(new StringReader(responseBody));
        return reader.readObject();
    }

    protected JsonObject sendRequestUsingRestClient(String method, Request request) throws IOException {
        if(method == null || request == null){
            throw new NullPointerException();
        }
        Response response = httpClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonReader reader = Json.createReader(new StringReader(responseBody));
        return reader.readObject();
    }

    public JacksonJsonpMapper getJacksonJsonpMapper() {
        return mapper;
    }

    protected String getEncodedApiKeyAndSecret(String password) {
        if(password.equals(System.getenv("MY_PASSWORD"))) {
            return apiKeyAuth;
        }
        else return null;
    }

}
