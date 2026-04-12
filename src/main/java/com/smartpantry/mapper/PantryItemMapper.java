package com.smartpantry.mapper;

import com.smartpantry.entity.PantryItemEntity;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.PantryItem;

public class PantryItemMapper {

  public static PantryItem toDomain(PantryItemEntity entity) {
    Ingredient ingredient = IngredientMapper.toDomain(entity.getIngredient());
    return new PantryItem(
        ingredient,
        entity.getQuantityInStock(),
        entity.getBoughtDate(),
        entity.getExpirationDate(),
        entity.getLowStockThreshold());
  }

  public static PantryItemEntity toEntity(
      PantryItem pantryItem, com.smartpantry.entity.IngredientEntity ingredientEntity) {
    return new PantryItemEntity(
        ingredientEntity,
        pantryItem.getQuantityInStock(),
        pantryItem.getBoughtDate(),
        pantryItem.getExpirationDate(),
        pantryItem.getLowStockThreshold());
  }

  private PantryItemMapper() {}
}
