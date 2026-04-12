package com.smartpantry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** Inbound DTO for generating a meal plan. */
public class GenerateMealPlanRequest {

  @NotBlank(message = "Strategy name is required")
  private String strategyName;

  @Positive(message = "Days must be positive")
  private int days;

  public String getStrategyName() {
    return strategyName;
  }

  public void setStrategyName(String strategyName) {
    this.strategyName = strategyName;
  }

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }
}
