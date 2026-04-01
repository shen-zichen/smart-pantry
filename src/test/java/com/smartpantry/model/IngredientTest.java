package com.smartpantry.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Ingredient}.
 * Covers: construction, getters, validation, equals/hashCode contract, toString.
 */
class IngredientTest {

  // ---- Construction & Getters ----

  @Test
  void constructor_validInputs_storesAllFields() {
    Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);

    assertEquals("chicken thigh", chicken.getName());
    assertEquals(500, chicken.getQuantity());
    assertEquals(UnitType.GRAM, chicken.getUnitType());
    assertEquals(CategoryType.PROTEIN, chicken.getCategoryType());
  }

  @Test
  void constructor_zeroQuantity_isAllowed() {
    // Zero is valid — you might have just used up an ingredient
    Ingredient empty = new Ingredient("milk", 0, UnitType.LITER, CategoryType.DAIRY);
    assertEquals(0, empty.getQuantity());
  }

  // ---- Validation ----

  @Test
  void constructor_nullName_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Ingredient(null, 100, UnitType.GRAM, CategoryType.PROTEIN));
  }

  @Test
  void constructor_blankName_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Ingredient("   ", 100, UnitType.GRAM, CategoryType.PROTEIN));
  }

  @Test
  void constructor_negativeQuantity_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Ingredient("rice", -1, UnitType.GRAM, CategoryType.GRAIN));
  }

  @Test
  void constructor_nullUnitType_throwsException() {
    assertThrows(NullPointerException.class,
        () -> new Ingredient("rice", 500, null, CategoryType.GRAIN));
  }

  @Test
  void constructor_nullCategoryType_throwsException() {
    assertThrows(NullPointerException.class,
        () -> new Ingredient("rice", 500, UnitType.GRAM, null));
  }

  // ---- Equals & HashCode ----

  @Test
  void equals_sameFields_isEqual() {
    Ingredient a = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    Ingredient b = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    assertEquals(a, b);
  }

  @Test
  void equals_differentQuantity_stillEqual() {
    // Identity doesn't include quantity — 500g chicken IS the same ingredient as 200g chicken
    Ingredient pantry = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    Ingredient recipe = new Ingredient("chicken thigh", 200, UnitType.GRAM, CategoryType.PROTEIN);
    assertEquals(pantry, recipe);
  }

  @Test
  void equals_differentUnit_notEqual() {
    // "2 pieces of chicken" and "500g of chicken" are tracked separately
    Ingredient byPiece = new Ingredient("chicken thigh", 2, UnitType.PIECE, CategoryType.PROTEIN);
    Ingredient byGram = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    assertNotEquals(byPiece, byGram);
  }

  @Test
  void equals_differentName_notEqual() {
    Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    Ingredient beef = new Ingredient("beef", 500, UnitType.GRAM, CategoryType.PROTEIN);
    assertNotEquals(chicken, beef);
  }

  @Test
  void equals_differentCategory_notEqual() {
    // Edge case: same name but different category (unlikely but contractually correct)
    Ingredient a = new Ingredient("tofu", 200, UnitType.GRAM, CategoryType.PROTEIN);
    Ingredient b = new Ingredient("tofu", 200, UnitType.GRAM, CategoryType.SNACK);
    assertNotEquals(a, b);
  }

  @Test
  void equals_null_notEqual() {
    Ingredient a = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    assertNotEquals(null, a);
  }

  @Test
  void equals_differentType_notEqual() {
    Ingredient a = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    assertNotEquals("onion", a);
  }

  @Test
  void equals_sameReference_isEqual() {
    Ingredient a = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    assertEquals(a, a);
  }

  @Test
  void hashCode_equalObjects_sameHash() {
    // Contract: if equals() is true, hashCode() must match
    Ingredient a = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    Ingredient b = new Ingredient("onion", 300, UnitType.GRAM, CategoryType.VEGETABLE);
    assertEquals(a.hashCode(), b.hashCode());
  }

  // ---- toString ----

  @Test
  void toString_containsAllFields() {
    Ingredient i = new Ingredient("garlic", 3, UnitType.PIECE, CategoryType.SPICE);
    String result = i.toString();

    assertTrue(result.contains("garlic"));
    assertTrue(result.contains("3"));
    assertTrue(result.contains("PIECE"));
    assertTrue(result.contains("SPICE"));
  }
}