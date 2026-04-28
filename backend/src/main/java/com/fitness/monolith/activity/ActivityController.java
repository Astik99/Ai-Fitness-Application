package com.fitness.monolith.activity;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<Activity> logActivity(@Valid @RequestBody ActivityRequest request) {
        Activity activity = activityService.logActivity(request);
        return ResponseEntity.ok(activity);
    }

    @GetMapping
    public ResponseEntity<List<Activity>> getUserActivities() {
        List<Activity> activities = activityService.getUserActivities();
        return ResponseEntity.ok(activities);
    }
}
