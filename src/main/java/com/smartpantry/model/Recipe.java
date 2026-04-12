package com.smartpantry.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * An immutable recipe definition: what ingredients you need, how to cook them, and metadata for
 * filtering/display. Scaling or modifying a recipe produces a new instance — the original is never
 * mutated.
 */
public class Recipe {

  private final String name;
  private final String description;
  private final List<Ingredient> ingredients;
  private final List<String> steps;
  private final double servings;
  private final CuisineType cuisineType;
  private final Set<RecipeTag> tags;
  private Long id;

  /**
   * Constructs a Recipe with validated and defensively copied inputs.
   *
   * @param name recipe name (e.g., "Orange Chicken")
   * @param description short summary (e.g., "Crispy chicken in citrus glaze")
   * @param ingredients the required ingredients with quantities
   * @param steps ordered cooking instructions
   * @param servings number of portions this recipe yields, must be positive
   * @param cuisineType the cuisine origin
   * @param tags descriptive tags for filtering by strategies
   */
  public Recipe(
      String name,
      String description,
      List<Ingredient> ingredients,
      List<String> steps,
      double servings,
      CuisineType cuisineType,
      Set<RecipeTag> tags) {
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(description, "Description cannot be null");
    Objects.requireNonNull(ingredients, "Ingredients cannot be null");
    Objects.requireNonNull(steps, "Steps cannot be null");
    Objects.requireNonNull(cuisineType, "Cuisine type cannot be null");
    Objects.requireNonNull(tags, "Tags cannot be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be blank");
    }
    if (ingredients.isEmpty()) {
      throw new IllegalArgumentException("Ingredients cannot be empty");
    }
    if (steps.isEmpty()) {
      throw new IllegalArgumentException("Steps cannot be empty");
    }
    if (servings <= 0) {
      throw new IllegalArgumentException("Servings must be positive: " + servings);
    }

    // Defensive copies — caller can't mutate our internals after construction
    this.name = name;
    this.description = description;
    this.ingredients = new ArrayList<>(ingredients);
    this.steps = new ArrayList<>(steps);
    this.servings = servings;
    this.cuisineType = cuisineType;
    this.tags = new HashSet<>(tags);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  // Defensive copy out — caller can't mutate our ingredient list
  public List<Ingredient> getIngredients() {
    return new ArrayList<>(ingredients);
  }

  public List<String> getSteps() {
    return new ArrayList<>(steps);
  }

  public double getServings() {
    return servings;
  }

  public CuisineType getCuisineType() {
    return cuisineType;
  }

  public Set<RecipeTag> getTags() {
    return new HashSet<>(tags);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /** Convenience method — strategies can quickly check if a recipe has a specific tag. */
  public boolean hasTag(RecipeTag tag) {
    return tags.contains(tag);
  }

  /**
   * Identity is based on name and ingredients — two recipes with the same name and same ingredient
   * list are considered equal regardless of description wording.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Recipe recipe = (Recipe) o;
    return name.equals(recipe.name) && ingredients.equals(recipe.ingredients);
  }

  // Consistent with equals — same two fields
  @Override
  public int hashCode() {
    return Objects.hash(name, ingredients);
  }

  @Override
  public String toString() {
    return name + " (" + description + ") — serves " + servings;
  }
}
