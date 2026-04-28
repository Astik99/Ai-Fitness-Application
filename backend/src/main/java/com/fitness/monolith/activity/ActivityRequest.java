package com.fitness.monolith.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActivityRequest {
    @NotBlank
    private String type;

    @NotNull
    private Integer durationMinutes;

    private Integer caloriesBurned;
}
