package com.smartpantry.controller;

import com.smartpantry.model.Ingredient;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;
import com.smartpantry.strategy.IMealPlanStrategy;
import com.smartpantry.strategy.IUnitFormatter;
import com.smartpantry.strategy.MealPlanGenerator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Orchestrates meal plan generation: strategy selection, plan creation, post-meal consumption, and
 * leftover detection.
 *
 * <p>This is where the Strategy Pattern meets the user — the controller lets them swap strategies
 * and see the results without knowing any implementation details.
 */
public class MealPlanController {

  private final MealPlanGenerator generator;
  private IUnitFormatter formatter;
  private MealPlan currentPlan; // most recently generated plan, null initially

  /**
   * Constructs a MealPlanController.
   *
   * @param generator the strategy pattern context class
   * @param formatter the active display formatter
   */
  public MealPlanController(MealPlanGenerator generator, IUnitFormatter formatter) {
    Objects.requireNonNull(generator, "MealPlanGenerator cannot be null");
    Objects.requireNonNull(formatter, "Formatter cannot be null");

    this.generator = generator;
    this.formatter = formatter;
    this.currentPlan = null;
  }

  // ======== Strategy Selection ========

  /**
   * Swaps the active strategy at runtime. Example: user switches from Zero Waste to Pantry First
   * mid-session.
   */
  public void setStrategy(IMealPlanStrategy strategy) {
    generator.setStrategy(strategy);
  }

  // ======== Plan Generation ========

  /**
   * Generates a meal plan using the active strategy and stores it.
   *
   * @param inventory the current pantry contents
   * @param recipes the full recipe library
   * @return the generated MealPlan
   */
  public MealPlan generatePlan(List<PantryItem> inventory, List<Recipe> recipes) {
    Objects.requireNonNull(inventory, "Inventory cannot be null");
    Objects.requireNonNull(recipes, "Recipes cannot be null");

    currentPlan = generator.generatePlan(inventory, recipes, 7); // Default to 7 for legacy Phase 0 compatibility
    return currentPlan;
  }

  /** Returns the most recently generated plan, or null if none generated yet. */
  public MealPlan getCurrentPlan() {
    return currentPlan;
  }

  // ======== Display ========

  /**
   * Formats a meal plan for display: strategy name, each recipe with its formatted ingredient list.
   */
  public String formatPlan(MealPlan plan) {
    Objects.requireNonNull(plan, "MealPlan cannot be null");

    StringBuilder sb = new StringBuilder();
    sb.append("Meal Plan — ")
        .append(plan.getStrategyName())
        .append(" (")
        .append(plan.getDays())
        .append(" day")
        .append(plan.getDays() > 1 ? "s" : "")
        .append(")")
        .append("\nGenerated: ")
        .append(plan.getCreatedDate())
        .append("\n");

    List<Recipe> recipes = plan.getRecipes();
    for (int i = 0; i < recipes.size(); i++) {
      Recipe recipe = recipes.get(i);
      sb.append("\n--- Day ").append(i + 1).append(": ").append(recipe.getName()).append(" ---\n");

      for (Ingredient ingredient : recipe.getIngredients()) {
        sb.append("  • ").append(formatter.format(ingredient)).append("\n");
      }
    }

    return sb.toString();
  }

  // ======== Post-Meal Flow ========

  /**
   * After cooking a recipe, deducts each ingredient from the pantry. Delegates consumption to
   * PantryController, which triggers observer alerts.
   *
   * @param recipe the recipe that was cooked
   * @param pantryController the pantry controller to consume from
   */
  public void postMealConsume(Recipe recipe, PantryController pantryController) {
    Objects.requireNonNull(recipe, "Recipe cannot be null");
    Objects.requireNonNull(pantryController, "PantryController cannot be null");

    for (Ingredient needed : recipe.getIngredients()) {
      List<PantryItem> matches = pantryController.findItemsByName(needed.getName());
      if (!matches.isEmpty()) {
        // Consume from the first matching pantry item
        pantryController.consumeItem(matches.get(0), needed.getQuantity());
      }
    }
  }

  /**
   * After a meal, finds pantry items with small remaining quantities. These are candidates for
   * zero-waste reuse — "you have half an onion left, want to use it tomorrow?"
   *
   * @param pantryController the pantry controller to query
   * @param threshold items with quantity at or below this are considered leftovers
   * @return list of pantry items with small remaining quantities
   */
  public List<PantryItem> getLeftovers(PantryController pantryController, double threshold) {
    Objects.requireNonNull(pantryController, "PantryController cannot be null");

    return pantryController.getInventory().stream()
        .filter(item -> item.getQuantityInStock() > 0 && item.getQuantityInStock() <= threshold)
        .collect(Collectors.toList());
  }

  /**
   * Formats leftover items for the zero-waste prompt. "You have these leftovers — want to use them
   * tomorrow?"
   */
  public String formatLeftovers(List<PantryItem> leftovers) {
    if (leftovers.isEmpty()) {
      return "No leftovers — pantry is clean!";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("You have leftovers that could be used tomorrow:\n");
    for (PantryItem item : leftovers) {
      Ingredient display =
          new Ingredient(
              item.getName(),
              item.getQuantityInStock(),
              item.getIngredient().getUnitType(),
              item.getIngredient().getCategoryType());
      sb.append("  • ").append(formatter.format(display)).append("\n");
    }
    sb.append("Want to generate a plan using these?");
    return sb.toString();
  }

  // ======== Formatter Toggle ========

  public void setFormatter(IUnitFormatter formatter) {
    Objects.requireNonNull(formatter, "Formatter cannot be null");
    this.formatter = formatter;
  }
}
