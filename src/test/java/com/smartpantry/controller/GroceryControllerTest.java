package com.smartpantry.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;
import com.smartpantry.strategy.CasualFormatter;
import com.smartpantry.strategy.GroceryListGenerator;
import com.smartpantry.strategy.ProfessionalFormatter;
import com.smartpantry.util.UnitConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GroceryController}. Covers: list generation, formatting, casual vs pro display,
 * edge cases.
 */
class GroceryControllerTest {

  private GroceryController controller;
  private UnitConverter converter;
  private MealPlan plan;
  private List<PantryItem> inventory;

  @BeforeEach
  void setUp() {
    converter =
        new UnitConverter(
            Map.of(
                "chicken thigh", 250.0,
                "onion", 150.0));
    GroceryListGenerator generator = new GroceryListGenerator();
    controller = new GroceryController(generator, new CasualFormatter(converter));

    // Recipe needs 500g chicken and 100g onion
    Ingredient chickenId =
        new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    Ingredient onionId = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);

    Recipe orangeChicken =
        new Recipe(
            "Orange Chicken",
            "Citrus chicken",
            List.of(chickenId, onionId),
            List.of("Prep", "Cook", "Serve"),
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY));

    plan = new MealPlan("Test", List.of(orangeChicken), 1, LocalDate.now());
  }

  // ---- Full coverage ----

  @Test
  void generateGroceryList_allInStock_emptyList() {
    Ingredient chickenPantry =
        new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    PantryItem chickenItem =
        new PantryItem(chickenPantry, 600, LocalDate.now(), LocalDate.now().plusDays(5), 100);

    Ingredient onionPantry = new Ingredient("onion", 0, UnitType.GRAM, CategoryType.VEGETABLE);
    PantryItem onionItem =
        new PantryItem(onionPantry, 200, LocalDate.now(), LocalDate.now().plusDays(7), 50);

    inventory = List.of(chickenItem, onionItem);

    List<GroceryItem> result = controller.generateGroceryList(plan, inventory);
    assertTrue(result.isEmpty());
  }

  // ---- Partial coverage ----

  @Test
  void generateGroceryList_partialStock_returnsDeficits() {
    // Only 200g chicken (need 500) and no onion
    Ingredient chickenPantry =
        new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    PantryItem lowChicken =
        new PantryItem(chickenPantry, 200, LocalDate.now(), LocalDate.now().plusDays(5), 50);

    inventory = List.of(lowChicken);

    List<GroceryItem> result = controller.generateGroceryList(plan, inventory);
    assertEquals(2, result.size()); // chicken deficit + onion deficit
  }

  // ---- Format: no shopping needed ----

  @Test
  void formatGroceryList_empty_showsNoShoppingMessage() {
    String result = controller.formatGroceryList(List.of());
    assertTrue(result.contains("no shopping required"));
  }

  // ---- Format: with deficits ----

  @Test
  void formatGroceryList_withItems_showsNumberedList() {
    Ingredient chickenPantry =
        new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    PantryItem lowChicken =
        new PantryItem(chickenPantry, 200, LocalDate.now(), LocalDate.now().plusDays(5), 50);
    inventory = List.of(lowChicken);

    List<GroceryItem> groceries = controller.generateGroceryList(plan, inventory);
    String result = controller.formatGroceryList(groceries);

    assertTrue(result.contains("Shopping List"));
    assertTrue(result.contains("1. Buy"));
    assertTrue(result.contains("chicken thigh"));
  }

  // ---- Formatter toggle ----

  @Test
  void formatGroceryList_proMode_showsGrams() {
    controller.setFormatter(new ProfessionalFormatter(converter));

    Ingredient chickenPantry =
        new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    PantryItem lowChicken =
        new PantryItem(chickenPantry, 200, LocalDate.now(), LocalDate.now().plusDays(5), 50);
    inventory = List.of(lowChicken);

    List<GroceryItem> groceries = controller.generateGroceryList(plan, inventory);
    String result = controller.formatGroceryList(groceries);

    assertTrue(result.contains("300g")); // 500 needed - 200 in stock = 300g deficit
    assertTrue(result.contains("chicken thigh"));
  }

  // ---- Convenience method ----

  @Test
  void generateAndFormat_combinesBothSteps() {
    inventory = List.of(); // empty pantry

    String result = controller.generateAndFormat(plan, inventory);

    assertTrue(result.contains("Shopping List"));
    assertTrue(result.contains("chicken thigh"));
    assertTrue(result.contains("onion"));
  }

  // ---- Validation ----

  @Test
  void constructor_nullGenerator_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new GroceryController(null, new CasualFormatter(converter)));
  }

  @Test
  void constructor_nullFormatter_throwsException() {
    assertThrows(
        NullPointerException.class, () -> new GroceryController(new GroceryListGenerator(), null));
  }

  @Test
  void setFormatter_null_throwsException() {
    assertThrows(NullPointerException.class, () -> controller.setFormatter(null));
  }
}
