package com.project.project1.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/ws/{userId}/{sessionId}/websocket")
                .setAllowedOrigins("http://localhost:8015")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

    public WebSocketHandler myHandler() {
        return new WebSocketHandler();
    }
}
