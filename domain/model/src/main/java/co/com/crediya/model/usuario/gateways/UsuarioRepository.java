package co.com.crediya.model.usuario.gateways;

import co.com.crediya.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
  Mono<Usuario> obtenerUsuarioPorId(Long idUsuario);

  Mono<Usuario> obtenerUsuarioPorDocumento(String documentoIdentidad);
}
