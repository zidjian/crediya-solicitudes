package co.com.crediya.r2dbcmysql.config;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.asyncer.r2dbc.mysql.constant.SslMode;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MysqlConnectionPool {
  public static final int INITIAL_SIZE = 12;
  public static final int MAX_SIZE = 15;
  public static final int MAX_IDLE_TIME = 30;
  public static final int DEFAULT_PORT = 3306;

  public MySqlConnectionConfiguration getConnectionConfig(MysqlConnectionProperties properties) {
    return MySqlConnectionConfiguration.builder()
        .host(properties.host())
        .port(properties.port() != null ? properties.port() : DEFAULT_PORT)
        .database(properties.database())
        .username(properties.username())
        .password(properties.password())
        .sslMode(SslMode.DISABLED)
        .build();
  }

  @Bean
  public ConnectionPool connectionPool(MysqlConnectionProperties properties) {
    MySqlConnectionConfiguration configuration = getConnectionConfig(properties);

    ConnectionFactory connectionFactory = MySqlConnectionFactory.from(configuration);

    ConnectionPoolConfiguration poolConfiguration =
        ConnectionPoolConfiguration.builder(connectionFactory)
            .name("api-mysql-connection-pool")
            .initialSize(INITIAL_SIZE)
            .maxSize(MAX_SIZE)
            .maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME))
            .validationQuery("SELECT 1")
            .build();

    return new ConnectionPool(poolConfiguration);
  }
}
