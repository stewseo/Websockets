package org.example.rawWebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@SuppressWarnings("unused")
@Configuration /* generate bean definitions and service requests for @Bean methods at runtime */
@EnableWebSocket  /* configure processing WebSocket requests */
public class ServerWebSocketConfig implements WebSocketConfigurer { /* Defines callback methods to configure the WebSocket request handling */
    private static final Logger logger = LoggerFactory.getLogger(ServerWebSocketConfig.class);
    /* Configure a WebSocketHandler at the specified URL path  */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // protocol host port endpoint
        // ws://localhost:8080/websocket
        registry.addHandler(webSocketHandler(), "/websocket");
    }
    /*
    Indicates that a method produces a bean to be managed by the Spring container.
    bean named webSocketHandler will be available in the BeanFactory / ApplicationContext, bound to an object instance of type ServerWebSocketConfig:
    webSocketHandler -> org.example.ServerWebSocketConfig
    */
    @Bean
    public WebSocketHandler webSocketHandler() { /* A handler for WebSocket messages and lifecycle events. */
        return new ServerWebSocketHandler();
    }
}