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
  public MealPlan generatePlan(List<PantryItem> inventory, List<Recipe> recipes) {
    List<Recipe> selectedRecipes = new ArrayList<>();

    for (Recipe recipe : recipes) {
      if (isFullyCookable(recipe, inventory)) {
        selectedRecipes.add(recipe);
        if (selectedRecipes.size() >= maxRecipes) {
          break;
        }
      }
    }

    // If nothing is fully cookable, include the first recipe as a suggestion
    // Controller/View will handle the "you need to buy ingredients" message
    if (selectedRecipes.isEmpty() && !recipes.isEmpty()) {
      selectedRecipes.add(recipes.get(0));
    }

    return new MealPlan(
        "Strict Inventory", selectedRecipes, selectedRecipes.size(), LocalDate.now());
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
