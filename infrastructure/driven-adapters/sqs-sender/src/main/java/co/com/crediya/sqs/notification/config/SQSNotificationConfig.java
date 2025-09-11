package co.com.crediya.sqs.notification.config;

import co.com.crediya.model.solicitud.gateways.NotificationGateway;
import co.com.crediya.sqs.notification.SQSNotificationAdapter;
import co.com.crediya.sqs.sender.SQSSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SQSNotificationConfig {

    @Bean
    public NotificationGateway notificationGateway(SQSSender sqsSender) {
        return new SQSNotificationAdapter(sqsSender);
    }
}
