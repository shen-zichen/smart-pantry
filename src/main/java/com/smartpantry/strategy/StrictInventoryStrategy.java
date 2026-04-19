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
 * Strict inventory strategy: only suggests recipes where EVERY ingredient is available in the
 * pantry with sufficient quantity. No grocery trip needed.
 *
 * <p>The simplest strategy — no scoring, no ranking. Pure binary filter: can I cook this entirely
 * from what I have? Yes → include. No → skip.
 *
 * <p>If multiple recipes qualify, they're returned in their original order from the recipe library,
 * up to the max.
 */
@Component("strict")
public class StrictInventoryStrategy implements IMealPlanStrategy {

  private final int maxRecipes;

  /**
   * Constructs a StrictInventoryStrategy.
   *
   * @param maxRecipes maximum number of recipes in the plan
   */
  public StrictInventoryStrategy(@Value("${smartpantry.strategy.max-recipes:7}") int maxRecipes) {
    if (maxRecipes <= 0) {
      throw new IllegalArgumentException("Max recipes must be positive: " + maxRecipes);
    }
    this.maxRecipes = maxRecipes;
  }

  @Override
  public MealPlan generatePlan(List<PantryItem> inventory, List<Recipe> recipes, int targetServings) {
    List<Recipe> selectedRecipes = new ArrayList<>();
    double currentServings = 0;

    for (Recipe recipe : recipes) {
      if (currentServings >= targetServings) {
        break;
      }
      if (isFullyCookable(recipe, inventory)) {
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
    }

    if (selectedRecipes.isEmpty()) {
      throw new IllegalArgumentException(
          "Insufficient inventory to strictly generate a meal plan. Try Pantry First strategy.");
    }

    return new MealPlan("Strict Inventory", selectedRecipes, targetServings, LocalDate.now());
  }

  // ---- Private helpers ----

  /** Every ingredient must be in stock with sufficient quantity. */
  private boolean isFullyCookable(Recipe recipe, List<PantryItem> inventory) {
    for (Ingredient needed : recipe.getIngredients()) {
      if (!hasEnoughInStock(needed, inventory)) {
        return false;
      }
    }
    return true;
  }

  private boolean hasEnoughInStock(Ingredient needed, List<PantryItem> inventory) {
    for (PantryItem item : inventory) {
      if (item.getIngredient().equals(needed)
          && item.getQuantityInStock() >= needed.getQuantity()) {
        return true;
      }
    }
    return false;
  }
}
