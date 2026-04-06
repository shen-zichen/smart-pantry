package com.smartpantry.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;
import com.smartpantry.observer.InventoryManager;
import com.smartpantry.strategy.CasualFormatter;
import com.smartpantry.strategy.IUnitFormatter;
import com.smartpantry.strategy.MealPlanGenerator;
import com.smartpantry.strategy.StrictInventoryStrategy;
import com.smartpantry.strategy.ZeroWasteStrategy;
import com.smartpantry.util.UnitConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MealPlanController}. Covers: plan generation, strategy swapping, post-meal
 * consume, leftovers, formatting.
 */
class MealPlanControllerTest {

  private MealPlanController mealPlanController;
  private PantryController pantryController;
  private Recipe orangeChicken;
  private Recipe friedRice;

  @BeforeEach
  void setUp() {
    UnitConverter converter =
        new UnitConverter(
            Map.of(
                "chicken thigh", 250.0,
                "onion", 150.0));
    IUnitFormatter formatter = new CasualFormatter(converter);

    // Meal plan controller
    MealPlanGenerator generator = new MealPlanGenerator(new StrictInventoryStrategy(3));
    mealPlanController = new MealPlanController(generator, formatter);

    // Pantry controller with stocked items
    InventoryManager inventoryManager = new InventoryManager(3);
    pantryController = new PantryController(inventoryManager, formatter, converter);

    pantryController.addItem(
        "chicken thigh",
        600,
        UnitType.GRAM,
        CategoryType.PROTEIN,
        LocalDate.now(),
        LocalDate.now().plusDays(5),
        100);
    pantryController.addItem(
        "onion",
        200,
        UnitType.GRAM,
        CategoryType.VEGETABLE,
        LocalDate.now(),
        LocalDate.now().plusDays(7),
        50);
    pantryController.addItem(
        "rice", 1000, UnitType.GRAM, CategoryType.GRAIN, LocalDate.now(), null, 100);

    // Recipes
    Ingredient chickenId =
        new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    Ingredient onionId = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    Ingredient riceId = new Ingredient("rice", 300, UnitType.GRAM, CategoryType.GRAIN);
    List<String> steps = List.of("Prep", "Cook", "Serve");

    orangeChicken =
        new Recipe(
            "Orange Chicken",
            "Citrus chicken",
            List.of(chickenId, onionId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY, RecipeTag.QUICK));

    friedRice =
        new Recipe(
            "Fried Rice",
            "Classic rice",
            List.of(riceId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.QUICK, RecipeTag.BUDGET_FRIENDLY));
  }

  // ---- Plan Generation ----

  @Test
  void generatePlan_returnsAndStoresPlan() {
    List<PantryItem> inventory = pantryController.getInventory();
    List<Recipe> recipes = List.of(orangeChicken, friedRice);

    MealPlan plan = mealPlanController.generatePlan(inventory, recipes);

    assertNotNull(plan);
    assertSame(plan, mealPlanController.getCurrentPlan());
  }

  @Test
  void getCurrentPlan_beforeGeneration_returnsNull() {
    assertNull(mealPlanController.getCurrentPlan());
  }

  // ---- Strategy Swapping ----

  @Test
  void setStrategy_changesPlanBehavior() {
    List<PantryItem> inventory = pantryController.getInventory();
    List<Recipe> recipes = List.of(orangeChicken, friedRice);

    // Generate with strict
    MealPlan strict = mealPlanController.generatePlan(inventory, recipes);
    assertEquals("Strict Inventory", strict.getStrategyName());

    // Swap to zero waste
    mealPlanController.setStrategy(new ZeroWasteStrategy(3, 2));
    MealPlan zeroWaste = mealPlanController.generatePlan(inventory, recipes);
    assertEquals("Zero Waste", zeroWaste.getStrategyName());
  }

  // ---- Post-Meal Flow ----

  @Test
  void postMealConsume_deductsIngredientsFromPantry() {
    // Chicken starts at 600g, recipe needs 500g
    mealPlanController.postMealConsume(orangeChicken, pantryController);

    List<PantryItem> chicken = pantryController.findItemsByName("chicken thigh");
    assertEquals(100, chicken.get(0).getQuantityInStock());

    List<PantryItem> onion = pantryController.findItemsByName("onion");
    assertEquals(100, onion.get(0).getQuantityInStock());
  }

  // ---- Leftovers ----

  @Test
  void getLeftovers_findsSmallQuantityItems() {
    // Consume most of the chicken, leaving 100g
    mealPlanController.postMealConsume(orangeChicken, pantryController);

    // Threshold 150 — chicken at 100g qualifies, onion at 100g qualifies
    List<PantryItem> leftovers = mealPlanController.getLeftovers(pantryController, 150);

    assertTrue(leftovers.size() >= 2);
    assertTrue(leftovers.stream().anyMatch(i -> i.getName().equals("chicken thigh")));
    assertTrue(leftovers.stream().anyMatch(i -> i.getName().equals("onion")));
  }

  @Test
  void getLeftovers_excludesZeroQuantity() {
    // Consume all chicken
    PantryItem chicken = pantryController.findItemsByName("chicken thigh").get(0);
    pantryController.consumeItem(chicken, 600);

    List<PantryItem> leftovers = mealPlanController.getLeftovers(pantryController, 150);

    // Chicken at 0 should NOT appear as a leftover
    assertTrue(leftovers.stream().noneMatch(i -> i.getName().equals("chicken thigh")));
  }

  // ---- Formatting ----

  @Test
  void formatPlan_containsStrategyAndRecipes() {
    List<PantryItem> inventory = pantryController.getInventory();
    MealPlan plan = mealPlanController.generatePlan(inventory, List.of(orangeChicken, friedRice));

    String formatted = mealPlanController.formatPlan(plan);

    assertTrue(formatted.contains("Strict Inventory"));
    assertTrue(formatted.contains("Day 1"));
  }

  @Test
  void formatLeftovers_noLeftovers_showsCleanMessage() {
    String result = mealPlanController.formatLeftovers(List.of());
    assertTrue(result.contains("No leftovers"));
  }

  @Test
  void formatLeftovers_withLeftovers_showsItems() {
    mealPlanController.postMealConsume(orangeChicken, pantryController);
    List<PantryItem> leftovers = mealPlanController.getLeftovers(pantryController, 150);

    String result = mealPlanController.formatLeftovers(leftovers);

    assertTrue(result.contains("leftovers"));
    assertTrue(result.contains("chicken thigh"));
  }

  // ---- Validation ----

  @Test
  void constructor_nullGenerator_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new MealPlanController(null, new CasualFormatter(new UnitConverter(Map.of()))));
  }

  @Test
  void constructor_nullFormatter_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new MealPlanController(new MealPlanGenerator(new StrictInventoryStrategy(3)), null));
  }
}
