import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractTestCase {

    String endpoint1 = "https://my-deployment-ba6b64.es.us-west-1.aws.found.io/_security/oauth2/token";
    String endpoint2 = "https://552e1fdcded8402295b5ff0c5afcc412.us-west-1.aws.found.io/_security/oauth2/token";

    void testSendRequest(String request) throws IOException {


        String apiKeyId = System.getenv("API_KEY_ID");
        String apiKeySecret = System.getenv("API_KEY_SECRET");

        String apiKeyAuth =
                java.util.Base64.getEncoder()
                        .encodeToString((apiKeyId + ":" + apiKeySecret)
                                .getBytes(StandardCharsets.UTF_8));

        String password = System.getenv("ES_CLOUD_PASS");

        String c = String.format("""
                curl -u "elastic:%s" -H "Content-Type: application/json"
                -X POST -d {"grant_type":"client_credentials"} "https://my-deployment-ba6b64.es.us-west-1.aws.found.io/_security/oauth2/token"
                """, password);

        Process process = Runtime.getRuntime().exec(c);

        InputStream inputStream = process.getInputStream();

        String text = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        assertEquals(text, "test");
    }

}
