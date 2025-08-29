package co.com.crediya.model.usuario.gateways;

import reactor.core.publisher.Mono;

public interface UsuarioValidacionRepository {
    Mono<Boolean> existeUsuarioPorDocumento(String documentoIdentidad);
}
