package com.smartpantry.strategy;

import com.smartpantry.model.Ingredient;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Zero-waste meal plan strategy: prioritizes recipes that consume ingredients closest to
 * expiration, reducing food waste.
 *
 * <p>Algorithm: 1. Find all pantry items expiring within the window 2. Score each recipe by how
 * many expiring ingredients it uses 3. Filter out recipes that can't be fully cooked from current
 * inventory 4. Sort by score (highest first) and pick the top N
 */
public class ZeroWasteStrategy implements IMealPlanStrategy {

  private final int expirationWindowDays;
  private final int maxRecipes;

  /**
   * Constructs a ZeroWasteStrategy.
   *
   * @param expirationWindowDays how many days ahead to consider "expiring soon"
   * @param maxRecipes maximum number of recipes in the plan
   */
  public ZeroWasteStrategy(int expirationWindowDays, int maxRecipes) {
    if (expirationWindowDays < 0) {
      throw new IllegalArgumentException(
          "Expiration window cannot be negative: " + expirationWindowDays);
    }
    if (maxRecipes <= 0) {
      throw new IllegalArgumentException("Max recipes must be positive: " + maxRecipes);
    }
    this.expirationWindowDays = expirationWindowDays;
    this.maxRecipes = maxRecipes;
  }

  @Override
  public MealPlan generatePlan(List<PantryItem> inventory, List<Recipe> recipes) {
    List<PantryItem> expiringItems = getExpiringItems(inventory);

    // Score each recipe, pair them up, and sort by score descending
    List<ScoredRecipe> scoredRecipes = new ArrayList<>();
    for (Recipe recipe : recipes) {
      int score = scoreRecipe(recipe, expiringItems);
      if (score > 0 && isFullyCookable(recipe, inventory)) {
        scoredRecipes.add(new ScoredRecipe(recipe, score));
      }
    }

    // Sort: highest score first. If tied, keep original recipe order (stable sort)
    scoredRecipes.sort((a, b) -> Integer.compare(b.score, a.score));

    // Pick top N recipes
    List<Recipe> selectedRecipes = new ArrayList<>();
    for (int i = 0; i < Math.min(maxRecipes, scoredRecipes.size()); i++) {
      selectedRecipes.add(scoredRecipes.get(i).recipe);
    }

    // If no recipes scored, fall back to any cookable recipe
    if (selectedRecipes.isEmpty()) {
      for (Recipe recipe : recipes) {
        if (isFullyCookable(recipe, inventory)) {
          selectedRecipes.add(recipe);
          if (selectedRecipes.size() >= maxRecipes) {
            break;
          }
        }
      }
    }

    // If still nothing is cookable, return a plan with whatever scored highest
    // (the controller/view will handle the "not enough ingredients" message)
    if (selectedRecipes.isEmpty() && !recipes.isEmpty()) {
      selectedRecipes.add(recipes.get(0));
    }

    return new MealPlan(
        "Zero Waste",
        selectedRecipes,
        selectedRecipes.size(), // one recipe per day
        LocalDate.now());
  }

  // ---- Private helpers ----

  /** Filters pantry to only items expiring within the window. */
  private List<PantryItem> getExpiringItems(List<PantryItem> inventory) {
    List<PantryItem> expiring = new ArrayList<>();
    for (PantryItem item : inventory) {
      if (item.isExpiringSoon(expirationWindowDays)) {
        expiring.add(item);
      }
    }
    return expiring;
  }

  /**
   * Scores a recipe by counting how many of its ingredients match expiring items. Higher score =
   * uses more at-risk food = better for zero-waste.
   */
  private int scoreRecipe(Recipe recipe, List<PantryItem> expiringItems) {
    int score = 0;
    for (Ingredient needed : recipe.getIngredients()) {
      for (PantryItem expiring : expiringItems) {
        // Compare by name and unit — Ingredient.equals ignores quantity
        if (expiring.getIngredient().equals(needed)) {
          score++;
          break; // don't double-count the same expiring item
        }
      }
    }
    return score;
  }

  /**
   * Checks if every ingredient in the recipe is available in the pantry with sufficient quantity.
   */
  private boolean isFullyCookable(Recipe recipe, List<PantryItem> inventory) {
    for (Ingredient needed : recipe.getIngredients()) {
      if (!hasEnoughInStock(needed, inventory)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the pantry has enough of a specific ingredient. Matches by ingredient identity (name
   * + unit + category), then compares quantity.
   */
  private boolean hasEnoughInStock(Ingredient needed, List<PantryItem> inventory) {
    for (PantryItem item : inventory) {
      if (item.getIngredient().equals(needed)
          && item.getQuantityInStock() >= needed.getQuantity()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Simple wrapper to pair a recipe with its score for sorting. Private inner class — no one
   * outside this strategy needs to see this.
   */
  private static class ScoredRecipe {
    final Recipe recipe;
    final int score;

    ScoredRecipe(Recipe recipe, int score) {
      this.recipe = recipe;
      this.score = score;
    }
  }
}
