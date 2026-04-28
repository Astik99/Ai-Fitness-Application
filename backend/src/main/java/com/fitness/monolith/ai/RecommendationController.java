package com.fitness.monolith.ai;

import com.fitness.monolith.user.User;
import com.fitness.monolith.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Recommendation>> getUserRecommendations() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        List<Recommendation> recommendations = recommendationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return ResponseEntity.ok(recommendations);
    }
}
