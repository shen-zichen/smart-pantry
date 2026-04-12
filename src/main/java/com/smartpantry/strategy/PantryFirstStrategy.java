package com.smartpantry.strategy;

import com.smartpantry.model.Ingredient;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Pantry-first meal plan strategy: prioritizes recipes that maximize use of ingredients already in
 * stock, minimizing grocery trips.
 *
 * <p>Algorithm: 1. Score each recipe by how many of its ingredients are in the pantry 2. Filter out
 * recipes that can't be fully cooked from current inventory 3. Sort by score (highest first) and
 * pick the top N
 *
 * <p>Future: will be complemented by a cost-based BudgetStrategy once price data is available from
 * grocery APIs.
 */
@Component("pantryFirst")
public class PantryFirstStrategy implements IMealPlanStrategy {

  private final int maxRecipes;

  /**
   * Constructs a PantryFirstStrategy.
   *
   * @param maxRecipes maximum number of recipes in the plan
   */
  public PantryFirstStrategy(
      @Value("${smartpantry.strategy.pantry-first-max-recipes:7}") int maxRecipes) {
    if (maxRecipes <= 0) {
      throw new IllegalArgumentException("Max recipes must be positive: " + maxRecipes);
    }
    this.maxRecipes = maxRecipes;
  }

  @Override
  public MealPlan generatePlan(List<PantryItem> inventory, List<Recipe> recipes) {

    // Score each recipe by pantry coverage, keep only cookable ones
    List<ScoredRecipe> scoredRecipes = new ArrayList<>();
    for (Recipe recipe : recipes) {
      int score = scoreRecipe(recipe, inventory);
      if (score > 0 && isFullyCookable(recipe, inventory)) {
        scoredRecipes.add(new ScoredRecipe(recipe, score));
      }
    }

    // Highest coverage first
    scoredRecipes.sort((a, b) -> Integer.compare(b.score, a.score));

    // Pick top N
    List<Recipe> selectedRecipes = new ArrayList<>();
    for (int i = 0; i < Math.min(maxRecipes, scoredRecipes.size()); i++) {
      selectedRecipes.add(scoredRecipes.get(i).recipe);
    }

    // Fallback: if nothing scored, pick any cookable recipe
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

    // Last resort: pick first recipe so MealPlan constructor doesn't reject empty list
    if (selectedRecipes.isEmpty() && !recipes.isEmpty()) {
      selectedRecipes.add(recipes.get(0));
    }

    return new MealPlan("Pantry First", selectedRecipes, selectedRecipes.size(), LocalDate.now());
  }

  // ---- Private helpers ----

  /**
   * Scores a recipe by how many of its ingredients are available in the pantry. Higher score = more
   * ingredients already on hand = less shopping.
   */
  private int scoreRecipe(Recipe recipe, List<PantryItem> inventory) {
    int score = 0;
    for (Ingredient needed : recipe.getIngredients()) {
      for (PantryItem item : inventory) {
        if (needed.equals(item.getIngredient())) { // compare Ingredient to Ingredient
          score++;
          break; // don't double-count
        }
      }
    }
    return score;
  }

  /** Checks if every ingredient in the recipe is covered by pantry stock. */
  private boolean isFullyCookable(Recipe recipe, List<PantryItem> inventory) {
    for (Ingredient needed : recipe.getIngredients()) {
      if (!hasEnoughInStock(needed, inventory)) {
        return false;
      }
    }
    return true;
  }

  /** Checks if the pantry has enough of a specific ingredient. */
  private boolean hasEnoughInStock(Ingredient needed, List<PantryItem> inventory) {
    for (PantryItem item : inventory) {
      if (item.getIngredient().equals(needed)
          && item.getQuantityInStock() >= needed.getQuantity()) {
        return true;
      }
    }
    return false;
  }

  /** Pairs a recipe with its score for sorting. */
  private static class ScoredRecipe {
    final Recipe recipe;
    final int score;

    ScoredRecipe(Recipe recipe, int score) {
      this.recipe = recipe;
      this.score = score;
    }
  }
}
