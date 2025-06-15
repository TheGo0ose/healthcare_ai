package chatbot.Controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ChatController {
    @PostMapping("/chatbot")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        if (userMessage == null || userMessage.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Сообщение не может быть пустым");
            return ResponseEntity.badRequest().body(error);
        }

        String flaskUrl = "http://localhost:5000/predict"; // Изменено на /predict
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> request = new HashMap<>();
        request.put("message", userMessage);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("answer")) { // Изменено на "answer"
                Map<String, String> error = new HashMap<>();
                error.put("error", "Wrong format");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }

            String botResponse = body.get("answer").toString();
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("response", botResponse);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            System.err.println("Error with Flask: " + e.getMessage()); // Логируем точную ошибку
            Map<String, String> error = new HashMap<>();
            error.put("error", "Connection error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}