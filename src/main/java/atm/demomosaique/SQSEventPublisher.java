package atm.demomosaique;

import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
//import com.amazonaws.services.sqs.AmazonSQS;
//import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SQSEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQSEventPublisher.class);

    @Autowired
    private SqsAsyncClient amazonSQS;

    @Autowired
    ObjectMapper objectMapper;

    public void publishEvent(JsonNode message) {
        LOGGER.info("Generating event : {}", message);


        try {
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl("http://localhost:4566/000000000000/my-queue")
                    .messageBody(objectMapper.writeValueAsString(message))
                    .build();
            amazonSQS.sendMessage(sendMessageRequest)
                    .thenAccept(resp -> {
                        System.out.println("MessageId: " + resp.messageId());
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
            LOGGER.info("Event has been published in SQS.");
        } catch (Exception e) {
            LOGGER.error("Exception ocurred while pushing event to sqs : {} and stacktrace ; {}", e.getMessage(), e);
        }

    }
}