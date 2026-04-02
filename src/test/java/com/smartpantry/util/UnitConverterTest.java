package com.smartpantry.util;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.UnitType;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link UnitConverter}.
 * Covers: piece ↔ gram ballpark, unit ↔ unit fixed ratios, edge cases.
 */
class UnitConverterTest {

  private UnitConverter converter;

  @BeforeEach
  void setUp() {
    // Small test table — real app would load from CSV
    converter = new UnitConverter(Map.of(
        "chicken thigh", 250.0,
        "onion",         150.0,
        "egg",           50.0,
        "garlic clove",  5.0
    ));
  }

  // ======== Piece → Grams ========

  @Test
  void piecesToGrams_wholeNumber() {
    assertEquals(1000.0, converter.piecesToGrams("chicken thigh", 4));
  }

  @Test
  void piecesToGrams_fractional() {
    // Half an onion ≈ 75g
    assertEquals(75.0, converter.piecesToGrams("onion", 0.5));
  }

  @Test
  void piecesToGrams_zero_returnsZero() {
    assertEquals(0.0, converter.piecesToGrams("egg", 0));
  }

  @Test
  void piecesToGrams_unknownIngredient_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> converter.piecesToGrams("dragon fruit", 2));
  }

  @Test
  void piecesToGrams_negativePieces_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> converter.piecesToGrams("egg", -3));
  }

  // ======== Grams → Pieces ========

  @Test
  void gramsToPieces_exactDivision() {
    assertEquals(2.0, converter.gramsToPieces("chicken thigh", 500));
  }

  @Test
  void gramsToPieces_fractionalResult() {
    // 100g of onion ÷ 150g/piece ≈ 0.667 pieces
    assertEquals(100.0 / 150.0, converter.gramsToPieces("onion", 100), 0.001);
  }

  @Test
  void gramsToPieces_unknownIngredient_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> converter.gramsToPieces("dragon fruit", 500));
  }

  @Test
  void gramsToPieces_negativeGrams_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> converter.gramsToPieces("egg", -100));
  }

  // ======== hasBallpark ========

  @Test
  void hasBallpark_knownIngredient_returnsTrue() {
    assertTrue(converter.hasBallpark("chicken thigh"));
  }

  @Test
  void hasBallpark_unknownIngredient_returnsFalse() {
    assertFalse(converter.hasBallpark("dragon fruit"));
  }

  @Test
  void hasBallpark_caseInsensitive() {
    assertTrue(converter.hasBallpark("Chicken Thigh"));
  }

  // ======== Weight: toGrams ========

  @Test
  void toGrams_fromGram_identity() {
    assertEquals(500.0, converter.toGrams(500, UnitType.GRAM));
  }

  @Test
  void toGrams_fromKilogram() {
    assertEquals(2000.0, converter.toGrams(2, UnitType.KILOGRAM));
  }

  @Test
  void toGrams_fromPound() {
    assertEquals(453.6, converter.toGrams(1, UnitType.POUND), 0.1);
  }

  @Test
  void toGrams_fromOunce() {
    assertEquals(28.35, converter.toGrams(1, UnitType.OUNCE), 0.1);
  }

  @Test
  void toGrams_nonWeightUnit_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> converter.toGrams(100, UnitType.CUP));
  }

  @Test
  void toGrams_negativeQuantity_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> converter.toGrams(-5, UnitType.GRAM));
  }

  // ======== Weight: fromGrams ========

  @Test
  void fromGrams_toPound() {
    assertEquals(1.0, converter.fromGrams(453.6, UnitType.POUND), 0.1);
  }

  @Test
  void fromGrams_toKilogram() {
    assertEquals(0.5, converter.fromGrams(500, UnitType.KILOGRAM), 0.001);
  }

  // ======== Volume: toMilliliters ========

  @Test
  void toMilliliters_fromMl_identity() {
    assertEquals(250.0, converter.toMilliliters(250, UnitType.MILLILITER));
  }

  @Test
  void toMilliliters_fromLiter() {
    assertEquals(1000.0, converter.toMilliliters(1, UnitType.LITER));
  }

  @Test
  void toMilliliters_fromCup() {
    assertEquals(240.0, converter.toMilliliters(1, UnitType.CUP), 0.1);
  }

  @Test
  void toMilliliters_fromTablespoon() {
    assertEquals(14.79, converter.toMilliliters(1, UnitType.TABLESPOON), 0.1);
  }

  @Test
  void toMilliliters_fromTeaspoon() {
    assertEquals(4.93, converter.toMilliliters(1, UnitType.TEASPOON), 0.1);
  }

  @Test
  void toMilliliters_fromFluidOunce() {
    assertEquals(29.57, converter.toMilliliters(1, UnitType.FLUID_OUNCE), 0.1);
  }

  @Test
  void toMilliliters_nonVolumeUnit_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> converter.toMilliliters(100, UnitType.GRAM));
  }

  // ======== Volume: fromMilliliters ========

  @Test
  void fromMilliliters_toCup() {
    assertEquals(2.0, converter.fromMilliliters(480, UnitType.CUP), 0.01);
  }

  @Test
  void fromMilliliters_toLiter() {
    assertEquals(1.5, converter.fromMilliliters(1500, UnitType.LITER), 0.001);
  }

  // ======== Empty converter ========

  @Test
  void emptyConverter_piecesToGrams_throwsException() {
    UnitConverter empty = new UnitConverter();
    assertThrows(IllegalArgumentException.class,
        () -> empty.piecesToGrams("egg", 3));
  }

  @Test
  void emptyConverter_unitConversions_stillWork() {
    // Fixed ratios don't depend on the ballpark map
    UnitConverter empty = new UnitConverter();
    assertEquals(453.6, empty.toGrams(1, UnitType.POUND), 0.1);
  }
}