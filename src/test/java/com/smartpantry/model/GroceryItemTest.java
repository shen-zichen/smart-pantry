package com.smartpantry.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GroceryItem}. Covers: construction, validation, getters, convenience delegates,
 * toString.
 */
class GroceryItemTest {

  private final Ingredient chicken =
      new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);

  // ---- Construction & Getters ----

  @Test
  void constructor_validInputs_storesAllFields() {
    GroceryItem item = new GroceryItem(chicken, 300);

    assertSame(chicken, item.getIngredient());
    assertEquals(300, item.getDeficit());
  }

  @Test
  void getName_delegatesToIngredient() {
    GroceryItem item = new GroceryItem(chicken, 300);
    assertEquals("chicken thigh", item.getName());
  }

  @Test
  void getUnitType_delegatesToIngredient() {
    GroceryItem item = new GroceryItem(chicken, 300);
    assertEquals(UnitType.GRAM, item.getUnitType());
  }

  // ---- Validation ----

  @Test
  void constructor_nullIngredient_throwsException() {
    assertThrows(NullPointerException.class, () -> new GroceryItem(null, 300));
  }

  @Test
  void constructor_zeroDeficit_throwsException() {
    // Zero deficit means you don't need to buy anything — shouldn't exist
    assertThrows(IllegalArgumentException.class, () -> new GroceryItem(chicken, 0));
  }

  @Test
  void constructor_negativeDeficit_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new GroceryItem(chicken, -100));
  }

  // ---- toString ----

  @Test
  void toString_showsDeficitAndName() {
    GroceryItem item = new GroceryItem(chicken, 300);
    String result = item.toString();

    assertTrue(result.contains("300"));
    assertTrue(result.contains("chicken thigh"));
    assertTrue(result.contains("g")); // abbreviation from UnitType.GRAM
  }
}
