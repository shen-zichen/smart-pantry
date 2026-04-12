package com.smartpantry.dto;

import com.smartpantry.model.CategoryType;
import com.smartpantry.model.UnitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Reusable DTO for ingredient data in recipe requests and responses. */
public class IngredientDto {

  @NotBlank(message = "Ingredient name is required")
  private String name;

  @Positive(message = "Quantity must be positive")
  private double quantity;

  @NotNull(message = "Unit type is required")
  private UnitType unitType;

  @NotNull(message = "Category is required")
  private CategoryType categoryType;

  public IngredientDto() {}

  public IngredientDto(String name, double quantity, UnitType unitType, CategoryType categoryType) {
    this.name = name;
    this.quantity = quantity;
    this.unitType = unitType;
    this.categoryType = categoryType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  public UnitType getUnitType() {
    return unitType;
  }

  public void setUnitType(UnitType unitType) {
    this.unitType = unitType;
  }

  public CategoryType getCategoryType() {
    return categoryType;
  }

  public void setCategoryType(CategoryType categoryType) {
    this.categoryType = categoryType;
  }
}
