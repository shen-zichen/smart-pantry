package com.smartpantry.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PantryFirstStrategy}. Covers: scoring by pantry coverage, cookability, ranking,
 * fallback, edge cases.
 */
class PantryFirstStrategyTest {

  private PantryFirstStrategy strategy;

  // Ingredients for recipes
  private Ingredient chickenId;
  private Ingredient onionId;
  private Ingredient riceId;
  private Ingredient tofuId;

  // Pantry items — we have chicken, onion, and rice but NOT tofu
  private PantryItem chickenItem;
  private PantryItem onionItem;
  private PantryItem riceItem;

  // Recipes
  private Recipe orangeChicken; // needs chicken + onion (both in pantry) → score 2
  private Recipe friedRice; // needs rice (in pantry) → score 1
  private Recipe tofuStirFry; // needs tofu + onion (tofu missing) → not cookable
  private Recipe fullDinner; // needs chicken + onion + rice (all in pantry) → score 3

  @BeforeEach
  void setUp() {
    strategy = new PantryFirstStrategy(2); // max 2 recipes

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

    fullDinner =
        new Recipe(
            "Full Dinner",
            "Chicken rice bowl",
            List.of(chickenId, onionId, riceId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY));
  }

  // ---- Core ranking ----

  @Test
  void generatePlan_picksRecipesWithMostPantryCoverage() {
    // fullDinner uses 3 pantry items (score 3), orangeChicken uses 2 (score 2)
    List<PantryItem> inventory = List.of(chickenItem, onionItem, riceItem);
    List<Recipe> recipes = List.of(orangeChicken, friedRice, fullDinner);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals("Full Dinner", plan.getRecipes().get(0).getName()); // score 3
    assertEquals("Orange Chicken", plan.getRecipes().get(1).getName()); // score 2
  }

  @Test
  void generatePlan_respectsMaxRecipes() {
    List<PantryItem> inventory = List.of(chickenItem, onionItem, riceItem);
    List<Recipe> recipes = List.of(orangeChicken, friedRice, fullDinner);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals(2, plan.getRecipeCount());
  }

  // ---- Cookability ----

  @Test
  void generatePlan_skipsUncookableRecipes() {
    // We don't have tofu — tofuStirFry should be skipped
    List<PantryItem> inventory = List.of(chickenItem, onionItem, riceItem);
    List<Recipe> recipes = List.of(tofuStirFry, friedRice);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    // Only fried rice is cookable
    assertEquals("Fried Rice", plan.getRecipes().get(0).getName());
  }

  @Test
  void generatePlan_skipsRecipesWithInsufficientQuantity() {
    // Pantry has only 100g chicken — recipe needs 500g
    Ingredient lowChickenPantry =
        new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    PantryItem lowChicken =
        new PantryItem(lowChickenPantry, 100, LocalDate.now(), LocalDate.now().plusDays(5), 50);

    List<PantryItem> inventory = List.of(lowChicken, riceItem);
    List<Recipe> recipes = List.of(orangeChicken, friedRice);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals("Fried Rice", plan.getRecipes().get(0).getName());
  }

  // ---- Fallback ----

  @Test
  void generatePlan_nothingCookable_fallsBackToFirstRecipe() {
    // Empty pantry — nothing is cookable
    List<PantryItem> inventory = List.of();
    List<Recipe> recipes = List.of(orangeChicken, friedRice);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    // Falls back to first recipe
    assertEquals(1, plan.getRecipeCount());
    assertEquals("Orange Chicken", plan.getRecipes().get(0).getName());
  }

  // ---- Metadata ----

  @Test
  void generatePlan_strategyNameIsPantryFirst() {
    List<PantryItem> inventory = List.of(chickenItem, onionItem);
    List<Recipe> recipes = List.of(orangeChicken);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals("Pantry First", plan.getStrategyName());
  }

  // ---- Validation ----

  @Test
  void constructor_zeroMaxRecipes_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new PantryFirstStrategy(0));
  }

  @Test
  void constructor_negativeMaxRecipes_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new PantryFirstStrategy(-1));
  }
}
