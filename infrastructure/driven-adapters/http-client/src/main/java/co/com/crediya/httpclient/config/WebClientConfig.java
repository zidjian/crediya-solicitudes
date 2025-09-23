package co.com.crediya.httpclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

  @Value("${webclient.timeout.connect:5000}")
  private int connectTimeout;

  @Value("${webclient.timeout.response:10000}")
  private int responseTimeout;

  @Bean
  public WebClient webClient() {
    HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(responseTimeout));

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
        .build();
  }
}
