package com.smartpantry.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;
import com.smartpantry.strategy.CasualFormatter;
import com.smartpantry.strategy.ProfessionalFormatter;
import com.smartpantry.util.RecipeScaler;
import com.smartpantry.util.UnitConverter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RecipeController}. Covers: library management, search/filter, scaling,
 * twin-swap, formatting.
 */
class RecipeControllerTest {

  private RecipeController controller;
  private UnitConverter converter;
  private Recipe orangeChicken;
  private Recipe friedRice;
  private Ingredient chickenId;
  private Ingredient onionId;
  private Ingredient riceId;

  @BeforeEach
  void setUp() {
    converter =
        new UnitConverter(
            Map.of(
                "chicken thigh", 250.0,
                "onion", 150.0));
    RecipeScaler scaler = new RecipeScaler();
    controller = new RecipeController(scaler, new CasualFormatter(converter));

    chickenId = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    onionId = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    riceId = new Ingredient("rice", 300, UnitType.GRAM, CategoryType.GRAIN);

    List<String> steps = List.of("Prep", "Cook", "Serve");

    orangeChicken =
        new Recipe(
            "Orange Chicken",
            "Crispy citrus chicken",
            List.of(chickenId, onionId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY, RecipeTag.QUICK));

    friedRice =
        new Recipe(
            "Fried Rice",
            "Classic fried rice",
            List.of(riceId),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.QUICK, RecipeTag.BUDGET_FRIENDLY));

    controller.addRecipe(orangeChicken);
    controller.addRecipe(friedRice);
  }

  // ---- Library Management ----

  @Test
  void addRecipe_increasesCount() {
    assertEquals(2, controller.getRecipeCount());
  }

  @Test
  void removeRecipe_decreasesCount() {
    controller.removeRecipe(orangeChicken);
    assertEquals(1, controller.getRecipeCount());
  }

  @Test
  void getAllRecipes_returnsDefensiveCopy() {
    controller.getAllRecipes().clear();
    assertEquals(2, controller.getRecipeCount());
  }

  // ---- Search & Filter ----

  @Test
  void findByName_exactMatch() {
    Recipe found = controller.findByName("Orange Chicken");
    assertNotNull(found);
    assertEquals("Orange Chicken", found.getName());
  }

  @Test
  void findByName_caseInsensitive() {
    Recipe found = controller.findByName("orange chicken");
    assertNotNull(found);
  }

  @Test
  void findByName_notFound_returnsNull() {
    assertNull(controller.findByName("Tofu Stir Fry"));
  }

  @Test
  void findByTag_returnsMatchingRecipes() {
    List<Recipe> quick = controller.findByTag(RecipeTag.QUICK);
    assertEquals(2, quick.size()); // both are QUICK
  }

  @Test
  void findByTag_noMatch_returnsEmptyList() {
    List<Recipe> vegan = controller.findByTag(RecipeTag.VEGAN);
    assertTrue(vegan.isEmpty());
  }

  @Test
  void findByCuisine_returnsMatchingRecipes() {
    List<Recipe> chinese = controller.findByCuisine(CuisineType.CHINESE);
    assertEquals(2, chinese.size());
  }

  @Test
  void findByCuisine_noMatch_returnsEmptyList() {
    List<Recipe> italian = controller.findByCuisine(CuisineType.ITALIAN);
    assertTrue(italian.isEmpty());
  }

  // ---- Scaling ----

  @Test
  void scaleByServings_returnsScaledRecipe() {
    Recipe scaled = controller.scaleByServings(orangeChicken, 4);

    assertEquals(4, scaled.getServings());
    assertEquals(1000, scaled.getIngredients().get(0).getQuantity()); // 500 × 2
  }

  @Test
  void scaleByServings_doesNotMutateOriginal() {
    controller.scaleByServings(orangeChicken, 10);
    assertEquals(2, orangeChicken.getServings());
  }

  @Test
  void scaleByAnchor_scalesAllIngredients() {
    Recipe scaled = controller.scaleByAnchor(orangeChicken, "chicken thigh", 750);

    assertEquals(750, scaled.getIngredients().get(0).getQuantity());
    assertEquals(150, scaled.getIngredients().get(1).getQuantity()); // 100 × 1.5
  }

  @Test
  void getAnchorPercentages_returnsCorrectPercentages() {
    Map<String, Double> pct = controller.getAnchorPercentages(orangeChicken, "chicken thigh");

    assertEquals(100.0, pct.get("chicken thigh"), 0.001);
    assertEquals(20.0, pct.get("onion"), 0.001);
  }

  // ---- Twin Recipes ----

  @Test
  void swapTwinVariant_togglesBetweenVariants() {
    Ingredient chicken = new Ingredient("chicken", 500, UnitType.GRAM, CategoryType.PROTEIN);
    List<String> steps = List.of("Cook");

    Recipe healthy =
        new Recipe(
            "Chicken H",
            "Baked",
            List.of(chicken),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY));
    Recipe guilty =
        new Recipe(
            "Chicken G",
            "Fried",
            List.of(chicken),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE));

    TwinRecipe twin = new TwinRecipe("Chicken", healthy, guilty);
    controller.addTwinRecipe(twin);

    // Default is healthy
    assertEquals("Chicken H", twin.getActiveRecipe().getName());

    // Swap to guilty
    Recipe active = controller.swapTwinVariant(twin);
    assertEquals("Chicken G", active.getName());

    // Swap back
    active = controller.swapTwinVariant(twin);
    assertEquals("Chicken H", active.getName());
  }

  @Test
  void getAllTwinRecipes_returnsDefensiveCopy() {
    Ingredient chicken = new Ingredient("chicken", 500, UnitType.GRAM, CategoryType.PROTEIN);
    List<String> steps = List.of("Cook");

    Recipe healthy =
        new Recipe(
            "H", "h", List.of(chicken), steps, 2, CuisineType.CHINESE, Set.of(RecipeTag.HEALTHY));
    Recipe guilty =
        new Recipe(
            "G",
            "g",
            List.of(chicken),
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE));

    controller.addTwinRecipe(new TwinRecipe("Test", healthy, guilty));
    controller.getAllTwinRecipes().clear();

    assertEquals(1, controller.getAllTwinRecipes().size());
  }

  // ---- Formatting ----

  @Test
  void formatRecipe_containsNameAndIngredients() {
    String result = controller.formatRecipe(orangeChicken);

    assertTrue(result.contains("Orange Chicken"));
    assertTrue(result.contains("chicken thigh"));
    assertTrue(result.contains("onion"));
    assertTrue(result.contains("Prep"));
    assertTrue(result.contains("serves 2"));
  }

  @Test
  void formatRecipe_changesWithFormatterToggle() {
    String casual = controller.formatRecipe(orangeChicken);

    controller.setFormatter(new ProfessionalFormatter(converter));
    String pro = controller.formatRecipe(orangeChicken);

    // Pro format shows "500g", casual doesn't
    assertTrue(pro.contains("500g"));
    assertNotEquals(casual, pro);
  }

  // ---- Validation ----

  @Test
  void constructor_nullScaler_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new RecipeController(null, new CasualFormatter(converter)));
  }

  @Test
  void constructor_nullFormatter_throwsException() {
    assertThrows(NullPointerException.class, () -> new RecipeController(new RecipeScaler(), null));
  }

  @Test
  void addRecipe_null_throwsException() {
    assertThrows(NullPointerException.class, () -> controller.addRecipe(null));
  }
}
