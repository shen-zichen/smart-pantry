package com.smartpantry.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;
import com.smartpantry.util.UnitConverter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ProfessionalFormatter}. Covers: metric normalization, anchor percentages,
 * countable units, list formatting.
 */
class ProfessionalFormatterTest {

  private ProfessionalFormatter formatter;

  @BeforeEach
  void setUp() {
    UnitConverter converter =
        new UnitConverter(
            Map.of(
                "chicken thigh", 250.0,
                "onion", 150.0));
    formatter = new ProfessionalFormatter(converter);
  }

  // ---- Weight normalization ----

  @Test
  void format_grams_displaysAsGrams() {
    Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    assertEquals("500g chicken thigh", formatter.format(chicken));
  }

  @Test
  void format_kilograms_normalizesToGrams() {
    Ingredient rice = new Ingredient("rice", 1.5, UnitType.KILOGRAM, CategoryType.GRAIN);
    assertEquals("1500g rice", formatter.format(rice));
  }

  @Test
  void format_pounds_normalizesToGrams() {
    Ingredient beef = new Ingredient("beef", 1, UnitType.POUND, CategoryType.PROTEIN);
    // 1 lb = 453.6g
    assertEquals("453.6g beef", formatter.format(beef));
  }

  @Test
  void format_ounces_normalizesToGrams() {
    Ingredient butter = new Ingredient("butter", 4, UnitType.OUNCE, CategoryType.DAIRY);
    // 4 oz = 113.4g
    assertEquals("113.4g butter", formatter.format(butter));
  }

  // ---- Volume normalization ----

  @Test
  void format_milliliters_displaysAsMl() {
    Ingredient milk = new Ingredient("milk", 240, UnitType.MILLILITER, CategoryType.DAIRY);
    assertEquals("240mL milk", formatter.format(milk));
  }

  @Test
  void format_cups_normalizesToMl() {
    Ingredient water = new Ingredient("water", 2, UnitType.CUP, CategoryType.BEVERAGE);
    // 2 cups = 480mL
    assertEquals("480mL water", formatter.format(water));
  }

  @Test
  void format_tablespoon_normalizesToMl() {
    Ingredient soySauce = new Ingredient("soy sauce", 1, UnitType.TABLESPOON, CategoryType.OIL);
    // 1 tbsp = 14.79mL
    assertEquals("14.8mL soy sauce", formatter.format(soySauce));
  }

  @Test
  void format_liter_normalizesToMl() {
    Ingredient broth = new Ingredient("chicken broth", 1.5, UnitType.LITER, CategoryType.BEVERAGE);
    assertEquals("1500mL chicken broth", formatter.format(broth));
  }

  // ---- Countable units — no conversion ----

  @Test
  void format_piece_displaysWithAbbreviation() {
    Ingredient egg = new Ingredient("egg", 3, UnitType.PIECE, CategoryType.PROTEIN);
    assertEquals("3pc egg", formatter.format(egg));
  }

  @Test
  void format_can_displaysWithAbbreviation() {
    Ingredient beans = new Ingredient("black beans", 2, UnitType.CAN, CategoryType.PROTEIN);
    assertEquals("2can black beans", formatter.format(beans));
  }

  // ---- Anchor percentages ----

  @Test
  void formatWithAnchor_showsPercentage() {
    Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    assertEquals("500g chicken thigh (100%)", formatter.formatWithAnchor(chicken, 100.0));
  }

  @Test
  void formatWithAnchor_fractionalPercentage() {
    Ingredient garlic = new Ingredient("garlic", 10, UnitType.GRAM, CategoryType.SPICE);
    assertEquals("10g garlic (2.5%)", formatter.formatWithAnchor(garlic, 2.5));
  }

  @Test
  void formatWithAnchor_wholePercentage_noDecimal() {
    Ingredient onion = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    assertEquals("100g onion (20%)", formatter.formatWithAnchor(onion, 20.0));
  }

  // ---- List formatting ----

  @Test
  void formatList_multipleIngredients() {
    Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    Ingredient onion = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);

    String result = formatter.formatList(List.of(chicken, onion));

    assertTrue(result.contains("• 500g chicken thigh"));
    assertTrue(result.contains("• 100g onion"));
    assertTrue(result.contains("\n"));
  }

  @Test
  void formatListWithAnchor_showsAllPercentages() {
    Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    Ingredient onion = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    Ingredient garlic = new Ingredient("garlic", 10, UnitType.GRAM, CategoryType.SPICE);

    // LinkedHashMap to preserve order
    Map<String, Double> percentages = new LinkedHashMap<>();
    percentages.put("chicken thigh", 100.0);
    percentages.put("onion", 20.0);
    percentages.put("garlic", 2.0);

    String result = formatter.formatListWithAnchor(List.of(chicken, onion, garlic), percentages);

    assertTrue(result.contains("500g chicken thigh (100%)"));
    assertTrue(result.contains("100g onion (20%)"));
    assertTrue(result.contains("10g garlic (2%)"));
  }

  // ---- Validation ----

  @Test
  void constructor_nullConverter_throwsException() {
    assertThrows(NullPointerException.class, () -> new ProfessionalFormatter(null));
  }

  @Test
  void format_nullIngredient_throwsException() {
    assertThrows(NullPointerException.class, () -> formatter.format(null));
  }
}
