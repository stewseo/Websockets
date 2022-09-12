package org.game.myDeployment.management;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UrlTests {
    String protocol = "https";
    int port = 443;
    String aliasedEndpoint = "my-deployment-ba6b64.es.us-west-1.aws.found.io";
    String serviceEndpoint = "552e1fdcded8402295b5ff0c5afcc412.us-west-1.aws.found.io";
    String openApiSpecficationUrl = "https://api.elastic-cloud.com/api/v1/deployments";
    // url = protocol , endpoint, port

    private String getEnvironmentVar(String envId){
        return System.getenv(envId);
    }

    @Test
    void testPathing(){
        InputStream input = getClass().getResourceAsStream("processors.json");

        assertThat(input).isNotNull();
    }
    @Test
    void aliasedUrlTest() throws IOException {

        String apiKeyId = getEnvironmentVar("API_KEY_ID");
        String apiKeySecret = getEnvironmentVar("API_KEY_SECRET");

        String apiKeyAuth =
                java.util.Base64.getEncoder()
                        .encodeToString((apiKeyId + ":" + apiKeySecret)
                                .getBytes(StandardCharsets.UTF_8));

        String password = System.getenv("ES_CLOUD_PASS");
        String cloudAuth = System.getenv("ES_CLOUD_AUTH");

        //
        //curl -X POST -u "elastic:$ES_CLOUD_PASS" "https://my-deployment-ba6b64.es.us-west-1.aws.found.io/_security/oauth2/token" -H "Content-Type: application/json" -d'
        //        {
        //            "grant_type":"client_credentials"
        //        }'

        String c = String.format("""
                curl -X POST "elastic:%s" "https://my-deployment-ba6b64.es.us-west-1.aws.found.io/_security/oauth2/token"
                -H "Content-Type: application/json"
                {
                    "grant_type":"client_credentials"
                }
                """, password);
//
        Process process = Runtime.getRuntime().exec(c);

        InputStream inputStream = process.getInputStream();

        String text = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

    }
    @Test
    void serviceUrlTest() {

    }
//
//    @Test
//    void deployment(){
//        processBuilder.command(
//                new String[]{"curl", "-u", "-I", "GET", "https://my-deployment-ba6b64.es.us-west-1.aws.found.io/my-data-stream?pretty"});
//    }
}
