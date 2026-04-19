package com.smartpantry.service;

import com.smartpantry.model.MealPlan;

import java.util.List;
import java.util.Set;

/**
 * Manages meal plan generation and retrieval. Strategy selection happens by name — the service
 * resolves which implementation to use.
 */
public interface IMealPlanService {

  /**
   * Generates a meal plan using the named strategy against current inventory and recipes.
   *
   * @param strategyName the strategy to use (e.g., "zeroWaste", "pantryFirst", "strict")
   * @param days how many meals the plan should cover
   * @return the generated and persisted meal plan
   */
  MealPlan generatePlan(String strategyName, int days);

  /** Returns all previously generated meal plans. */
  List<MealPlan> getAllPlans();

  /** Returns a specific meal plan by ID. */
  MealPlan getPlanById(Long id);

  /**
   * Marks a specific recipe in a meal plan as cooked and deducts its ingredients from pantry.
   *
   * @param planId the meal plan
   * @param recipeIndex the 0-based index of the recipe to mark as cooked
   * @return the updated meal plan
   */
  MealPlan markRecipeCooked(Long planId, int recipeIndex);

  /**
   * Consumes pantry ingredients for ALL recipes in a meal plan.
   *
   * @param planId the meal plan that was cooked
   */
  void postMealConsume(Long planId);

  /** Deletes a meal plan by ID. */
  void deletePlan(Long planId);

  /** Returns the names of all available strategies. */
  Set<String> getAvailableStrategies();
}
