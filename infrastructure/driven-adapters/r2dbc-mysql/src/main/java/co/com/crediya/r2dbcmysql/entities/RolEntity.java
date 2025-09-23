package co.com.crediya.r2dbcmysql.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("rol")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RolEntity {

  @Id
  @Column("id_tipo_prestamo")
  private Long idEstado;

  private String nombre;

  private String descripcion;
}
