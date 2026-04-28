package com.fitness.monolith.ai;

import com.fitness.monolith.activity.Activity;
import com.fitness.monolith.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RecommendationRepository recommendationRepository;

    public void generateRecommendation(User user, Activity activity) {

        String prompt = buildPrompt(user, activity);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            String url = apiUrl + "?key=" + apiKey;

            System.out.println("👉 Calling Gemini API...");
            System.out.println("URL: " + url);
            System.out.println("Request: " + requestBody);

            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            System.out.println("👉 Gemini Response: " + response);

            String generatedText = extractTextFromResponse(response);

            System.out.println("👉 Generated Recommendation: " + generatedText);

            Recommendation recommendation = new Recommendation();
            recommendation.setUser(user);
            recommendation.setContent(generatedText);

            recommendationRepository.save(recommendation);

            System.out.println("👉 Recommendation saved successfully!");

        } catch (Exception e) {
            System.err.println("❌ Error calling Gemini API:");
            e.printStackTrace();
        }
    }

    private String buildPrompt(User user, Activity activity) {
        return String.format(
                "You are an AI fitness coach. " +
                        "User Profile: Age %d, Weight %.1f kg, Height %.1f cm, Goals: %s. " +
                        "The user just logged: %d minutes of %s burning %d calories. " +
                        "Based on the user's profile, goals, and recent activity, provide a personalized fitness tip with recovery advice, workout improvement, or next-step guidance. If suggesting an exercise, briefly include proper form steps or key cues for correct execution. Keep it specific, actionable, motivating, and within 5 sentences.",

                user.getAge(),
                user.getWeight(),
                user.getHeight(),
                user.getFitnessGoals(),
                activity.getDurationMinutes(),
                activity.getType(),
                activity.getCaloriesBurned());
    }

    private String extractTextFromResponse(Map<String, Object> response) {

        try {
            if (response == null) {
                return "No response from AI service.";
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                return "No recommendation generated.";
            }

            Map<String, Object> firstCandidate = candidates.get(0);

            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                return "Keep going with your fitness journey!";
            }

            Object text = parts.get(0).get("text");

            return text != null ? text.toString()
                    : "Stay consistent and keep improving!";

        } catch (Exception e) {
            System.err.println("❌ Error parsing Gemini response:");
            e.printStackTrace();
            return "Keep up your fitness consistency!";
        }
    }
}