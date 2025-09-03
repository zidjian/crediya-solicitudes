package co.com.crediya.config;

import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HttpClientConfig {

    // Bean WebClient eliminado para evitar conflicto con WebClientConfig
    // Se usa el WebClient configurado en el módulo http-client
}
