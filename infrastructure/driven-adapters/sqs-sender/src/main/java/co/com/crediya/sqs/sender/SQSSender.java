package co.com.crediya.sqs.sender;

import co.com.crediya.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender /*implements SomeGateway*/ {
    private final SQSSenderProperties properties;
    private final @Qualifier("sqsSenderClient") SqsAsyncClient client;

    public Mono<String> send(String message) {
        return send(message, properties.queueUrl());
    }

    public Mono<String> send(String message, String queueUrl) {
        return Mono.fromCallable(() -> buildRequest(message, queueUrl))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return buildRequest(message, properties.queueUrl());
    }

    private SendMessageRequest buildRequest(String message, String queueUrl) {
        SendMessageRequest.Builder builder = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message);

        // Add MessageGroupId and MessageDeduplicationId for FIFO queues
        if (isFifoQueue(queueUrl)) {
            builder.messageGroupId("default-group");
            builder.messageDeduplicationId(generateMessageDeduplicationId(message));
        }

        return builder.build();
    }

    private String generateMessageDeduplicationId(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.warn("MD5 algorithm not available, using message hashCode as fallback", e);
            return String.valueOf(message.hashCode());
        }
    }

    private boolean isFifoQueue(String queueUrl) {
        return queueUrl != null && queueUrl.endsWith(".fifo");
    }
}
