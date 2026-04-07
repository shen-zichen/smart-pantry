package com.smartpantry.mapper;

import com.smartpantry.entity.IngredientEntity;
import com.smartpantry.model.Ingredient;

public class IngredientMapper {

  // Entity → Domain (coming OUT of the database)
  public static Ingredient toDomain(IngredientEntity entity) {
    return new Ingredient(
        entity.getName(), entity.getQuantity(), entity.getUnitType(), entity.getCategoryType());
  }

  // Domain → Entity (going INTO the database)
  public static IngredientEntity toEntity(Ingredient ingredient) {
    return new IngredientEntity(
        ingredient.getName(),
        ingredient.getQuantity(),
        ingredient.getUnitType(),
        ingredient.getCategoryType());
  }

  private IngredientMapper() {} // Utility class — no instances
}
