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
   * @param days how many days the plan should cover
   * @return the generated and persisted meal plan
   */
  MealPlan generatePlan(String strategyName, int days);

  /** Returns all previously generated meal plans. */
  List<MealPlan> getAllPlans();

  /** Returns a specific meal plan by ID. */
  MealPlan getPlanById(Long id);

  /**
   * Consumes pantry ingredients for a given meal plan. Deducts each recipe's ingredient quantities
   * from pantry stock.
   *
   * @param planId the meal plan that was cooked
   */
  void postMealConsume(Long planId);

  /** Returns the names of all available strategies. */
  Set<String> getAvailableStrategies();
}
