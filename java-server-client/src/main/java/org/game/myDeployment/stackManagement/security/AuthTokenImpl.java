package org.game.myDeployment.stackManagement.security;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.game.myDeployment.AbstractApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class AuthTokenImpl extends AbstractApiImpl {

    private final static Logger logger = LoggerFactory.getLogger(AuthTokenImpl.class);
    private static String accessToken;
    private static String refreshToken;
    private String oauth2Endpoint = "/_security/oauth2/token";

    public AuthTokenImpl(){}

    public boolean create(String username, String password) throws IOException {
        String method = "POST";

        Request request = new Request(
                method, oauth2Endpoint);

        request.addParameter("pretty", "true");

        String json =
                String.format("{\"grant_type\":\"password\", " +
                                "\"username\":\"%s\", " +
                                "\"password\":\"%s\"}",
                        username,
                        password);

        request.setEntity(new NStringEntity(json, ContentType.APPLICATION_JSON));
        JsonObject jsonObject = sendRequestUsingRestClient(request);

        JsonValue newAuthToken = jsonObject.get("access_token");
        JsonValue newRefreshToken = jsonObject.get("refresh_token");

        if(newAuthToken != null && newRefreshToken != null) {
            accessToken = newAuthToken.toString();
            refreshToken = newRefreshToken.toString();
            return true;
        }
        return false;
    }

    public String get() throws Exception {
        return sendRequest("-X GET", oauth2Endpoint);
    }

    public String get(String tokenId) throws Exception {
        if(tokenId == null) {
            throw new Exception("Throw exception");
        }
        return sendRequest("-X GET", oauth2Endpoint + tokenId);
    }

    public String getAccessToken() {
        return accessToken;
    }

    private void setAuthToken(String authToken) {
        AuthTokenImpl.accessToken = authToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    private void setRefreshToken(String refreshToken) {
        AuthTokenImpl.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AuthTokenHandler{" +
                "oauth2Endpoint='" + oauth2Endpoint + '\'' +
                '}');
        if(accessToken != null){
            sb.append("\n auth token: ").append(accessToken);
        }

        if(refreshToken != null){
            sb.append("\n refresh token: ").append(refreshToken);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(oauth2Endpoint, accessToken, refreshToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthTokenImpl that)) return false;
        return Objects.equals(oauth2Endpoint, that.oauth2Endpoint) &&
                Objects.equals(accessToken, that.getAccessToken()) &&
                Objects.equals(refreshToken, that.getRefreshToken());
    }

}

