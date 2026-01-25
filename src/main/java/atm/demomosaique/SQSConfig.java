package atm.demomosaique;

//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.client.builder.AwsClientBuilder;
//import com.amazonaws.services.sqs.AmazonSQSAsync;
//import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
//import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import io.awspring.cloud.sqs.listener.SqsMessageListenerContainer;
import io.awspring.cloud.sqs.listener.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;


import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Logger;

import static org.springframework.boot.convert.ApplicationConversionService.configure;

@Configuration
public class SQSConfig {

    //@Value(value = "${cloud.aws.region.static}")
    private String region = "eu-west-1";

    //@Value("${cloud.aws.credentials.access-key}")
    private String accessKeyId = "test";

    //@Value("${cloud.aws.credentials.secret-key}")
    private String secretAccessKey = "test";

    //@Value("${cloud.aws.end-point.uri}")
    private String sqsUrl = "http://localhost:4566";

    Logger log = Logger.getLogger(SQSConfig.class.getName());

//    @Bean
//    @Primary
//    public AmazonSQSAsync amazonSQSAsync() {
//        return AmazonSQSAsyncClientBuilder.standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsUrl, region))
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
//                .build();
//    }

    @Bean
    @Primary
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(sqsUrl)) // LocalStack or custom endpoint
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                        )
                )
                .build();
    }



//    @Bean
//    public SqsMessageListenerContainer<String> myQueueListener(
//            SqsAsyncClient sqsAsyncClient) {
//
//        return SqsMessageListenerContainer.builder()
//                .sqsAsyncClient(sqsAsyncClient)
//                .queueNames("my-queue")
//                .messageListener(message -> {
//                                 log.info("Received: {}" + message.getPayload());
//                                 return AcknowledgmentProcessCallback.NOOP; // auto-delete
//                             })
//
//                            // Container options
//                .configure(options -> options
//                                    .maxConcurrentMessages(10)           // max parallel consumers
//                                    //.pollingTimeout(Duration.ofSeconds(20))  // long polling
//                                    //.maxConcurrentRequests(5)            // concurrent ReceiveMessage calls
//                                    //.visibilityTimeout(Duration.ofSeconds(30))
//                                    //.waitTime(Duration.ofSeconds(20))
//                                    //.errorHandler(error -> log.error("SQS error", error))
//                            );
//    }

//    @Bean
//    public QueueMessagingTemplate queueMessagingTemplate() {
//        return new QueueMessagingTemplate(amazonSQSAsync());
//    }

    @Bean
    public MappingJackson2MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter =
                new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setStrictContentTypeMatch(false);
        return converter;
    }

    @Bean
    public StringMessageConverter stringMessageConverter() {
        return new StringMessageConverter(StandardCharsets.UTF_8);
    }



    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}