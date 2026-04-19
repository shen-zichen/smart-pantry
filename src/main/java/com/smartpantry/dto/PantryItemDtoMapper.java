package com.smartpantry.dto;

import com.smartpantry.model.Ingredient;
import com.smartpantry.model.PantryItem;

/**
 * Converts between domain PantryItem and its DTOs. Separate from the entity mapper — this one
 * handles the HTTP boundary.
 */
public class PantryItemDtoMapper {

  // TODO: read from application.yml (smartpantry.strategy.zero-waste-expiration-days) via Spring config
  private static final int EXPIRING_SOON_DAYS = 3;

  public static PantryItemResponse toResponse(PantryItem item) {
    return new PantryItemResponse(
        item.getId(),
        item.getName(),
        item.getQuantityInStock(),
        item.getIngredient().getUnitType(),
        item.getIngredient().getCategoryType(),
        item.getBoughtDate(),
        item.getExpirationDate(),
        item.getLowStockThreshold(),
        item.isLowStock(),
        item.isExpiringSoon(EXPIRING_SOON_DAYS)
    );
  }

  /** Request DTO → Domain for service layer input. */
  public static PantryItem fromRequest(CreatePantryItemRequest request) {
    Ingredient ingredient =
        new Ingredient(
            request.getName(),
            request.getQuantity(),
            request.getUnitType(),
            request.getCategoryType());
    return new PantryItem(
        ingredient,
        request.getQuantity(),
        request.getBoughtDate(),
        request.getExpirationDate(),
        request.getLowStockThreshold());
  }

  private PantryItemDtoMapper() {}
}
