package atm.demomosaique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

@RestController
public class PublisherController {

    @Autowired
    private SQSEventPublisher sqsEventPublisher;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/sendMessage")
    public ResponseEntity sendMessage(@RequestBody JsonNode jsonNode) {
        sqsEventPublisher.publishEvent(jsonNode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sendMessageTEST")
    public ResponseEntity sendMessageTest() {
        String json = """
            {
              "type": "TEST",
              "payload": {
                "id": 1,
                "name": "hello"
              }
            }
            """;
        JsonNode jsonNode = objectMapper.readTree(json);
        sqsEventPublisher.publishEvent(jsonNode);
        return ResponseEntity.ok().build();
    }
}