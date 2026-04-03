package com.smartpantry.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link StrictInventoryStrategy}. Covers: fully cookable filtering, quantity checks, max
 * limit, fallback, edge cases.
 */
class StrictInventoryStrategyTest {

  private StrictInventoryStrategy strategy;

  private Ingredient chickenId;
  private Ingredient onionId;
  private Ingredient riceId;
  private Ingredient tofuId;

  private PantryItem chickenItem;
  private PantryItem onionItem;
  private PantryItem riceItem;

  private Recipe orangeChicken; // needs chicken + onion
  private Recipe friedRice; // needs rice
  private Recipe tofuStirFry; // needs tofu + onion (tofu NOT in pantry)

  @BeforeEach
  void setUp() {
    strategy = new StrictInventoryStrategy(3);

    chickenId = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    onionId = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    riceId = new Ingredient("rice", 300, UnitType.GRAM, CategoryType.GRAIN);
    tofuId = new Ingredient("tofu", 200, UnitType.GRAM, CategoryType.PROTEIN);

    Ingredient chickenPantry =
        new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    chickenItem =
        new PantryItem(chickenPantry, 600, LocalDate.now(), LocalDate.now().plusDays(5), 100);

    Ingredient onionPantry = new Ingredient("onion", 0, UnitType.GRAM, CategoryType.VEGETABLE);
    onionItem = new PantryItem(onionPantry, 200, LocalDate.now(), LocalDate.now().plusDays(7), 50);

    Ingredient ricePantry = new Ingredient("rice", 0, UnitType.GRAM, CategoryType.GRAIN);
    riceItem = new PantryItem(ricePantry, 1000, LocalDate.now(), null, 100);

    List<String> steps = List.of("Prep", "Cook", "Serve");

    orangeChicken =
        new Recipe(
            "Orange Chicken",
            "Citrus chicken",
            List.of(chickenId, onionId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY));

    friedRice =
        new Recipe(
            "Fried Rice",
            "Classic rice",
            List.of(riceId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.BUDGET_FRIENDLY));

    tofuStirFry =
        new Recipe(
            "Tofu Stir Fry",
            "Tofu with onion",
            List.of(tofuId, onionId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.VEGAN));
  }

  // ---- Core filtering ----

  @Test
  void generatePlan_onlyIncludesFullyCookableRecipes() {
    List<PantryItem> inventory = List.of(chickenItem, onionItem, riceItem);
    List<Recipe> recipes = List.of(orangeChicken, friedRice, tofuStirFry);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    // orangeChicken and friedRice are cookable, tofuStirFry is not
    assertEquals(2, plan.getRecipeCount());
    assertTrue(plan.getRecipes().stream().noneMatch(r -> r.getName().equals("Tofu Stir Fry")));
  }

  @Test
  void generatePlan_preservesRecipeOrder() {
    List<PantryItem> inventory = List.of(chickenItem, onionItem, riceItem);
    List<Recipe> recipes = List.of(friedRice, orangeChicken);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    // Both cookable — should maintain original order
    assertEquals("Fried Rice", plan.getRecipes().get(0).getName());
    assertEquals("Orange Chicken", plan.getRecipes().get(1).getName());
  }

  // ---- Quantity checks ----

  @Test
  void generatePlan_rejectsRecipeWhenQuantityInsufficient() {
    // Only 100g chicken — recipe needs 500g
    Ingredient lowChickenPantry =
        new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    PantryItem lowChicken =
        new PantryItem(lowChickenPantry, 100, LocalDate.now(), LocalDate.now().plusDays(5), 50);

    List<PantryItem> inventory = List.of(lowChicken, onionItem, riceItem);
    List<Recipe> recipes = List.of(orangeChicken, friedRice);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    // Orange chicken rejected (not enough), fried rice accepted
    assertEquals(1, plan.getRecipeCount());
    assertEquals("Fried Rice", plan.getRecipes().get(0).getName());
  }

  // ---- Max recipes ----

  @Test
  void generatePlan_respectsMaxRecipes() {
    StrictInventoryStrategy limited = new StrictInventoryStrategy(1);

    List<PantryItem> inventory = List.of(chickenItem, onionItem, riceItem);
    List<Recipe> recipes = List.of(orangeChicken, friedRice);

    MealPlan plan = limited.generatePlan(inventory, recipes);

    assertEquals(1, plan.getRecipeCount());
  }

  // ---- Fallback ----

  @Test
  void generatePlan_nothingCookable_fallsBackToFirstRecipe() {
    List<PantryItem> inventory = List.of(); // empty pantry
    List<Recipe> recipes = List.of(orangeChicken, friedRice);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals(1, plan.getRecipeCount());
    assertEquals("Orange Chicken", plan.getRecipes().get(0).getName());
  }

  // ---- Metadata ----

  @Test
  void generatePlan_strategyNameIsStrictInventory() {
    List<PantryItem> inventory = List.of(riceItem);
    List<Recipe> recipes = List.of(friedRice);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals("Strict Inventory", plan.getStrategyName());
  }

  // ---- Validation ----

  @Test
  void constructor_zeroMaxRecipes_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new StrictInventoryStrategy(0));
  }

  @Test
  void constructor_negativeMaxRecipes_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new StrictInventoryStrategy(-1));
  }
}
