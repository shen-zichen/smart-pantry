package com.smartpantry.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MealPlanGenerator}. Uses simple stub strategies to verify delegation and
 * swapping behavior. Full strategy logic is tested in each strategy's own test class.
 */
class MealPlanGeneratorTest {

  private Recipe orangeChicken;
  private Recipe friedRice;
  private List<PantryItem> inventory;
  private List<Recipe> recipes;

  /** A stub strategy that always picks the first recipe. */
  private final IMealPlanStrategy firstRecipeStrategy =
      (inv, rec) -> new MealPlan("First Pick", List.of(rec.get(0)), 1, LocalDate.now());

  /** A stub strategy that always picks the last recipe. */
  private final IMealPlanStrategy lastRecipeStrategy =
      (inv, rec) -> new MealPlan("Last Pick", List.of(rec.get(rec.size() - 1)), 1, LocalDate.now());

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

    recipes = List.of(orangeChicken, friedRice);

    PantryItem chickenItem =
        new PantryItem(chicken, 500, LocalDate.now(), LocalDate.now().plusDays(5), 100);
    inventory = List.of(chickenItem);
  }

  // ---- Delegation ----

  @Test
  void generatePlan_delegatesToStrategy() {
    MealPlanGenerator generator = new MealPlanGenerator(firstRecipeStrategy);
    MealPlan plan = generator.generatePlan(inventory, recipes);

    assertEquals("First Pick", plan.getStrategyName());
    assertEquals(orangeChicken, plan.getRecipes().get(0));
  }

  // ---- Strategy Swapping ----

  @Test
  void setStrategy_swapsBehaviorAtRuntime() {
    MealPlanGenerator generator = new MealPlanGenerator(firstRecipeStrategy);

    // First strategy picks orange chicken
    MealPlan plan1 = generator.generatePlan(inventory, recipes);
    assertEquals(orangeChicken, plan1.getRecipes().get(0));

    // Swap to last-pick strategy — now picks fried rice
    generator.setStrategy(lastRecipeStrategy);
    MealPlan plan2 = generator.generatePlan(inventory, recipes);
    assertEquals(friedRice, plan2.getRecipes().get(0));
  }

  @Test
  void getStrategy_returnsCurrentStrategy() {
    MealPlanGenerator generator = new MealPlanGenerator(firstRecipeStrategy);
    assertSame(firstRecipeStrategy, generator.getStrategy());

    generator.setStrategy(lastRecipeStrategy);
    assertSame(lastRecipeStrategy, generator.getStrategy());
  }

  // ---- Validation ----

  @Test
  void constructor_nullStrategy_throwsException() {
    assertThrows(NullPointerException.class, () -> new MealPlanGenerator(null));
  }

  @Test
  void setStrategy_null_throwsException() {
    MealPlanGenerator generator = new MealPlanGenerator(firstRecipeStrategy);
    assertThrows(NullPointerException.class, () -> generator.setStrategy(null));
  }

  @Test
  void generatePlan_nullInventory_throwsException() {
    MealPlanGenerator generator = new MealPlanGenerator(firstRecipeStrategy);
    assertThrows(NullPointerException.class, () -> generator.generatePlan(null, recipes));
  }

  @Test
  void generatePlan_nullRecipes_throwsException() {
    MealPlanGenerator generator = new MealPlanGenerator(firstRecipeStrategy);
    assertThrows(NullPointerException.class, () -> generator.generatePlan(inventory, null));
  }
}
