package com.smartpantry.mapper;

import com.smartpantry.entity.IngredientEntity;
import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.Recipe;

import java.util.List;

public class RecipeMapper {

  public static Recipe toDomain(RecipeEntity entity) {
    List<Ingredient> ingredients =
        entity.getIngredients().stream().map(IngredientMapper::toDomain).toList();

    Recipe recipe =
        new Recipe(
            entity.getName(),
            entity.getDescription(),
            ingredients,
            entity.getSteps(),
            entity.getServings(),
            entity.getCuisineType(),
            entity.getTags());
    recipe.setId(entity.getId());
    return recipe;
  }

  public static RecipeEntity toEntity(Recipe recipe) {
    List<IngredientEntity> ingredientEntities =
        recipe.getIngredients().stream().map(IngredientMapper::toEntity).toList();

    return new RecipeEntity(
        recipe.getName(),
        recipe.getDescription(),
        ingredientEntities,
        recipe.getSteps(),
        recipe.getServings(),
        recipe.getCuisineType(),
        recipe.getTags());
  }

  private RecipeMapper() {}
}
