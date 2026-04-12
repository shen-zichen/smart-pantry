package com.smartpantry.dto;

import com.smartpantry.model.Ingredient;
import com.smartpantry.model.Recipe;

import java.util.List;

/** Converts between domain Recipe and its DTOs. */
public class RecipeDtoMapper {

  public static RecipeResponse toResponse(Recipe recipe) {
    List<IngredientDto> ingredientDtos =
        recipe.getIngredients().stream()
            .map(
                i ->
                    new IngredientDto(
                        i.getName(), i.getQuantity(), i.getUnitType(), i.getCategoryType()))
            .toList();

    return new RecipeResponse(
        recipe.getId(),
        recipe.getName(),
        recipe.getDescription(),
        ingredientDtos,
        recipe.getSteps(),
        recipe.getServings(),
        recipe.getCuisineType(),
        recipe.getTags());
  }

  public static Recipe fromRequest(CreateRecipeRequest request) {
    List<Ingredient> ingredients =
        request.getIngredients().stream()
            .map(
                dto ->
                    new Ingredient(
                        dto.getName(), dto.getQuantity(), dto.getUnitType(), dto.getCategoryType()))
            .toList();

    return new Recipe(
        request.getName(),
        request.getDescription(),
        ingredients,
        request.getSteps(),
        request.getServings(),
        request.getCuisineType(),
        request.getTags());
  }

  private RecipeDtoMapper() {}
}
