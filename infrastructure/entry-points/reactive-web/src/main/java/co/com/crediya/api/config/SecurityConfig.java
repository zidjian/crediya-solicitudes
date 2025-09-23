package co.com.crediya.api.config;

import co.com.crediya.api.security.JwtService;
import co.com.crediya.shared.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(customAuthenticationEntryPoint())
                    .accessDeniedHandler(customAccessDeniedHandler()))
        .authorizeExchange(
            exchanges ->
                exchanges
                    .pathMatchers(
                        "/webjars/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-resources/**")
                    .permitAll()
                    .pathMatchers(HttpMethod.POST, "/api/v1/solicitud")
                    .hasAnyRole("cliente", "administrador")
                    .pathMatchers(HttpMethod.GET, "/api/v1/solicitud")
                    .hasAnyRole("asesor", "administrador")
                    .pathMatchers(HttpMethod.PUT, "/api/v1/solicitud")
                    .hasAnyRole("asesor")
                    .pathMatchers(HttpMethod.GET, "/api/v1/solicitud/user/{idUser}")
                    .hasAnyRole("servicios")
                    .anyExchange()
                    .authenticated())
        .build();
  }

  @Bean
  public ServerAuthenticationEntryPoint customAuthenticationEntryPoint() {
    return (exchange, ex) -> {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

      ErrorResponse errorResponse =
          ErrorResponse.of(
              "UNAUTHORIZED",
              "Token inválido o no proporcionado",
              401,
              exchange.getRequest().getPath().value(),
              Collections.emptyList(),
              UUID.randomUUID().toString());

      try {
        String body = objectMapper.writeValueAsString(errorResponse);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
      } catch (Exception e) {
        String fallbackBody =
            "{\"codigo\":\"UNAUTHORIZED\",\"mensaje\":\"Token inválido o no proporcionado\",\"status\":401}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(fallbackBody.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
      }
    };
  }

  @Bean
  public ServerAccessDeniedHandler customAccessDeniedHandler() {
    return (exchange, denied) -> {
      exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
      exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

      ErrorResponse errorResponse =
          ErrorResponse.of(
              "FORBIDDEN",
              "Sin permisos suficientes para acceder a este recurso",
              403,
              exchange.getRequest().getPath().value(),
              Collections.emptyList(),
              UUID.randomUUID().toString());

      try {
        String body = objectMapper.writeValueAsString(errorResponse);
        org.springframework.core.io.buffer.DataBuffer buffer =
            exchange.getResponse().bufferFactory().wrap(body.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
      } catch (Exception e) {
        String fallbackBody =
            "{\"codigo\":\"FORBIDDEN\",\"mensaje\":\"Sin permisos suficientes\",\"status\":403}";
        org.springframework.core.io.buffer.DataBuffer buffer =
            exchange.getResponse().bufferFactory().wrap(fallbackBody.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
      }
    };
  }

  @Bean
  public AuthenticationWebFilter jwtAuthenticationWebFilter() {
    AuthenticationWebFilter filter =
        new AuthenticationWebFilter(jwtReactiveAuthenticationManager());
    filter.setServerAuthenticationConverter(jwtAuthenticationConverter());
    return filter;
  }

  @Bean
  public ServerAuthenticationConverter jwtAuthenticationConverter() {
    return this::convert;
  }

  @Bean
  public ReactiveAuthenticationManager jwtReactiveAuthenticationManager() {
    return authentication -> Mono.just(authentication);
  }

  private Mono<Authentication> convert(ServerWebExchange exchange) {
    String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return Mono.empty();
    }
    String token = authHeader.substring(7);
    try {
      if (jwtService.isTokenExpired(token)) {
        return Mono.empty();
      }
      String role = jwtService.getRolFromToken(token);
      AbstractAuthenticationToken auth = getAbstractAuthenticationToken(role, token);
      return Mono.just(auth);
    } catch (Exception e) {
      return Mono.empty();
    }
  }

  private AbstractAuthenticationToken getAbstractAuthenticationToken(String role, String token) {
    List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    AbstractAuthenticationToken auth =
        new AbstractAuthenticationToken(authorities) {
          @Override
          public Object getCredentials() {
            return token;
          }

          @Override
          public Object getPrincipal() {
            return jwtService.getUserIdFromToken(token);
          }
        };
    auth.setAuthenticated(true);
    return auth;
  }
}
