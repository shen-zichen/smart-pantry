package com.smartpantry.dto;

import com.smartpantry.model.CuisineType;
import com.smartpantry.model.RecipeTag;

import java.util.List;
import java.util.Set;

/** Outbound DTO for recipe data. */
public class RecipeResponse {

  private Long id;
  private String name;
  private String description;
  private List<IngredientDto> ingredients;
  private List<String> steps;
  private double servings;
  private CuisineType cuisineType;
  private Set<RecipeTag> tags;

  public RecipeResponse() {}

  public RecipeResponse(
      Long id,
      String name,
      String description,
      List<IngredientDto> ingredients,
      List<String> steps,
      double servings,
      CuisineType cuisineType,
      Set<RecipeTag> tags) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.ingredients = ingredients;
    this.steps = steps;
    this.servings = servings;
    this.cuisineType = cuisineType;
    this.tags = tags;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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
