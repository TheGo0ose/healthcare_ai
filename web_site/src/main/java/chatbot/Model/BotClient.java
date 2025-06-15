package chatbot.Model;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

public class BotClient {
    public static String sendMessageToBot(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:5000/predict"; // Изменено на /predict

        Map<String, String> json = new HashMap<>();
        json.put("message", userMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("answer")) { // Изменено на "answer"
                throw new RuntimeException("Неверный формат ответа от бота");
            }
            return body.get("answer").toString();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка связи с ботом: " + e.getMessage());
        }
    }
}