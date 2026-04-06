package com.smartpantry.controller;

import com.smartpantry.model.CuisineType;
import com.smartpantry.model.Recipe;
import com.smartpantry.model.RecipeTag;
import com.smartpantry.model.TwinRecipe;
import com.smartpantry.strategy.IUnitFormatter;
import com.smartpantry.util.RecipeScaler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Orchestrates recipe operations: search, filter, scale, twin-swap, and display. Manages the recipe
 * library and twin recipe pairings.
 *
 * <p>Does not own business logic — delegates scaling to {@link RecipeScaler} and formatting to the
 * active {@link IUnitFormatter}.
 */
public class RecipeController {

  private final List<Recipe> recipeLibrary;
  private final List<TwinRecipe> twinRecipes;
  private final RecipeScaler scaler;
  private IUnitFormatter formatter; // mutable — user toggles at runtime

  /**
   * Constructs a RecipeController.
   *
   * @param scaler the recipe scaler for serving and anchor-based scaling
   * @param formatter the active display formatter
   */
  public RecipeController(RecipeScaler scaler, IUnitFormatter formatter) {
    Objects.requireNonNull(scaler, "RecipeScaler cannot be null");
    Objects.requireNonNull(formatter, "Formatter cannot be null");

    this.recipeLibrary = new ArrayList<>();
    this.twinRecipes = new ArrayList<>();
    this.scaler = scaler;
    this.formatter = formatter;
  }

  // ======== Library Management ========

  /** Adds a recipe to the library. */
  public void addRecipe(Recipe recipe) {
    Objects.requireNonNull(recipe, "Recipe cannot be null");
    recipeLibrary.add(recipe);
  }

  /** Removes a recipe from the library. */
  public boolean removeRecipe(Recipe recipe) {
    return recipeLibrary.remove(recipe);
  }

  /** Adds a twin recipe pairing. */
  public void addTwinRecipe(TwinRecipe twin) {
    Objects.requireNonNull(twin, "TwinRecipe cannot be null");
    twinRecipes.add(twin);
  }

  // ======== Search & Filter ========

  /**
   * Finds a recipe by name (case-insensitive).
   *
   * @param name the recipe name to search for
   * @return the matching recipe, or null if not found
   */
  public Recipe findByName(String name) {
    Objects.requireNonNull(name, "Name cannot be null");
    return recipeLibrary.stream()
        .filter(r -> r.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElse(null);
  }

  /** Filters recipes by tag. Example: findByTag(RecipeTag.QUICK) → all quick recipes. */
  public List<Recipe> findByTag(RecipeTag tag) {
    Objects.requireNonNull(tag, "Tag cannot be null");
    return recipeLibrary.stream().filter(r -> r.hasTag(tag)).collect(Collectors.toList());
  }

  /**
   * Filters recipes by cuisine type. Example: findByCuisine(CuisineType.CHINESE) → all Chinese
   * recipes.
   */
  public List<Recipe> findByCuisine(CuisineType cuisine) {
    Objects.requireNonNull(cuisine, "CuisineType cannot be null");
    return recipeLibrary.stream()
        .filter(r -> r.getCuisineType() == cuisine)
        .collect(Collectors.toList());
  }

  // ======== Scaling — delegates to RecipeScaler ========

  /**
   * Scales a recipe to a target number of servings. Returns a new Recipe — the original is not
   * modified.
   */
  public Recipe scaleByServings(Recipe recipe, double targetServings) {
    return scaler.scaleByServings(recipe, targetServings);
  }

  /**
   * Scales a recipe based on an anchor ingredient's new quantity. Returns a new Recipe — the
   * original is not modified.
   */
  public Recipe scaleByAnchor(Recipe recipe, String anchorName, double newQuantity) {
    return scaler.scaleByAnchor(recipe, anchorName, newQuantity);
  }

  /**
   * Returns each ingredient as a percentage of the anchor. For pro mode display: "chicken 100%,
   * onion 20%, garlic 2%"
   */
  public Map<String, Double> getAnchorPercentages(Recipe recipe, String anchorName) {
    return scaler.getAnchorPercentages(recipe, anchorName);
  }

  // ======== Twin Recipes ========

  /**
   * Swaps a twin recipe between healthy and guilty pleasure variant.
   *
   * @param twin the twin recipe to swap
   * @return the newly active recipe after swapping
   */
  public Recipe swapTwinVariant(TwinRecipe twin) {
    Objects.requireNonNull(twin, "TwinRecipe cannot be null");
    twin.swap();
    return twin.getActiveRecipe();
  }

  /** Returns all twin recipe pairings. */
  public List<TwinRecipe> getAllTwinRecipes() {
    return new ArrayList<>(twinRecipes);
  }

  // ======== Display ========

  /**
   * Formats a recipe's ingredient list using the active formatter. Returns a display-ready string
   * with recipe name, description, servings, and formatted ingredients.
   */
  public String formatRecipe(Recipe recipe) {
    Objects.requireNonNull(recipe, "Recipe cannot be null");

    StringBuilder sb = new StringBuilder();
    sb.append(recipe.getName())
        .append(" — ")
        .append(recipe.getDescription())
        .append(" (serves ")
        .append(formatNumber(recipe.getServings()))
        .append(")")
        .append("\n\nIngredients:\n");

    for (int i = 0; i < recipe.getIngredients().size(); i++) {
      if (i > 0) {
        sb.append("\n");
      }
      sb.append("• ").append(formatter.format(recipe.getIngredients().get(i)));
    }

    sb.append("\n\nSteps:\n");
    List<String> steps = recipe.getSteps();
    for (int i = 0; i < steps.size(); i++) {
      sb.append(i + 1).append(". ").append(steps.get(i));
      if (i < steps.size() - 1) {
        sb.append("\n");
      }
    }

    return sb.toString();
  }

  // ======== Formatter Toggle ========

  public void setFormatter(IUnitFormatter formatter) {
    Objects.requireNonNull(formatter, "Formatter cannot be null");
    this.formatter = formatter;
  }

  public IUnitFormatter getFormatter() {
    return formatter;
  }

  // ======== Getters ========

  /** Returns a defensive copy of the full recipe library. */
  public List<Recipe> getAllRecipes() {
    return new ArrayList<>(recipeLibrary);
  }

  public int getRecipeCount() {
    return recipeLibrary.size();
  }

  // ---- Internal helper ----

  private String formatNumber(double value) {
    if (value == (long) value) {
      return String.valueOf((long) value);
    }
    return String.valueOf(value);
  }
}
