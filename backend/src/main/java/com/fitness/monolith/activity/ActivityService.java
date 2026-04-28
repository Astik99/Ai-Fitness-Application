package com.fitness.monolith.activity;

import com.fitness.monolith.ai.GeminiAiService;
import com.fitness.monolith.user.User;
import com.fitness.monolith.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private GeminiAiService aiService;

    public Activity logActivity(ActivityRequest request) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        Activity activity = new Activity();
        activity.setUser(user);
        activity.setType(request.getType());
        activity.setDurationMinutes(request.getDurationMinutes());
        activity.setCaloriesBurned(request.getCaloriesBurned());

        Activity savedActivity = activityRepository.save(activity);
        
        // Trigger AI recommendation generation async or sync (sync for simplicity here)
        try {
            aiService.generateRecommendation(user, savedActivity);
        } catch (Exception e) {
            // Log error but don't fail the activity logging
            System.err.println("Failed to generate AI recommendation: " + e.getMessage());
        }

        return savedActivity;
    }

    public List<Activity> getUserActivities() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }
        return activityRepository.findByUserIdOrderByActivityDateDesc(user.getId());
    }
}
