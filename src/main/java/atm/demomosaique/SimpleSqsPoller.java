package atm.demomosaique;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SimpleSqsPoller {

    private final SqsAsyncClient sqs;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Logger log = LoggerFactory.getLogger(SimpleSqsPoller.class);

    public SimpleSqsPoller(SqsAsyncClient sqs) {
        this.sqs = sqs;
        startPolling();
    }

    private void startPolling() {
        scheduler.scheduleWithFixedDelay(this::pollQueue, 0, 5, TimeUnit.SECONDS);
    }

    private void pollQueue() {
        sqs.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl("http://localhost:4566/000000000000/my-queue")
                        .maxNumberOfMessages(10)
                        .waitTimeSeconds(10)
                        .build())
                .thenAccept(response -> {
                    response.messages().forEach(this::processMessage);
                });
    }

    private void processMessage(Message msg) {
        String sqsUrl = "http://localhost:4566/000000000000/my-queue";
        log.info("Got message: {}", msg.body());
        // Delete after processing
        sqs.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(sqsUrl)
                .receiptHandle(msg.receiptHandle())
                .build());
    }
}
