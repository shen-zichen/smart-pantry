package com.smartpantry.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;
import com.smartpantry.util.UnitConverter;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CasualFormatter}. Covers: countable units, piece conversion, cooking units,
 * spice pinch, fallback, list formatting.
 */
class CasualFormatterTest {

  private CasualFormatter formatter;

  @BeforeEach
  void setUp() {
    UnitConverter converter =
        new UnitConverter(
            Map.of(
                "chicken thigh", 250.0,
                "onion", 150.0,
                "egg", 50.0));
    formatter = new CasualFormatter(converter);
  }

  // ---- Countable units — display directly ----

  @Test
  void format_pieceUnit_displaysDirectly() {
    Ingredient chicken = new Ingredient("chicken thigh", 2, UnitType.PIECE, CategoryType.PROTEIN);
    assertEquals("2 chicken thigh", formatter.format(chicken));
  }

  @Test
  void format_canUnit_displaysDirectly() {
    Ingredient beans = new Ingredient("black beans", 1, UnitType.CAN, CategoryType.PROTEIN);
    assertEquals("1 black beans", formatter.format(beans));
  }

  @Test
  void format_bagUnit_displaysDirectly() {
    Ingredient chips = new Ingredient("tortilla chips", 2, UnitType.BAG, CategoryType.SNACK);
    assertEquals("2 tortilla chips", formatter.format(chips));
  }

  // ---- Cooking units — natural display ----

  @Test
  void format_cupUnit_showsUnitAndName() {
    Ingredient flour = new Ingredient("flour", 2, UnitType.CUP, CategoryType.GRAIN);
    assertEquals("2 cup flour", formatter.format(flour));
  }

  @Test
  void format_tablespoon_showsAbbreviation() {
    Ingredient soySauce = new Ingredient("soy sauce", 3, UnitType.TABLESPOON, CategoryType.OIL);
    assertEquals("3 tbsp soy sauce", formatter.format(soySauce));
  }

  // ---- Weight with ballpark — convert to pieces ----

  @Test
  void format_gramsWithBallpark_convertsToPieces() {
    // 500g chicken ÷ 250g/piece = about 2
    Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    String result = formatter.format(chicken);

    assertTrue(result.contains("about"));
    assertTrue(result.contains("2"));
    assertTrue(result.contains("chicken thigh"));
  }

  @Test
  void format_halfPiece_displaysHalf() {
    // 75g onion ÷ 150g/piece = 0.5 → "about half"
    Ingredient onion = new Ingredient("onion", 75, UnitType.GRAM, CategoryType.VEGETABLE);
    String result = formatter.format(onion);

    assertTrue(result.contains("half"));
    assertTrue(result.contains("onion"));
  }

  // ---- Weight without ballpark — fall back to metric ----

  @Test
  void format_gramsWithoutBallpark_showsMetric() {
    // No ballpark for "butter" — falls back to grams
    Ingredient butter = new Ingredient("butter", 200, UnitType.GRAM, CategoryType.DAIRY);
    assertEquals("200g butter", formatter.format(butter));
  }

  // ---- Small spice — "a pinch" ----

  @Test
  void format_smallSpice_showsPinch() {
    Ingredient salt = new Ingredient("salt", 2, UnitType.GRAM, CategoryType.SPICE);
    assertEquals("a pinch of salt", formatter.format(salt));
  }

  @Test
  void format_largeSpice_showsNormally() {
    // 50g of paprika is not "a pinch"
    Ingredient paprika = new Ingredient("paprika", 50, UnitType.GRAM, CategoryType.SPICE);
    String result = formatter.format(paprika);
    assertFalse(result.contains("pinch"));
  }

  @Test
  void format_smallNonSpice_noPinch() {
    // 3g of chicken is weird but shouldn't say "a pinch of chicken"
    Ingredient chicken = new Ingredient("chicken thigh", 3, UnitType.GRAM, CategoryType.PROTEIN);
    String result = formatter.format(chicken);
    assertFalse(result.contains("pinch"));
  }

  // ---- formatList ----

  @Test
  void formatList_multipleIngredients_bulletedAndNewlined() {
    Ingredient chicken = new Ingredient("chicken thigh", 2, UnitType.PIECE, CategoryType.PROTEIN);
    Ingredient salt = new Ingredient("salt", 2, UnitType.GRAM, CategoryType.SPICE);

    String result = formatter.formatList(List.of(chicken, salt));

    assertTrue(result.contains("• 2 chicken thigh"));
    assertTrue(result.contains("• a pinch of salt"));
    assertTrue(result.contains("\n"));
  }

  // ---- Validation ----

  @Test
  void constructor_nullConverter_throwsException() {
    assertThrows(NullPointerException.class, () -> new CasualFormatter(null));
  }

  @Test
  void format_nullIngredient_throwsException() {
    assertThrows(NullPointerException.class, () -> formatter.format(null));
  }
}
