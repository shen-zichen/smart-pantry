package com.smartpantry.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A mutable pantry entry that wraps an immutable {@link Ingredient} with pantry-specific data:
 * dates, stock level, and alert threshold.
 *
 * <p>PantryItem is mutable because pantry quantities change as users consume or restock food. The
 * underlying Ingredient identity stays fixed.
 */
public class PantryItem {

  private final Ingredient ingredient; // identity never changes — composition, not inheritance
  private double quantityInStock; // mutable — changes on consume/restock
  private final LocalDate boughtDate;
  private final LocalDate expirationDate;
  private final double lowStockThreshold; // per-item, not global — 10g salt ≠ 10 pieces chicken

  /**
   * Constructs a PantryItem with validated inputs.
   *
   * @param ingredient the ingredient this pantry entry tracks
   * @param quantityInStock how much is currently in the pantry
   * @param boughtDate when this item was purchased
   * @param expirationDate when this item expires (can be null for non-perishables like salt)
   * @param lowStockThreshold the quantity at which InventoryManager should fire an alert
   */
  public PantryItem(
      Ingredient ingredient,
      double quantityInStock,
      LocalDate boughtDate,
      LocalDate expirationDate,
      double lowStockThreshold) {
    Objects.requireNonNull(ingredient, "Ingredient cannot be null");
    Objects.requireNonNull(boughtDate, "Bought date cannot be null");
    if (quantityInStock < 0) {
      throw new IllegalArgumentException(
          "Quantity in stock cannot be negative: " + quantityInStock);
    }
    if (lowStockThreshold < 0) {
      throw new IllegalArgumentException(
          "Low stock threshold cannot be negative: " + lowStockThreshold);
    }

    this.ingredient = ingredient;
    this.quantityInStock = quantityInStock;
    this.boughtDate = boughtDate;
    this.expirationDate = expirationDate; // null is OK — salt doesn't expire
    this.lowStockThreshold = lowStockThreshold;
  }

  // ---- Getters ----

  public Ingredient getIngredient() {
    return ingredient;
  }

  /** Convenience delegate — avoids chaining item.getIngredient().getName() everywhere. */
  public String getName() {
    return ingredient.getName();
  }

  public double getQuantityInStock() {
    return quantityInStock;
  }

  public LocalDate getBoughtDate() {
    return boughtDate;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public double getLowStockThreshold() {
    return lowStockThreshold;
  }

  // ---- Mutators ----

  /**
   * Consumes a given amount from stock. Quantity will not drop below zero — you can't eat what you
   * don't have.
   *
   * @param amount the quantity to consume
   * @throws IllegalArgumentException if amount is negative
   */
  public void consume(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot consume a negative amount: " + amount);
    }
    this.quantityInStock = Math.max(0, this.quantityInStock - amount);
  }

  /**
   * Restocks the pantry with additional quantity.
   *
   * @param amount the quantity to add
   * @throws IllegalArgumentException if amount is negative
   */
  public void restock(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Cannot restock a negative amount: " + amount);
    }
    this.quantityInStock += amount;
  }

  // ---- Query methods ----

  /** Returns true if current stock is at or below the alert threshold. */
  public boolean isLowStock() {
    return quantityInStock <= lowStockThreshold;
  }

  /** Returns true if the item has an expiration date and it's on or before the given date. */
  public boolean isExpiredBy(LocalDate date) {
    return expirationDate != null && !expirationDate.isAfter(date);
  }

  /** Returns true if the item expires within the given number of days from today. */
  public boolean isExpiringSoon(int days) {
    if (expirationDate == null) {
      return false;
    }
    LocalDate warningDate = LocalDate.now().plusDays(days);
    return !expirationDate.isAfter(warningDate);
  }

  @Override
  public String toString() {
    return "PantryItem{"
        + ingredient.getName()
        + ", inStock="
        + quantityInStock
        + ingredient.getUnitType().getAbbreviation()
        + ", expires="
        + (expirationDate != null ? expirationDate : "N/A")
        + ", threshold="
        + lowStockThreshold
        + '}';
  }
}
