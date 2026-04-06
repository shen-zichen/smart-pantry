package com.smartpantry.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;
import com.smartpantry.observer.InventoryManager;
import com.smartpantry.observer.LowStockAlertObserver;
import com.smartpantry.strategy.CasualFormatter;
import com.smartpantry.strategy.IUnitFormatter;
import com.smartpantry.strategy.ProfessionalFormatter;
import com.smartpantry.util.UnitConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PantryController}. Covers: add/remove/consume/restock, listing with formatter,
 * search, formatter toggle.
 */
class PantryControllerTest {

  private PantryController controller;
  private InventoryManager inventoryManager;
  private UnitConverter converter;
  private LowStockAlertObserver alertObserver;

  @BeforeEach
  void setUp() {
    inventoryManager = new InventoryManager(3);
    converter =
        new UnitConverter(
            Map.of(
                "chicken thigh", 250.0,
                "onion", 150.0,
                "egg", 50.0));
    IUnitFormatter formatter = new CasualFormatter(converter);

    // Wire up an alert observer so we can verify alerts fire
    alertObserver = new LowStockAlertObserver();
    inventoryManager.registerObserver(alertObserver);

    controller = new PantryController(inventoryManager, formatter, converter);
  }

  // ---- Add ----

  @Test
  void addItem_appearsInInventory() {
    controller.addItem(
        "chicken thigh",
        500,
        UnitType.GRAM,
        CategoryType.PROTEIN,
        LocalDate.now(),
        LocalDate.now().plusDays(5),
        100);

    List<PantryItem> inventory = controller.getInventory();
    assertEquals(1, inventory.size());
    assertEquals("chicken thigh", inventory.get(0).getName());
    assertEquals(500, inventory.get(0).getQuantityInStock());
  }

  // ---- Remove ----

  @Test
  void removeItem_removesFromInventory() {
    controller.addItem(
        "chicken thigh",
        500,
        UnitType.GRAM,
        CategoryType.PROTEIN,
        LocalDate.now(),
        LocalDate.now().plusDays(5),
        100);
    PantryItem item = controller.getInventory().get(0);

    assertTrue(controller.removeItem(item));
    assertEquals(0, controller.getInventory().size());
  }

  // ---- Consume ----

  @Test
  void consumeItem_reducesQuantity() {
    controller.addItem(
        "chicken thigh",
        500,
        UnitType.GRAM,
        CategoryType.PROTEIN,
        LocalDate.now(),
        LocalDate.now().plusDays(5),
        100);
    PantryItem item = controller.getInventory().get(0);

    controller.consumeItem(item, 200);

    assertEquals(300, item.getQuantityInStock());
  }

  @Test
  void consumeItem_belowThreshold_triggersAlert() {
    controller.addItem(
        "chicken thigh",
        500,
        UnitType.GRAM,
        CategoryType.PROTEIN,
        LocalDate.now(),
        LocalDate.now().plusDays(5),
        100);
    PantryItem item = controller.getInventory().get(0);

    controller.consumeItem(item, 450); // 50 remaining, threshold is 100

    assertFalse(alertObserver.getAlertHistory().isEmpty());
    assertTrue(alertObserver.getAlertHistory().get(0).contains("chicken thigh"));
  }

  // ---- Restock ----

  @Test
  void restockItem_increasesQuantity() {
    controller.addItem("rice", 500, UnitType.GRAM, CategoryType.GRAIN, LocalDate.now(), null, 100);
    PantryItem item = controller.getInventory().get(0);

    controller.restockItem(item, 300);

    assertEquals(800, item.getQuantityInStock());
  }

  // ---- List ----

  @Test
  void listAllItems_emptyPantry_showsMessage() {
    String result = controller.listAllItems();
    assertEquals("Your pantry is empty.", result);
  }

  @Test
  void listAllItems_usesActiveFormatter() {
    controller.addItem(
        "chicken thigh",
        500,
        UnitType.GRAM,
        CategoryType.PROTEIN,
        LocalDate.now(),
        LocalDate.now().plusDays(5),
        100);

    String casual = controller.listAllItems();
    // CasualFormatter with ballpark should show "about 2 chicken thigh"
    assertTrue(casual.contains("chicken thigh"));

    // Swap to professional
    controller.setFormatter(new ProfessionalFormatter(converter));
    String pro = controller.listAllItems();
    // ProfessionalFormatter should show "500g chicken thigh"
    assertTrue(pro.contains("500g"));
    assertTrue(pro.contains("chicken thigh"));
  }

  // ---- Find ----

  @Test
  void findItemsByName_caseInsensitive() {
    controller.addItem(
        "chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN, LocalDate.now(), null, 100);

    List<PantryItem> found = controller.findItemsByName("CHICKEN THIGH");
    assertEquals(1, found.size());
  }

  @Test
  void findItemsByName_noMatch_returnsEmptyList() {
    controller.addItem(
        "chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN, LocalDate.now(), null, 100);

    List<PantryItem> found = controller.findItemsByName("tofu");
    assertTrue(found.isEmpty());
  }

  @Test
  void findItemsByName_multipleEntries_returnsAll() {
    // Two separate chicken entries (different units)
    controller.addItem(
        "chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN, LocalDate.now(), null, 100);
    controller.addItem(
        "chicken thigh", 2, UnitType.PIECE, CategoryType.PROTEIN, LocalDate.now(), null, 1);

    List<PantryItem> found = controller.findItemsByName("chicken thigh");
    assertEquals(2, found.size());
  }

  // ---- Formatter Toggle ----

  @Test
  void setFormatter_swapsFormatterAtRuntime() {
    ProfessionalFormatter pro = new ProfessionalFormatter(converter);
    controller.setFormatter(pro);
    assertSame(pro, controller.getFormatter());
  }

  @Test
  void setFormatter_null_throwsException() {
    assertThrows(NullPointerException.class, () -> controller.setFormatter(null));
  }

  // ---- Validation ----

  @Test
  void constructor_nullInventoryManager_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new PantryController(null, new CasualFormatter(converter), converter));
  }
}
