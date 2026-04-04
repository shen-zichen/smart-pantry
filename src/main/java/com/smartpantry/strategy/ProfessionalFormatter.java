package com.smartpantry.strategy;

import com.smartpantry.model.Ingredient;
import com.smartpantry.model.UnitType;
import com.smartpantry.util.UnitConverter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Professional display formatter: shows precise metric quantities and optional anchor-based
 * percentages.
 *
 * <p>"500g chicken thigh" or "500g chicken thigh (100%)"
 *
 * <p>All weights normalize to grams, all volumes normalize to mL. No rounding to "about 2 pieces" —
 * this is for users who weigh ingredients on a scale and think in ratios.
 */
public class ProfessionalFormatter implements IUnitFormatter {

  private final UnitConverter converter;

  // Weight units that should normalize to grams
  private static final Set<UnitType> WEIGHT_UNITS =
      Set.of(UnitType.GRAM, UnitType.KILOGRAM, UnitType.OUNCE, UnitType.POUND);

  // Volume units that should normalize to mL
  private static final Set<UnitType> VOLUME_UNITS =
      Set.of(
          UnitType.MILLILITER,
          UnitType.LITER,
          UnitType.TEASPOON,
          UnitType.TABLESPOON,
          UnitType.CUP,
          UnitType.FLUID_OUNCE);

  /**
   * Constructs a ProfessionalFormatter.
   *
   * @param converter the converter for unit normalization
   */
  public ProfessionalFormatter(UnitConverter converter) {
    Objects.requireNonNull(converter, "UnitConverter cannot be null");
    this.converter = converter;
  }

  /**
   * Formats a single ingredient in precise metric. "500g chicken thigh", "240mL milk", "2pc egg"
   */
  @Override
  public String format(Ingredient ingredient) {
    Objects.requireNonNull(ingredient, "Ingredient cannot be null");
    return toMetricString(ingredient) + " " + ingredient.getName();
  }

  /**
   * Formats a single ingredient with its anchor percentage. "500g chicken thigh (100.0%)", "100g
   * onion (20.0%)"
   *
   * @param ingredient the ingredient to format
   * @param anchorPercentage the percentage relative to the anchor ingredient
   * @return formatted string with percentage
   */
  public String formatWithAnchor(Ingredient ingredient, double anchorPercentage) {
    Objects.requireNonNull(ingredient, "Ingredient cannot be null");
    return toMetricString(ingredient)
        + " "
        + ingredient.getName()
        + " ("
        + formatPercent(anchorPercentage)
        + ")";
  }

  /**
   * Formats a full ingredient list in precise metric.
   *
   * @param ingredients the list to format
   * @return each ingredient on its own line
   */
  public String formatList(List<Ingredient> ingredients) {
    Objects.requireNonNull(ingredients, "Ingredients list cannot be null");
    return ingredients.stream()
        .map(this::format)
        .map(line -> "• " + line)
        .collect(Collectors.joining("\n"));
  }

  /**
   * Formats a full ingredient list with anchor percentages. The percentages map comes from {@code
   * RecipeScaler.getAnchorPercentages()}.
   *
   * @param ingredients the list to format
   * @param percentages map of ingredient name → percentage
   * @return each ingredient with its percentage on its own line
   */
  public String formatListWithAnchor(
      List<Ingredient> ingredients, Map<String, Double> percentages) {
    Objects.requireNonNull(ingredients, "Ingredients list cannot be null");
    Objects.requireNonNull(percentages, "Percentages map cannot be null");

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < ingredients.size(); i++) {
      Ingredient ingredient = ingredients.get(i);
      double percent = percentages.getOrDefault(ingredient.getName(), 0.0);

      if (i > 0) {
        sb.append("\n");
      }
      sb.append("• ").append(formatWithAnchor(ingredient, percent));
    }
    return sb.toString();
  }

  // ---- Private helpers ----

  /**
   * Normalizes any unit to its metric base and formats cleanly. Weight → grams, Volume → mL,
   * Countable → as-is with unit abbreviation.
   */
  private String toMetricString(Ingredient ingredient) {
    double quantity = ingredient.getQuantity();
    UnitType unit = ingredient.getUnitType();

    // Weight → normalize to grams
    if (WEIGHT_UNITS.contains(unit)) {
      double grams = converter.toGrams(quantity, unit);
      return formatNumber(grams) + "g";
    }

    // Volume → normalize to mL
    if (VOLUME_UNITS.contains(unit)) {
      double ml = converter.toMilliliters(quantity, unit);
      return formatNumber(ml) + "mL";
    }

    // Countable (PIECE, BAG, CAN, etc.) — no conversion, show as-is
    return formatNumber(quantity) + unit.getAbbreviation();
  }

  /**
   * Formats a number cleanly: no trailing zeros for whole numbers. 500.0 → "500", 14.79 → "14.8"
   */
  private String formatNumber(double value) {
    if (value == (long) value) {
      return String.valueOf((long) value);
    }
    // One decimal place for non-whole numbers
    return String.format("%.1f", value);
  }

  /** Formats a percentage value. 100.0 → "100%", 20.0 → "20%", 4.5 → "4.5%" */
  private String formatPercent(double percent) {
    if (percent == (long) percent) {
      return (long) percent + "%";
    }
    return String.format("%.1f%%", percent);
  }
}
