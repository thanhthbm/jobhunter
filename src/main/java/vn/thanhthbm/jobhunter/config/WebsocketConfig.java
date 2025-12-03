package vn.thanhthbm.jobhunter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
  private final JwtDecoder jwtDecoder;

  public WebsocketConfig(@Lazy JwtDecoder jwtDecoder) {
    this.jwtDecoder = jwtDecoder;
  }

  public void registerStompEndpoints(StompEndpointRegistry registry){
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*");
  }

  public void configureMessageBroker(MessageBrokerRegistry registry){
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
  }


  public void configureClientInboundChannel(ChannelRegistration registration){
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
          String authHeader = accessor.getFirstNativeHeader("Authorization");

          if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
              Jwt jwt = jwtDecoder.decode(token);

              JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt);
              accessor.setUser(authenticationToken);
            } catch (Exception e) {
              System.out.println("Websocket Token validation failed" + e.getMessage());
            }
          }
        }
        return message;
      }
    });
  }
}
