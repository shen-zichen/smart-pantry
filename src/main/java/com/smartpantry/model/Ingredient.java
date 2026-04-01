package com.smartpantry.model;

import java.util.Objects;

/**
 * Represents a food item with a name, quantity, unit, and category.
 * Immutable by design — scaling or consuming produces new instances
 * rather than mutating existing ones.
 */
public class Ingredient {

  private final String name;
  private final double quantity;
  private final UnitType unitType;
  private final CategoryType categoryType;

  /**
   * Constructs an Ingredient with validated inputs.
   *
   * @param name         the ingredient name (e.g., "chicken thigh")
   * @param quantity     the amount, must be non-negative
   * @param unitType     the unit of measurement
   * @param categoryType the food category
   * @throws IllegalArgumentException if name is null/blank or quantity is negative
   * @throws NullPointerException     if unitType or categoryType is null
   */
  public Ingredient(String name, double quantity, UnitType unitType, CategoryType categoryType) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Ingredient name cannot be null or blank");
    }
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
    }
    Objects.requireNonNull(unitType, "UnitType cannot be null");
    Objects.requireNonNull(categoryType, "CategoryType cannot be null");

    this.name = name;
    this.quantity = quantity;
    this.unitType = unitType;
    this.categoryType = categoryType;
  }

  public String getName() {
    return name;
  }

  public double getQuantity() {
    return quantity;
  }

  public UnitType getUnitType() {
    return unitType;
  }

  public CategoryType getCategoryType() {
    return categoryType;
  }

  /**
   * Identity is based on name, unit, and category — not quantity.
   * "500g chicken" and "200g chicken" are the same ingredient in different amounts.
   * This lets InventoryManager aggregate and strategies compare without quantity clashes.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ingredient that = (Ingredient) o;
    return name.equals(that.name)
        && unitType == that.unitType
        && categoryType == that.categoryType;
  }

  // Must stay consistent with equals — same three fields
  @Override
  public int hashCode() {
    return Objects.hash(name, unitType, categoryType);
  }

  @Override
  public String toString() {
    return "Ingredient{"
        + name
        + ", quantity=" + quantity
        + ", unitType=" + unitType
        + ", categoryType=" + categoryType
        + '}';
  }
}