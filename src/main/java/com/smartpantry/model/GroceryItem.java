package com.smartpantry.model;

import java.util.Objects;

/**
 * Represents a shopping list entry: an ingredient the pantry is short on and how much more is
 * needed. Produced by the GroceryListGenerator when a recipe's requirements exceed available
 * inventory.
 *
 * <p>Immutable — once the deficit is calculated, it doesn't change.
 */
public class GroceryItem {

  private final Ingredient ingredient; // what to buy (carries name, unit, category)
  private final double deficit; // how much more is needed

  /**
   * Constructs a GroceryItem.
   *
   * @param ingredient the ingredient that's in short supply
   * @param deficit how much more is needed, must be positive
   */
  public GroceryItem(Ingredient ingredient, double deficit) {
    Objects.requireNonNull(ingredient, "Ingredient cannot be null");
    if (deficit <= 0) {
      throw new IllegalArgumentException("Deficit must be positive: " + deficit);
    }

    this.ingredient = ingredient;
    this.deficit = deficit;
  }

  public Ingredient getIngredient() {
    return ingredient;
  }

  /** Convenience delegate — avoids chaining getIngredient().getName() everywhere. */
  public String getName() {
    return ingredient.getName();
  }

  public double getDeficit() {
    return deficit;
  }

  public UnitType getUnitType() {
    return ingredient.getUnitType();
  }

  @Override
  public String toString() {
    return "Buy: "
        + deficit
        + ingredient.getUnitType().getAbbreviation()
        + " "
        + ingredient.getName();
  }
}
