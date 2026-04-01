package com.smartpantry.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MealPlan}. Covers: construction, validation, defensive copies, getters,
 * toString.
 */
class MealPlanTest {

  private Recipe orangeChicken;
  private Recipe friedRice;
  private List<Recipe> recipes;

  @BeforeEach
  void setUp() {
    Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    Ingredient rice = new Ingredient("rice", 300, UnitType.GRAM, CategoryType.GRAIN);
    List<String> steps = List.of("Prep", "Cook", "Serve");

    orangeChicken =
        new Recipe(
            "Orange Chicken",
            "Crispy citrus chicken",
            List.of(chicken),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY, RecipeTag.QUICK));

    friedRice =
        new Recipe(
            "Fried Rice",
            "Classic fried rice",
            List.of(rice),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.QUICK, RecipeTag.BUDGET_FRIENDLY));

    recipes = new ArrayList<>(List.of(orangeChicken, friedRice));
  }

  private MealPlan makePlan() {
    return new MealPlan("Zero Waste", recipes, 3, LocalDate.of(2026, 3, 31));
  }

  // ---- Construction & Getters ----

  @Test
  void constructor_validInputs_storesAllFields() {
    MealPlan plan = makePlan();

    assertEquals("Zero Waste", plan.getStrategyName());
    assertEquals(2, plan.getRecipeCount());
    assertEquals(3, plan.getDays());
    assertEquals(LocalDate.of(2026, 3, 31), plan.getCreatedDate());
  }

  @Test
  void getRecipes_returnsAllRecipesInOrder() {
    MealPlan plan = makePlan();
    List<Recipe> result = plan.getRecipes();

    assertEquals(2, result.size());
    assertEquals(orangeChicken, result.get(0));
    assertEquals(friedRice, result.get(1));
  }

  // ---- Validation ----

  @Test
  void constructor_nullStrategyName_throwsException() {
    assertThrows(NullPointerException.class, () -> new MealPlan(null, recipes, 3, LocalDate.now()));
  }

  @Test
  void constructor_blankStrategyName_throwsException() {
    assertThrows(
        IllegalArgumentException.class, () -> new MealPlan("  ", recipes, 3, LocalDate.now()));
  }

  @Test
  void constructor_nullRecipes_throwsException() {
    assertThrows(
        NullPointerException.class, () -> new MealPlan("Budget", null, 3, LocalDate.now()));
  }

  @Test
  void constructor_emptyRecipes_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new MealPlan("Budget", List.of(), 3, LocalDate.now()));
  }

  @Test
  void constructor_zeroDays_throwsException() {
    assertThrows(
        IllegalArgumentException.class, () -> new MealPlan("Budget", recipes, 0, LocalDate.now()));
  }

  @Test
  void constructor_negativeDays_throwsException() {
    assertThrows(
        IllegalArgumentException.class, () -> new MealPlan("Budget", recipes, -1, LocalDate.now()));
  }

  @Test
  void constructor_nullCreatedDate_throwsException() {
    assertThrows(NullPointerException.class, () -> new MealPlan("Budget", recipes, 3, null));
  }

  // ---- Defensive Copies ----

  @Test
  void constructor_defensiveCopy_callerCannotMutateRecipes() {
    MealPlan plan = makePlan();
    recipes.clear(); // mutate the original list
    assertEquals(2, plan.getRecipeCount()); // plan is unaffected
  }

  @Test
  void getRecipes_returnsCopy_cannotMutateInternal() {
    MealPlan plan = makePlan();
    List<Recipe> returned = plan.getRecipes();
    returned.clear(); // mutate the returned list
    assertEquals(2, plan.getRecipeCount()); // plan is unaffected
  }

  // ---- toString ----

  @Test
  void toString_containsKeyInfo() {
    MealPlan plan = makePlan();
    String result = plan.toString();

    assertTrue(result.contains("Zero Waste"));
    assertTrue(result.contains("2")); // recipe count
    assertTrue(result.contains("3")); // days
  }
}
