package com.smartpantry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** Inbound DTO for marking a grocery item as bought. */
public class BoughtItemRequest {

  @NotBlank(message = "Ingredient name is required")
  private String name;

  @Positive(message = "Quantity must be positive")
  private double quantity;

  @NotBlank(message = "Unit type is required")
  private String unitType;

  @NotBlank(message = "Category type is required")
  private String categoryType;

  public BoughtItemRequest() {}

  public BoughtItemRequest(String name, double quantity, String unitType, String categoryType) {
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

  public String getUnitType() {
    return unitType;
  }

  public void setUnitType(String unitType) {
    this.unitType = unitType;
  }

  public String getCategoryType() {
    return categoryType;
  }

  public void setCategoryType(String categoryType) {
    this.categoryType = categoryType;
  }
}
