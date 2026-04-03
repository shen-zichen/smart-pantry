package com.smartpantry.strategy;

import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;

import java.util.List;
import java.util.Objects;

/**
 * The "context" class in the Strategy Pattern. Holds an {@link IMealPlanStrategy} and delegates
 * plan generation to it.
 *
 * <p>The generator doesn't know or care which strategy it's using — it just calls generatePlan().
 * The user (or controller) can swap strategies at runtime via {@link #setStrategy}, enabling the
 * same generator to produce completely different meal plans depending on the active strategy.
 *
 * <p>This is the Strategy Pattern in action: same interface, swappable behavior.
 */
public class MealPlanGenerator {

  private IMealPlanStrategy strategy;

  /**
   * Constructs a MealPlanGenerator with an initial strategy.
   *
   * @param strategy the meal plan strategy to use
   */
  public MealPlanGenerator(IMealPlanStrategy strategy) {
    Objects.requireNonNull(strategy, "Strategy cannot be null");
    this.strategy = strategy;
  }

  /**
   * Swaps the active strategy at runtime. Example: user switches from Zero Waste mode to Budget
   * mode mid-session.
   *
   * @param strategy the new strategy to use
   */
  public void setStrategy(IMealPlanStrategy strategy) {
    Objects.requireNonNull(strategy, "Strategy cannot be null");
    this.strategy = strategy;
  }

  /**
   * Delegates meal plan generation to the active strategy.
   *
   * @param inventory the current pantry contents
   * @param recipes the full recipe library
   * @return a MealPlan produced by the active strategy
   */
  public MealPlan generatePlan(List<PantryItem> inventory, List<Recipe> recipes) {
    Objects.requireNonNull(inventory, "Inventory cannot be null");
    Objects.requireNonNull(recipes, "Recipes cannot be null");
    return strategy.generatePlan(inventory, recipes);
  }

  /** Returns the current strategy (useful for display: "Plan generated using Zero Waste mode"). */
  public IMealPlanStrategy getStrategy() {
    return strategy;
  }
}
