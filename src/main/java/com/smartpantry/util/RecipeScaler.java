package com.smartpantry.util;

import com.smartpantry.model.Ingredient;
import com.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Scales recipe ingredient quantities in two modes:
 *
 * <p><b>Casual (serving-based):</b> "I want 4 servings instead of 2" → double everything.
 *
 * <p><b>Pro (anchor-based):</b> "I have 750g chicken" → scale everything proportionally.
 *
 * <p>Stateless — no fields. All data comes in through method parameters. Always returns a new
 * Recipe; the original is never mutated.
 */
public class RecipeScaler {

  /**
   * Scales a recipe to a target number of servings. Example: recipe serves 2, target is 3 → ratio
   * 1.5 → all quantities × 1.5
   *
   * @param recipe the original recipe
   * @param targetServings the desired number of servings, must be positive
   * @return a new Recipe with scaled ingredients and updated serving count
   */
  public Recipe scaleByServings(Recipe recipe, double targetServings) {
    Objects.requireNonNull(recipe, "Recipe cannot be null");
    if (targetServings <= 0) {
      throw new IllegalArgumentException("Target servings must be positive: " + targetServings);
    }

    double ratio = targetServings / recipe.getServings();
    List<Ingredient> scaledIngredients = scaleIngredients(recipe.getIngredients(), ratio);

    return new Recipe(
        recipe.getName(),
        recipe.getDescription(),
        scaledIngredients,
        recipe.getSteps(),
        targetServings,
        recipe.getCuisineType(),
        recipe.getTags());
  }

  /**
   * Scales a recipe based on an anchor ingredient's new quantity. The anchor is the ingredient that
   * defines the dish (e.g., chicken in a chicken dish = 100%). All other ingredients scale
   * proportionally.
   *
   * <p>Assumes all quantities are in compatible units. Use UnitConverter to normalize before
   * calling this method if units differ.
   *
   * @param recipe the original recipe
   * @param anchorName the name of the anchor ingredient
   * @param newAnchorQuantity the new quantity for the anchor, must be positive
   * @return a new Recipe with all ingredients scaled around the anchor
   */
  public Recipe scaleByAnchor(Recipe recipe, String anchorName, double newAnchorQuantity) {
    Objects.requireNonNull(recipe, "Recipe cannot be null");
    Objects.requireNonNull(anchorName, "Anchor name cannot be null");
    if (newAnchorQuantity <= 0) {
      throw new IllegalArgumentException("Anchor quantity must be positive: " + newAnchorQuantity);
    }

    Ingredient anchor = findIngredient(recipe, anchorName);
    double ratio = newAnchorQuantity / anchor.getQuantity();
    List<Ingredient> scaledIngredients = scaleIngredients(recipe.getIngredients(), ratio);

    // Serving count scales with the same ratio
    double newServings = recipe.getServings() * ratio;

    return new Recipe(
        recipe.getName(),
        recipe.getDescription(),
        scaledIngredients,
        recipe.getSteps(),
        newServings,
        recipe.getCuisineType(),
        recipe.getTags());
  }

  /**
   * Returns each ingredient as a percentage of the anchor ingredient. Used by the pro-mode display:
   * "chicken 100%, onion 20%, garlic 2%"
   *
   * @param recipe the recipe to analyze
   * @param anchorName the name of the anchor ingredient (will be 100%)
   * @return a map of ingredient name → percentage (ordered to match recipe order)
   */
  public Map<String, Double> getAnchorPercentages(Recipe recipe, String anchorName) {
    Objects.requireNonNull(recipe, "Recipe cannot be null");
    Objects.requireNonNull(anchorName, "Anchor name cannot be null");

    Ingredient anchor = findIngredient(recipe, anchorName);
    double anchorQuantity = anchor.getQuantity();

    // LinkedHashMap preserves recipe ingredient order
    Map<String, Double> percentages = new LinkedHashMap<>();
    for (Ingredient ingredient : recipe.getIngredients()) {
      double percent = (ingredient.getQuantity() / anchorQuantity) * 100.0;
      percentages.put(ingredient.getName(), percent);
    }
    return percentages;
  }

  // ---- Private helpers ----

  /** Finds an ingredient by name in a recipe. Case-insensitive to be forgiving with user input. */
  private Ingredient findIngredient(Recipe recipe, String name) {
    for (Ingredient ingredient : recipe.getIngredients()) {
      if (ingredient.getName().equalsIgnoreCase(name)) {
        return ingredient;
      }
    }
    throw new IllegalArgumentException(
        "Ingredient '" + name + "' not found in recipe '" + recipe.getName() + "'");
  }

  /** Creates new Ingredient objects with each quantity multiplied by the ratio. */
  private List<Ingredient> scaleIngredients(List<Ingredient> ingredients, double ratio) {
    List<Ingredient> scaled = new ArrayList<>();
    for (Ingredient ingredient : ingredients) {
      scaled.add(
          new Ingredient(
              ingredient.getName(),
              ingredient.getQuantity() * ratio,
              ingredient.getUnitType(),
              ingredient.getCategoryType()));
    }
    return scaled;
  }
}
