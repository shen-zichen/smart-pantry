package com.smartpantry.util;

import com.smartpantry.model.UnitType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles two types of conversions:
 *
 * <p>1. <b>Piece ↔ Grams</b> (ingredient-specific ballpark): "1 chicken thigh ≈ 250g" — uses a
 * lookup map.
 *
 * <p>2. <b>Unit ↔ Unit</b> (fixed ratios): "1 pound = 453.6g", "1 cup = 240mL" — standard
 * conversions.
 *
 * <p>Everything normalizes to metric internally (grams for weight, mL for volume).
 */
public class UnitConverter {

  // ---- Fixed unit-to-metric ratios (weight → grams, volume → mL) ----

  private static final Map<UnitType, Double> TO_GRAMS =
      Map.of(
          UnitType.GRAM, 1.0,
          UnitType.KILOGRAM, 1000.0,
          UnitType.OUNCE, 28.35,
          UnitType.POUND, 453.6);

  private static final Map<UnitType, Double> TO_ML =
      Map.of(
          UnitType.MILLILITER, 1.0,
          UnitType.LITER, 1000.0,
          UnitType.TEASPOON, 4.93,
          UnitType.TABLESPOON, 14.79,
          UnitType.CUP, 240.0,
          UnitType.FLUID_OUNCE, 29.57);

  // ---- Ingredient-specific piece-to-gram ballpark ----

  private final Map<String, Double> gramsPerPiece; // "chicken thigh" → 250.0

  /**
   * Constructs a UnitConverter with a custom piece-to-gram lookup table.
   *
   * @param gramsPerPiece map of ingredient name → grams per one piece
   */
  public UnitConverter(Map<String, Double> gramsPerPiece) {
    Objects.requireNonNull(gramsPerPiece, "Grams-per-piece map cannot be null");
    // Normalize all keys to lowercase — "Chicken Thigh" and "chicken thigh" should match
    this.gramsPerPiece = new HashMap<>();
    gramsPerPiece.forEach((key, value) -> this.gramsPerPiece.put(key.toLowerCase(), value));
  }

  /** Constructs a UnitConverter with an empty lookup table. */
  public UnitConverter() {
    this.gramsPerPiece = new HashMap<>();
  }

  // ======== Piece ↔ Grams (ingredient-specific) ========

  /**
   * Converts a countable quantity to grams using the ballpark table. Example: 4 chicken thighs ×
   * 250g = 1000g
   *
   * @param ingredientName the ingredient to look up
   * @param pieces how many pieces (can be fractional, e.g., 1.5)
   * @return the estimated weight in grams
   * @throws IllegalArgumentException if the ingredient is not in the lookup table or pieces is
   *     negative
   */
  public double piecesToGrams(String ingredientName, double pieces) {
    if (pieces < 0) {
      throw new IllegalArgumentException("Pieces cannot be negative: " + pieces);
    }
    double gpp = lookupGramsPerPiece(ingredientName);
    return pieces * gpp;
  }

  /**
   * Converts grams to an approximate piece count using the ballpark table. Example: 500g chicken ÷
   * 250g = 2 pieces
   *
   * @param ingredientName the ingredient to look up
   * @param grams the weight in grams
   * @return the estimated number of pieces
   * @throws IllegalArgumentException if the ingredient is not in the lookup table or grams is
   *     negative
   */
  public double gramsToPieces(String ingredientName, double grams) {
    if (grams < 0) {
      throw new IllegalArgumentException("Grams cannot be negative: " + grams);
    }
    double gpp = lookupGramsPerPiece(ingredientName);
    return grams / gpp;
  }

  /**
   * Returns true if the ballpark table has an entry for this ingredient. Callers can check before
   * converting to avoid exceptions.
   */
  public boolean hasBallpark(String ingredientName) {
    return gramsPerPiece.containsKey(ingredientName.toLowerCase());
  }

  // ======== Unit ↔ Unit (fixed ratios) ========

  /**
   * Converts any weight unit to grams. Example: 2 pounds → 907.2g
   *
   * @param quantity the amount in the source unit
   * @param from the source weight unit (GRAM, KILOGRAM, OUNCE, POUND)
   * @return the equivalent quantity in grams
   * @throws IllegalArgumentException if the unit is not a weight unit
   */
  public double toGrams(double quantity, UnitType from) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
    }
    Double factor = TO_GRAMS.get(from);
    if (factor == null) {
      throw new IllegalArgumentException("Not a weight unit: " + from);
    }
    return quantity * factor;
  }

  /**
   * Converts grams to a target weight unit. Example: 907.2g → 2 pounds
   *
   * @param grams the amount in grams
   * @param target the desired weight unit
   * @return the equivalent quantity in the target unit
   */
  public double fromGrams(double grams, UnitType target) {
    if (grams < 0) {
      throw new IllegalArgumentException("Grams cannot be negative: " + grams);
    }
    Double factor = TO_GRAMS.get(target);
    if (factor == null) {
      throw new IllegalArgumentException("Not a weight unit: " + target);
    }
    return grams / factor;
  }

  /**
   * Converts any volume unit to milliliters. Example: 2 cups → 480mL
   *
   * @param quantity the amount in the source unit
   * @param from the source volume unit (MILLILITER, LITER, TEASPOON, TABLESPOON, CUP, FLUID_OUNCE)
   * @return the equivalent quantity in milliliters
   * @throws IllegalArgumentException if the unit is not a volume unit
   */
  public double toMilliliters(double quantity, UnitType from) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
    }
    Double factor = TO_ML.get(from);
    if (factor == null) {
      throw new IllegalArgumentException("Not a volume unit: " + from);
    }
    return quantity * factor;
  }

  /**
   * Converts milliliters to a target volume unit. Example: 480mL → 2 cups
   *
   * @param ml the amount in milliliters
   * @param target the desired volume unit
   * @return the equivalent quantity in the target unit
   */
  public double fromMilliliters(double ml, UnitType target) {
    if (ml < 0) {
      throw new IllegalArgumentException("Milliliters cannot be negative: " + ml);
    }
    Double factor = TO_ML.get(target);
    if (factor == null) {
      throw new IllegalArgumentException("Not a volume unit: " + target);
    }
    return ml / factor;
  }

  // ---- Internal helpers ----

  private double lookupGramsPerPiece(String ingredientName) {
    Double gpp = gramsPerPiece.get(ingredientName.toLowerCase());
    if (gpp == null) {
      throw new IllegalArgumentException(
          "No ballpark conversion found for: "
              + ingredientName
              + ". Add it to the conversion table first.");
    }
    return gpp;
  }
}
