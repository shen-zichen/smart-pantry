package com.smartpantry.strategy;

import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;

import java.util.List;

/**
 * Strategy interface for meal plan generation. Each implementation applies a different priority
 * when selecting recipes:
 *
 * <ul>
 *   <li>ZeroWasteStrategy — prioritizes recipes using items closest to expiration
 *   <li>BudgetStrategy — prioritizes recipes using cheapest available ingredients
 *   <li>StrictInventoryStrategy — only selects recipes fully covered by current stock
 * </ul>
 *
 * <p>All strategies receive the same inputs and return a MealPlan. The MealPlanGenerator (context
 * class) holds a strategy and delegates to it, allowing the user to swap strategies at runtime
 * without changing any other code. That's the Strategy Pattern in action.
 */
public interface IMealPlanStrategy {

  /**
   * Generates a meal plan based on available inventory and known recipes.
   *
   * @param inventory the current pantry contents
   * @param recipes the full recipe library to choose from
   * @return a MealPlan containing the selected recipes
   */
  MealPlan generatePlan(List<PantryItem> inventory, List<Recipe> recipes);
}
