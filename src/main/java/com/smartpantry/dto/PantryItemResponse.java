package com.smartpantry.dto;

import com.smartpantry.model.CategoryType;
import com.smartpantry.model.UnitType;

import java.time.LocalDate;

/**
 * Outbound DTO for pantry items. Includes the database ID so clients can reference this item in
 * consume/restock/delete calls.
 */
public class PantryItemResponse {

  private Long id;
  private String ingredientName;
  private double quantityInStock;
  private UnitType unitType;
  private CategoryType categoryType;
  private LocalDate boughtDate;
  private LocalDate expirationDate;
  private double lowStockThreshold;
  private boolean lowStock;
  private boolean expiringSoon;

  public PantryItemResponse() {}

  public PantryItemResponse(
      Long id,
      String ingredientName,
      double quantityInStock,
      UnitType unitType,
      CategoryType categoryType,
      LocalDate boughtDate,
      LocalDate expirationDate,
      double lowStockThreshold,
      boolean lowStock,
      boolean expiringSoon) {
    this.id = id;
    this.ingredientName = ingredientName;
    this.quantityInStock = quantityInStock;
    this.unitType = unitType;
    this.categoryType = categoryType;
    this.boughtDate = boughtDate;
    this.expirationDate = expirationDate;
    this.lowStockThreshold = lowStockThreshold;
    this.lowStock = lowStock;
    this.expiringSoon = expiringSoon;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIngredientName() {
    return ingredientName;
  }

  public void setIngredientName(String ingredientName) {
    this.ingredientName = ingredientName;
  }

  public double getQuantityInStock() {
    return quantityInStock;
  }

  public void setQuantityInStock(double quantityInStock) {
    this.quantityInStock = quantityInStock;
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

  public boolean isLowStock() {
    return lowStock;
  }

  public void setLowStock(boolean lowStock) {
    this.lowStock = lowStock;
  }

  public boolean isExpiringSoon() {
    return expiringSoon;
  }

  public void setExpiringSoon(boolean expiringSoon) {
    this.expiringSoon = expiringSoon;
  }
}
