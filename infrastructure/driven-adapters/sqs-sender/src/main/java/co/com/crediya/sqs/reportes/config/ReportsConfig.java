package co.com.crediya.sqs.reportes.config;

import co.com.crediya.model.solicitud.gateways.ReportsGateway;
import co.com.crediya.sqs.reportes.ReportsAdapter;
import co.com.crediya.sqs.sender.SQSSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportsConfig {

    @Bean
    public ReportsGateway reportsGateway(SQSSender sqsSender) {
        return new ReportsAdapter(sqsSender);
    }
}