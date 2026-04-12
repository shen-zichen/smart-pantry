package com.smartpantry.dto;

import com.smartpantry.model.CategoryType;
import com.smartpantry.model.UnitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

/**
 * Inbound DTO for creating a new pantry item. Validation annotations enforce the same rules as the
 * domain model, but at the HTTP boundary so bad requests never reach the service layer.
 */
public class CreatePantryItemRequest {

  @NotBlank(message = "Ingredient name is required")
  private String name;

  @Positive(message = "Quantity must be positive")
  private double quantity;

  @NotNull(message = "Unit type is required")
  private UnitType unitType;

  @NotNull(message = "Category is required")
  private CategoryType categoryType;

  @NotNull(message = "Bought date is required")
  private LocalDate boughtDate;

  private LocalDate expirationDate; // nullable — salt doesn't expire

  @PositiveOrZero(message = "Threshold cannot be negative")
  private double lowStockThreshold;

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

  public LocalDate getBoughtDate() {
    return boughtDate;
  }

  public void setBoughtDate(LocalDate boughtDate) {
    this.boughtDate = boughtDate;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public double getLowStockThreshold() {
    return lowStockThreshold;
  }

  public void setLowStockThreshold(double lowStockThreshold) {
    this.lowStockThreshold = lowStockThreshold;
  }
}
