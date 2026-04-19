package com.smartpantry.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/** Outbound DTO for generated meal plans. */
public class MealPlanResponse {

  private Long id;
  private String strategyName;
  private List<RecipeResponse> recipes;
  private int days;
  private LocalDate createdDate;
  private Set<Integer> cookedIndexes;
  private boolean requiresGroceryRun;

  public MealPlanResponse() {}

  public MealPlanResponse(
      Long id, String strategyName, List<RecipeResponse> recipes, int days, LocalDate createdDate,
      Set<Integer> cookedIndexes, boolean requiresGroceryRun) {
    this.id = id;
    this.strategyName = strategyName;
    this.recipes = recipes;
    this.days = days;
    this.createdDate = createdDate;
    this.cookedIndexes = cookedIndexes;
    this.requiresGroceryRun = requiresGroceryRun;
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

  public Set<Integer> getCookedIndexes() {
    return cookedIndexes;
  }

  public void setCookedIndexes(Set<Integer> cookedIndexes) {
    this.cookedIndexes = cookedIndexes;
  }

  public boolean isRequiresGroceryRun() {
    return requiresGroceryRun;
  }

  public void setRequiresGroceryRun(boolean requiresGroceryRun) {
    this.requiresGroceryRun = requiresGroceryRun;
  }
}

