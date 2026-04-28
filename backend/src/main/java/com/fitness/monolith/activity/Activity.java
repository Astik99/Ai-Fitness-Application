package com.fitness.monolith.activity;

import com.fitness.monolith.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
@Data
@NoArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    private String type; // e.g., running, cycling, gym

    @NotNull
    private Integer durationMinutes;

    private Integer caloriesBurned;

    private LocalDateTime activityDate;

    @PrePersist
    protected void onCreate() {
        if (activityDate == null) {
            activityDate = LocalDateTime.now();
        }
    }
}
