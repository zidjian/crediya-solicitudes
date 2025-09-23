package co.com.crediya.r2dbcmysql.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("solicitud")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SolicitudEntity {

  @Id
  @Column("id_solicitud")
  private Long idSolicitud;

  private BigDecimal monto;

  private LocalDate plazo;

  private String email;

  @Column("id_user")
  private String idUser;

  @Column("id_estado")
  private Long idEstado;

  @Column("id_tipo_prestamo")
  private Long idTipoPrestamo;
}
