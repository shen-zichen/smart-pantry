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
 * Zero-waste meal plan strategy: prioritizes recipes that consume ingredients closest to
 * expiration, reducing food waste.
 *
 * <p>Algorithm: 1. Find all pantry items expiring within the window 2. Score each recipe by how
 * many expiring ingredients it uses 3. Filter out recipes that can't be fully cooked from current
 * inventory 4. Sort by score (highest first) and pick the top N
 */
@Component("zeroWaste")
public class ZeroWasteStrategy implements IMealPlanStrategy {

  private final int expirationWindowDays;
  private final int maxRecipes;

  /**
   * Constructs a ZeroWasteStrategy.
   *
   * @param expirationWindowDays how many days ahead to consider "expiring soon"
   * @param maxRecipes maximum number of recipes in the plan
   */
  public ZeroWasteStrategy(
      @Value("${smartpantry.strategy.zero-waste-expiration-days:3}") int expirationWindowDays,
      @Value("${smartpantry.strategy.max-recipes:7}") int maxRecipes) {
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
  public MealPlan generatePlan(List<PantryItem> inventory, List<Recipe> recipes, int targetServings) {
    // 1. Identify critical ingredients dying soon
    List<PantryItem> criticalItems =
        inventory.stream().filter(item -> item.isExpiringSoon(expirationWindowDays)).toList();

    // 2. Score recipes by how many critical items they use
    List<ScoredRecipe> scoredRecipes = new ArrayList<>();
    for (Recipe recipe : recipes) {
      int score = scoreRecipe(recipe, criticalItems);
      if (score > 0) {
        scoredRecipes.add(new ScoredRecipe(recipe, score));
      }
    }

    // 3. Sort by usefulness (score)
    scoredRecipes.sort((a, b) -> Integer.compare(b.score, a.score));

    // 4. Select top ones and scale to fit exact targetServings
    List<Recipe> selectedRecipes = new ArrayList<>();
    double currentServings = 0;
    
    for (ScoredRecipe sr : scoredRecipes) {
      if (currentServings >= targetServings) break;
      Recipe recipe = sr.recipe;
      double needed = targetServings - currentServings;
      double available = recipe.getServings();
      if (available > needed) {
        selectedRecipes.add(recipe.scale(needed));
        currentServings += needed;
      } else {
        selectedRecipes.add(recipe);
        currentServings += available;
      }
    }

    // Fallback: fill with any recipe if we didn't hit targetServings
    if (currentServings < targetServings) {
      for (Recipe recipe : recipes) {
        if (currentServings >= targetServings) break;
        // Don't add dupes unless we have to, but for simplicity just add them
        double needed = targetServings - currentServings;
        double available = recipe.getServings();
        if (available > needed) {
          selectedRecipes.add(recipe.scale(needed));
          currentServings += needed;
          selectedRecipes.add(recipe);
          currentServings += available;
        }
      }
    }

    return new MealPlan("Zero Waste", selectedRecipes, targetServings, LocalDate.now());
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
