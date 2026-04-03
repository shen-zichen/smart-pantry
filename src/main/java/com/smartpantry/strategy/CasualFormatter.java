package com.smartpantry.strategy;

import com.smartpantry.model.CategoryType;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.UnitType;
import com.smartpantry.util.UnitConverter;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Casual display formatter: shows ingredients the way a home cook thinks about them. "2 chicken
 * thighs", "1 onion", "a pinch of salt" — not "500g" or "2.5g".
 *
 * <p>Uses {@link UnitConverter} to convert grams back to approximate piece counts when a ballpark
 * exists. Falls back to metric when no conversion is available.
 */
public class CasualFormatter implements IUnitFormatter {

  private final UnitConverter converter;

  // Units that are already casual-friendly — no conversion needed
  private static final Set<UnitType> COUNTABLE_UNITS =
      Set.of(UnitType.PIECE, UnitType.BAG, UnitType.CAN, UnitType.BOTTLE, UnitType.BOX);

  // Cooking units that read naturally as-is
  private static final Set<UnitType> COOKING_UNITS =
      Set.of(UnitType.CUP, UnitType.TABLESPOON, UnitType.TEASPOON);

  /**
   * Constructs a CasualFormatter with a unit converter for ballpark lookups.
   *
   * @param converter the converter with piece-to-gram ballpark data
   */
  public CasualFormatter(UnitConverter converter) {
    Objects.requireNonNull(converter, "UnitConverter cannot be null");
    this.converter = converter;
  }

  /**
   * Formats a single ingredient for casual display.
   *
   * <p>Priority: 1. Tiny spice → "a pinch of salt" 2. Already countable (PIECE/BAG/CAN) → "2
   * chicken thighs" 3. Cooking unit (CUP/TBSP/TSP) → "2 cups flour" 4. Weight with ballpark →
   * "about 2 chicken thighs" 5. Weight without ballpark → "500g chicken"
   */
  @Override
  public String format(Ingredient ingredient) {
    Objects.requireNonNull(ingredient, "Ingredient cannot be null");

    // Tiny spice gets special casual treatment
    if (isSmallSpiceQuantity(ingredient)) {
      return "a pinch of " + ingredient.getName();
    }

    UnitType unit = ingredient.getUnitType();
    double quantity = ingredient.getQuantity();
    String name = ingredient.getName();

    // Already in countable units — display directly
    if (COUNTABLE_UNITS.contains(unit)) {
      return formatQuantity(quantity) + " " + name;
    }

    // Cooking units read naturally
    if (COOKING_UNITS.contains(unit)) {
      return formatQuantity(quantity) + " " + unit.getAbbreviation() + " " + name;
    }

    // Weight unit — try to convert to pieces if ballpark exists
    if (converter.hasBallpark(name)) {
      return formatAsPieces(ingredient);
    }

    // No ballpark — fall back to metric display
    return formatQuantity(quantity) + unit.getAbbreviation() + " " + name;
  }

  /**
   * Formats a full ingredient list for display.
   *
   * @param ingredients the list to format
   * @return each ingredient on its own line, e.g., "• 2 chicken thighs\n• 1 onion"
   */
  public String formatList(List<Ingredient> ingredients) {
    Objects.requireNonNull(ingredients, "Ingredients list cannot be null");
    return ingredients.stream()
        .map(this::format)
        .map(line -> "• " + line)
        .collect(Collectors.joining("\n"));
  }

  // ---- Private helpers ----

  /**
   * Converts a weight-based ingredient to approximate piece count. "500g chicken thigh" → "about 2
   * chicken thighs" (if 1 thigh ≈ 250g)
   */
  private String formatAsPieces(Ingredient ingredient) {
    double grams;
    UnitType unit = ingredient.getUnitType();

    // Normalize to grams first (handles kg, lb, oz)
    try {
      grams = converter.toGrams(ingredient.getQuantity(), unit);
    } catch (IllegalArgumentException e) {
      // Not a weight unit — shouldn't happen but fall back gracefully
      return formatQuantity(ingredient.getQuantity())
          + unit.getAbbreviation()
          + " "
          + ingredient.getName();
    }

    double pieces = converter.gramsToPieces(ingredient.getName(), grams);
    String pieceStr = formatPieceCount(pieces);
    return "about " + pieceStr + " " + ingredient.getName();
  }

  /**
   * Formats a piece count into natural language. 0.5 → "half", 1.0 → "1", 1.5 → "1 and a half", 2.0
   * → "2"
   */
  private String formatPieceCount(double pieces) {
    if (Math.abs(pieces - 0.5) < 0.1) {
      return "half";
    }

    long whole = Math.round(pieces);
    double fraction = pieces - (long) pieces;

    // Close enough to a whole number — just round
    if (Math.abs(fraction) < 0.2 || Math.abs(fraction) > 0.8) {
      return String.valueOf(Math.max(1, whole));
    }

    // Has a meaningful fractional part
    if (Math.abs(fraction - 0.5) < 0.2) {
      return (long) pieces + " and a half";
    }

    // Odd fraction — just round
    return String.valueOf(Math.max(1, whole));
  }

  /** Formats a numeric quantity cleanly. 2.0 → "2", 1.5 → "1.5", 0.25 → "0.25" */
  private String formatQuantity(double quantity) {
    if (quantity == (long) quantity) {
      return String.valueOf((long) quantity);
    }
    return String.valueOf(quantity);
  }

  /** A small amount of a spice (under 5g or under 1 tsp) gets "a pinch" treatment. */
  private boolean isSmallSpiceQuantity(Ingredient ingredient) {
    if (ingredient.getCategoryType() != CategoryType.SPICE) {
      return false;
    }
    double quantity = ingredient.getQuantity();
    UnitType unit = ingredient.getUnitType();

    // Under 5 grams of any spice
    if ((unit == UnitType.GRAM
            || unit == UnitType.KILOGRAM
            || unit == UnitType.OUNCE
            || unit == UnitType.POUND)
        && quantity <= 5) {
      return true;
    }
    // Under 1 teaspoon
    if (unit == UnitType.TEASPOON && quantity < 1) {
      return true;
    }
    return false;
  }
}
