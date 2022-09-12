package org.game.myDeployment;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.json.jsonb.JsonbJsonpMapper;
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

// Sends a Request to aliased endpoint endpoint: "my-deployment-ba6b64.es.us-west-1.aws.found.io" or
// service endpoint:
// Reads a Response and appends lines to $PROJECT_HOME/logs
// Decodes a Json String to Java Objects
// TODO: Encode Java Objects to a Json String
public abstract class AbstractApiImpl {
    private static final Logger logger = LoggerFactory.getLogger(AbstractApiImpl.class);
    protected static final String aliasedUrl = "https://my-deployment-ba6b64.es.us-west-1.aws.found.io";    // initialize aliased endpoint with protocol
    private static final int port = 443; // initialize endpoint port
    private static String apiKeyAuth; // declare api key to be used for authentication

    static {
        // Initialize the apiKey id and apiKey secret
        String apiKeyId = System.getenv("API_KEY_ID");
        String apiKeySecret = System.getenv("API_KEY_SECRET");

        apiKeyAuth =
                java.util.Base64.getEncoder() // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set
                        .encodeToString((apiKeyId + ":" + apiKeySecret)
                                .getBytes(StandardCharsets.UTF_8));

        initRestClient(); // Initializes a RestClient Object with: a http host pointing to the cluster service url, a Header for ApiKey authorization
        initClient(); // Initializes an ElasticsearchClient Object with: the RestClient, a JacksonJsonpMapper that extends JsonpMapperBase.

    }

    public String sendRequest(String method, String apiEndpoint) throws IOException {
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

        curl.append(aliasedUrl)
                .append(apiEndpoint);

        Process process = Runtime.getRuntime().exec(curl.toString());

        InputStream inputStream = process.getInputStream();

        String response = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        logger.info("executing curl {} ", response);

        return response;
    }
    private static RestClient httpClient;
    protected static ElasticsearchClient client;
    private static void initRestClient() {
        final String protocol = "https";
        final String serviceEndpoint = "552e1fdcded8402295b5ff0c5afcc412.us-west-1.aws.found.io";

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        "ApiKey " + apiKeyAuth)};

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(serviceEndpoint, port, protocol));

        builder.setDefaultHeaders(defaultHeaders);

        httpClient = builder.build();
    }
    protected static JacksonJsonpMapper mapper;

    private static void initClient() {
        mapper = new JacksonJsonpMapper();
        // interface that extends Transport, the layer that allows ApiClients to send requests.
        ElasticsearchTransport transport = new RestClientTransport(
                httpClient,
                mapper
        );
        client = new ElasticsearchClient(transport);
    }

    public JsonObject sendRequestUsingRestClient(Request request) throws IOException {
        if(httpClient == null){
            initRestClient();
        }
        if(client == null) {
            initClient();
        }

        Response response = httpClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonReader reader = Json.createReader(new StringReader(responseBody));
        return reader.readObject();
    }

    public JsonObject sendRequestUsingRestClient(String method, Request request) throws IOException {
        if(httpClient == null){
            initRestClient();
        }

        Response response = httpClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonReader reader = Json.createReader(new StringReader(responseBody));
        return reader.readObject();
    }

    public JacksonJsonpMapper getJacksonJsonpMapper() {
        return mapper;
    }
    public String getApiKeyAuth(String password) {
        if(password.equals(System.getenv("MY_PASSWORD"))) {
            return apiKeyAuth;
        }
        else return null;
    }

    protected String getAliasedUrl(){
        return aliasedUrl;
    }

}
