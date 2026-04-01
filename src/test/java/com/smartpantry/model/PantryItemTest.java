package com.smartpantry.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PantryItem}. Covers: construction, validation, consume/restock, low stock,
 * expiration queries.
 */
class PantryItemTest {

  private Ingredient chicken;
  private Ingredient salt;
  private PantryItem chickenItem;

  @BeforeEach
  void setUp() {
    chicken = new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    salt = new Ingredient("salt", 0, UnitType.GRAM, CategoryType.SPICE);

    chickenItem =
        new PantryItem(chicken, 500, LocalDate.of(2026, 3, 30), LocalDate.of(2026, 4, 5), 100);
  }

  // ---- Construction & Getters ----

  @Test
  void constructor_validInputs_storesAllFields() {
    assertEquals("chicken thigh", chickenItem.getName());
    assertEquals(500, chickenItem.getQuantityInStock());
    assertEquals(LocalDate.of(2026, 3, 30), chickenItem.getBoughtDate());
    assertEquals(LocalDate.of(2026, 4, 5), chickenItem.getExpirationDate());
    assertEquals(100, chickenItem.getLowStockThreshold());
    assertEquals(chicken, chickenItem.getIngredient());
  }

  @Test
  void constructor_nullExpirationDate_isAllowed() {
    // Salt doesn't expire
    PantryItem saltItem = new PantryItem(salt, 1000, LocalDate.of(2026, 1, 1), null, 50);
    assertNull(saltItem.getExpirationDate());
  }

  // ---- Validation ----

  @Test
  void constructor_nullIngredient_throwsException() {
    assertThrows(
        NullPointerException.class, () -> new PantryItem(null, 500, LocalDate.now(), null, 100));
  }

  @Test
  void constructor_nullBoughtDate_throwsException() {
    assertThrows(NullPointerException.class, () -> new PantryItem(chicken, 500, null, null, 100));
  }

  @Test
  void constructor_negativeQuantity_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PantryItem(chicken, -1, LocalDate.now(), null, 100));
  }

  @Test
  void constructor_negativeThreshold_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PantryItem(chicken, 500, LocalDate.now(), null, -10));
  }

  // ---- Consume ----

  @Test
  void consume_reducesQuantity() {
    chickenItem.consume(200);
    assertEquals(300, chickenItem.getQuantityInStock());
  }

  @Test
  void consume_moreThanAvailable_floorsAtZero() {
    chickenItem.consume(9999);
    assertEquals(0, chickenItem.getQuantityInStock());
  }

  @Test
  void consume_negativeAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> chickenItem.consume(-50));
  }

  // ---- Restock ----

  @Test
  void restock_increasesQuantity() {
    chickenItem.restock(300);
    assertEquals(800, chickenItem.getQuantityInStock());
  }

  @Test
  void restock_negativeAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> chickenItem.restock(-50));
  }

  // ---- Low Stock ----

  @Test
  void isLowStock_aboveThreshold_returnsFalse() {
    assertFalse(chickenItem.isLowStock()); // 500 > 100
  }

  @Test
  void isLowStock_atThreshold_returnsTrue() {
    chickenItem.consume(400); // 500 - 400 = 100, exactly at threshold
    assertTrue(chickenItem.isLowStock());
  }

  @Test
  void isLowStock_belowThreshold_returnsTrue() {
    chickenItem.consume(450); // 500 - 450 = 50 < 100
    assertTrue(chickenItem.isLowStock());
  }

  // ---- Expiration ----

  @Test
  void isExpiredBy_beforeExpirationDate_returnsFalse() {
    assertFalse(chickenItem.isExpiredBy(LocalDate.of(2026, 4, 4)));
  }

  @Test
  void isExpiredBy_onExpirationDate_returnsTrue() {
    assertTrue(chickenItem.isExpiredBy(LocalDate.of(2026, 4, 5)));
  }

  @Test
  void isExpiredBy_afterExpirationDate_returnsTrue() {
    assertTrue(chickenItem.isExpiredBy(LocalDate.of(2026, 4, 10)));
  }

  @Test
  void isExpiredBy_nullExpiration_returnsFalse() {
    // Non-perishable items never expire
    PantryItem saltItem = new PantryItem(salt, 1000, LocalDate.of(2026, 1, 1), null, 50);
    assertFalse(saltItem.isExpiredBy(LocalDate.of(2099, 12, 31)));
  }

  @Test
  void isExpiringSoon_nullExpiration_returnsFalse() {
    PantryItem saltItem = new PantryItem(salt, 1000, LocalDate.of(2026, 1, 1), null, 50);
    assertFalse(saltItem.isExpiringSoon(7));
  }

  // ---- toString ----

  @Test
  void toString_containsKeyInfo() {
    String result = chickenItem.toString();
    assertTrue(result.contains("chicken thigh"));
    assertTrue(result.contains("500"));
    assertTrue(result.contains("2026-04-05"));
  }
}
