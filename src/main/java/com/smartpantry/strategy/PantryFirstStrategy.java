package com.smartpantry.strategy;

import com.smartpantry.model.Ingredient;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Pantry-first meal plan strategy: prioritizes recipes that maximize use of ingredients already in
 * stock, minimizing grocery trips.
 *
 * <p>Algorithm: 1. Score each recipe by how many of its ingredients are in the pantry 2. Sort by
 * score (highest proportion of ingredients found first) 3. Iteratively add scaling recipes until targetServings is met
 */
@Component("pantryFirst")
public class PantryFirstStrategy implements IMealPlanStrategy {

  @Override
  public MealPlan generatePlan(List<PantryItem> inventory, List<Recipe> recipes, int targetServings) {

    // Score each recipe by pantry coverage
    List<ScoredRecipe> scoredRecipes = new ArrayList<>();
    for (Recipe recipe : recipes) {
      int score = scoreRecipe(recipe, inventory);
      int totalIngredients = recipe.getIngredients().size();
      if (totalIngredients > 0) {
        scoredRecipes.add(new ScoredRecipe(recipe, score, totalIngredients));
      }
    }

    // Sort by percentage of ingredients derived from the pantry
    scoredRecipes.sort((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()));

    List<Recipe> planRecipes = new ArrayList<>();
    double currentServings = 0;
    boolean requiresGroceryRun = false;

    // Pick top recipes until we hit targetServings
    for (ScoredRecipe scored : scoredRecipes) {
      if (currentServings >= targetServings) {
        break;
      }

      // If coverage isn't 100%, we'll need to buy something
      if (scored.score < scored.totalIngredients) {
        requiresGroceryRun = true;
      }

      Recipe recipe = scored.recipe;
      double needed = targetServings - currentServings;
      double available = recipe.getServings();

      if (available > needed) {
        // Scale it down
        planRecipes.add(recipe.scale(needed));
        currentServings += needed;
      } else {
        // Keep as is
        planRecipes.add(recipe);
        currentServings += available;
      }
    }

    // Last resort: pick first recipe scaled if nothing else
    if (planRecipes.isEmpty() && !recipes.isEmpty()) {
      Recipe fallback = recipes.get(0);
      planRecipes.add(fallback.scale(targetServings));
      requiresGroceryRun = true;
    }

    MealPlan plan = new MealPlan("Pantry First", planRecipes, targetServings, LocalDate.now());
    plan.setRequiresGroceryRun(requiresGroceryRun);
    return plan;
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
    final int totalIngredients;

    ScoredRecipe(Recipe recipe, int score, int totalIngredients) {
      this.recipe = recipe;
      this.score = score;
      this.totalIngredients = totalIngredients;
    }

    double getPercentage() {
      if (totalIngredients == 0) return 0;
      return (double) score / totalIngredients;
    }
  }
}
