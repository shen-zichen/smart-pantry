package com.smartpantry.dto;

import java.time.LocalDate;
import java.util.List;

/** Outbound DTO for generated meal plans. */
public class MealPlanResponse {

  private Long id;
  private String strategyName;
  private List<RecipeResponse> recipes;
  private int days;
  private LocalDate createdDate;

  public MealPlanResponse() {}

  public MealPlanResponse(
      Long id, String strategyName, List<RecipeResponse> recipes, int days, LocalDate createdDate) {
    this.id = id;
    this.strategyName = strategyName;
    this.recipes = recipes;
    this.days = days;
    this.createdDate = createdDate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStrategyName() {
    return strategyName;
  }

  public void setStrategyName(String strategyName) {
    this.strategyName = strategyName;
  }

  public List<RecipeResponse> getRecipes() {
    return recipes;
  }

  public void setRecipes(List<RecipeResponse> recipes) {
    this.recipes = recipes;
  }

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }

  public LocalDate getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDate createdDate) {
    this.createdDate = createdDate;
  }
}
