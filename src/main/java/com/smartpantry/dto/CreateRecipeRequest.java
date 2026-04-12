package com.smartpantry.dto;

import com.smartpantry.model.CuisineType;
import com.smartpantry.model.RecipeTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Set;

/** Inbound DTO for creating a new recipe. */
public class CreateRecipeRequest {

  @NotBlank(message = "Recipe name is required")
  private String name;

  @NotBlank(message = "Description is required")
  private String description;

  @NotEmpty(message = "At least one ingredient is required")
  private List<IngredientDto> ingredients;

  @NotEmpty(message = "At least one step is required")
  private List<String> steps;

  @Positive(message = "Servings must be positive")
  private double servings;

  @NotNull(message = "Cuisine type is required")
  private CuisineType cuisineType;

  @NotNull(message = "Tags are required")
  private Set<RecipeTag> tags;

  // Getters and setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<IngredientDto> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<IngredientDto> ingredients) {
    this.ingredients = ingredients;
  }

  public List<String> getSteps() {
    return steps;
  }

  public void setSteps(List<String> steps) {
    this.steps = steps;
  }

  public double getServings() {
    return servings;
  }

  public void setServings(double servings) {
    this.servings = servings;
  }

  public CuisineType getCuisineType() {
    return cuisineType;
  }

  public void setCuisineType(CuisineType cuisineType) {
    this.cuisineType = cuisineType;
  }

  public Set<RecipeTag> getTags() {
    return tags;
  }

  public void setTags(Set<RecipeTag> tags) {
    this.tags = tags;
  }
}
