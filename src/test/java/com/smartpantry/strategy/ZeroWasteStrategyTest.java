package com.smartpantry.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ZeroWasteStrategy}. Covers: scoring, sorting by expiring ingredients,
 * cookability checks, edge cases.
 */
class ZeroWasteStrategyTest {

  private ZeroWasteStrategy strategy;

  // Ingredients (identity objects — quantity 0, used for matching)
  private Ingredient chickenId;
  private Ingredient onionId;
  private Ingredient riceId;
  private Ingredient garlicId;

  // Pantry items
  private PantryItem chickenItem; // expiring soon
  private PantryItem onionItem; // expiring soon
  private PantryItem riceItem; // not expiring
  private PantryItem garlicItem; // expiring soon

  // Recipes
  private Recipe orangeChicken; // uses chicken + onion (2 expiring)
  private Recipe chickenSoup; // uses chicken + garlic (2 expiring)
  private Recipe friedRice; // uses rice only (0 expiring)
  private Recipe stirFry; // uses chicken + onion + garlic (3 expiring)

  @BeforeEach
  void setUp() {
    strategy = new ZeroWasteStrategy(3, 2); // 3-day window, max 2 recipes

    // Ingredient identities (for recipe definitions)
    chickenId = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    onionId = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    riceId = new Ingredient("rice", 300, UnitType.GRAM, CategoryType.GRAIN);
    garlicId = new Ingredient("garlic", 10, UnitType.GRAM, CategoryType.SPICE);

    // Pantry items — chicken, onion, garlic expiring in 2 days; rice not expiring
    Ingredient chickenPantry =
        new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    chickenItem =
        new PantryItem(chickenPantry, 600, LocalDate.now(), LocalDate.now().plusDays(2), 100);

    Ingredient onionPantry = new Ingredient("onion", 0, UnitType.GRAM, CategoryType.VEGETABLE);
    onionItem = new PantryItem(onionPantry, 200, LocalDate.now(), LocalDate.now().plusDays(2), 50);

    Ingredient ricePantry = new Ingredient("rice", 0, UnitType.GRAM, CategoryType.GRAIN);
    riceItem = new PantryItem(ricePantry, 1000, LocalDate.now(), null, 100); // rice doesn't expire

    Ingredient garlicPantry = new Ingredient("garlic", 0, UnitType.GRAM, CategoryType.SPICE);
    garlicItem = new PantryItem(garlicPantry, 50, LocalDate.now(), LocalDate.now().plusDays(1), 10);

    List<String> steps = List.of("Prep", "Cook", "Serve");

    orangeChicken =
        new Recipe(
            "Orange Chicken",
            "Citrus chicken",
            List.of(chickenId, onionId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY, RecipeTag.ZERO_WASTE));

    chickenSoup =
        new Recipe(
            "Chicken Soup",
            "Warm soup",
            List.of(chickenId, garlicId),
            steps,
            2,
            CuisineType.AMERICAN,
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

    stirFry =
        new Recipe(
            "Stir Fry",
            "Quick veggie stir fry",
            List.of(chickenId, onionId, garlicId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.QUICK, RecipeTag.ZERO_WASTE));
  }

  // ---- Core scoring & ranking ----

  @Test
  void generatePlan_picksRecipesUsingMostExpiringIngredients() {
    // stirFry uses 3 expiring items (chicken, onion, garlic) — highest score
    // orangeChicken uses 2 expiring (chicken, onion) — second
    // friedRice uses 0 expiring (rice doesn't expire) — lowest
    List<PantryItem> inventory = List.of(chickenItem, onionItem, riceItem, garlicItem);
    List<Recipe> recipes = List.of(orangeChicken, friedRice, stirFry);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals("Stir Fry", plan.getRecipes().get(0).getName()); // score 3
    assertEquals("Orange Chicken", plan.getRecipes().get(1).getName()); // score 2
  }

  @Test
  void generatePlan_respectsMaxRecipes() {
    List<PantryItem> inventory = List.of(chickenItem, onionItem, riceItem, garlicItem);
    List<Recipe> recipes = List.of(orangeChicken, chickenSoup, friedRice, stirFry);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals(2, plan.getRecipeCount()); // maxRecipes is 2
  }

  // ---- Cookability ----

  @Test
  void generatePlan_skipsRecipesWithInsufficientStock() {
    // Pantry only has 100g chicken — not enough for 500g recipe
    Ingredient lowChickenPantry =
        new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
    PantryItem lowChicken =
        new PantryItem(lowChickenPantry, 100, LocalDate.now(), LocalDate.now().plusDays(2), 50);

    List<PantryItem> inventory = List.of(lowChicken, riceItem);
    List<Recipe> recipes = List.of(orangeChicken, friedRice);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    // Orange chicken needs 500g but we only have 100g — skipped
    // Fried rice is cookable but has 0 expiring score — falls to fallback
    assertEquals("Fried Rice", plan.getRecipes().get(0).getName());
  }

  // ---- Expiration window ----

  @Test
  void generatePlan_ignoresItemsOutsideExpirationWindow() {
    // Only rice in pantry, not expiring — no expiring items to match
    List<PantryItem> inventory = List.of(riceItem);
    List<Recipe> recipes = List.of(friedRice);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    // friedRice is cookable but scored 0 on expiring — picked by fallback
    assertEquals(1, plan.getRecipeCount());
    assertEquals("Fried Rice", plan.getRecipes().get(0).getName());
  }

  // ---- Edge cases ----

  @Test
  void generatePlan_noExpiringItems_fallsBackToCookable() {
    List<PantryItem> inventory = List.of(riceItem); // nothing expiring
    List<Recipe> recipes = List.of(friedRice);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals(1, plan.getRecipeCount());
  }

  @Test
  void generatePlan_strategyNameIsZeroWaste() {
    List<PantryItem> inventory = List.of(chickenItem, onionItem);
    List<Recipe> recipes = List.of(orangeChicken);

    MealPlan plan = strategy.generatePlan(inventory, recipes);

    assertEquals("Zero Waste", plan.getStrategyName());
  }

  // ---- Validation ----

  @Test
  void constructor_negativeWindow_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new ZeroWasteStrategy(-1, 3));
  }

  @Test
  void constructor_zeroMaxRecipes_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new ZeroWasteStrategy(3, 0));
  }
}
