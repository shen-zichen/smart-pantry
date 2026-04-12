package com.smartpantry.dto;

/** Outbound DTO for grocery list items — what needs to be purchased. */
public class GroceryItemResponse {

  private String ingredientName;
  private double deficit;
  private String unitType;
  private String categoryType;

  public GroceryItemResponse() {}

  public GroceryItemResponse(
      String ingredientName, double deficit, String unitType, String categoryType) {
    this.ingredientName = ingredientName;
    this.deficit = deficit;
    this.unitType = unitType;
    this.categoryType = categoryType;
  }

  public String getIngredientName() {
    return ingredientName;
  }

  public void setIngredientName(String ingredientName) {
    this.ingredientName = ingredientName;
  }

  public double getDeficit() {
    return deficit;
  }

  public void setDeficit(double deficit) {
    this.deficit = deficit;
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
