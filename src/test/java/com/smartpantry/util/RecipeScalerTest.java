package com.smartpantry.util;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RecipeScaler}. Covers: serving-based scaling, anchor-based scaling, percentage
 * display, edge cases.
 */
class RecipeScalerTest {

  private RecipeScaler scaler;
  private Recipe orangeChicken;
  private Ingredient chicken;
  private Ingredient onion;
  private Ingredient garlic;

  @BeforeEach
  void setUp() {
    scaler = new RecipeScaler();

    chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    onion = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
    garlic = new Ingredient("garlic", 10, UnitType.GRAM, CategoryType.SPICE);

    orangeChicken =
        new Recipe(
            "Orange Chicken",
            "Crispy chicken in citrus glaze",
            List.of(chicken, onion, garlic),
            List.of("Prep", "Cook", "Serve"),
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY, RecipeTag.QUICK));
  }

  // ======== scaleByServings ========

  @Test
  void scaleByServings_double_doublesAllQuantities() {
    Recipe scaled = scaler.scaleByServings(orangeChicken, 4);

    assertEquals(4, scaled.getServings());
    List<Ingredient> ingredients = scaled.getIngredients();
    assertEquals(1000, ingredients.get(0).getQuantity()); // chicken: 500 × 2
    assertEquals(200, ingredients.get(1).getQuantity()); // onion: 100 × 2
    assertEquals(20, ingredients.get(2).getQuantity()); // garlic: 10 × 2
  }

  @Test
  void scaleByServings_half_halvesAllQuantities() {
    Recipe scaled = scaler.scaleByServings(orangeChicken, 1);

    assertEquals(1, scaled.getServings());
    List<Ingredient> ingredients = scaled.getIngredients();
    assertEquals(250, ingredients.get(0).getQuantity()); // chicken: 500 × 0.5
    assertEquals(50, ingredients.get(1).getQuantity()); // onion: 100 × 0.5
    assertEquals(5, ingredients.get(2).getQuantity()); // garlic: 10 × 0.5
  }

  @Test
  void scaleByServings_fractional_scalesCorrectly() {
    Recipe scaled = scaler.scaleByServings(orangeChicken, 1.5);

    assertEquals(1.5, scaled.getServings());
    List<Ingredient> ingredients = scaled.getIngredients();
    assertEquals(375, ingredients.get(0).getQuantity()); // chicken: 500 × 0.75
  }

  @Test
  void scaleByServings_preservesMetadata() {
    Recipe scaled = scaler.scaleByServings(orangeChicken, 4);

    assertEquals("Orange Chicken", scaled.getName());
    assertEquals("Crispy chicken in citrus glaze", scaled.getDescription());
    assertEquals(CuisineType.CHINESE, scaled.getCuisineType());
    assertTrue(scaled.hasTag(RecipeTag.HEALTHY));
    assertEquals(3, scaled.getSteps().size());
  }

  @Test
  void scaleByServings_doesNotMutateOriginal() {
    scaler.scaleByServings(orangeChicken, 10);

    // Original recipe unchanged
    assertEquals(2, orangeChicken.getServings());
    assertEquals(500, orangeChicken.getIngredients().get(0).getQuantity());
  }

  @Test
  void scaleByServings_nullRecipe_throwsException() {
    assertThrows(NullPointerException.class, () -> scaler.scaleByServings(null, 4));
  }

  @Test
  void scaleByServings_zeroTarget_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> scaler.scaleByServings(orangeChicken, 0));
  }

  @Test
  void scaleByServings_negativeTarget_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> scaler.scaleByServings(orangeChicken, -2));
  }

  // ======== scaleByAnchor ========

  @Test
  void scaleByAnchor_scaleUp_allIngredientsFollow() {
    // Anchor: chicken 500g → 750g (ratio 1.5)
    Recipe scaled = scaler.scaleByAnchor(orangeChicken, "chicken thigh", 750);

    List<Ingredient> ingredients = scaled.getIngredients();
    assertEquals(750, ingredients.get(0).getQuantity()); // chicken: 500 × 1.5
    assertEquals(150, ingredients.get(1).getQuantity()); // onion: 100 × 1.5
    assertEquals(15, ingredients.get(2).getQuantity()); // garlic: 10 × 1.5
  }

  @Test
  void scaleByAnchor_scaleDown_allIngredientsFollow() {
    // Anchor: chicken 500g → 250g (ratio 0.5)
    Recipe scaled = scaler.scaleByAnchor(orangeChicken, "chicken thigh", 250);

    List<Ingredient> ingredients = scaled.getIngredients();
    assertEquals(250, ingredients.get(0).getQuantity());
    assertEquals(50, ingredients.get(1).getQuantity());
    assertEquals(5, ingredients.get(2).getQuantity());
  }

  @Test
  void scaleByAnchor_servingsScaleWithRatio() {
    // Original: 2 servings, ratio 1.5 → 3 servings
    Recipe scaled = scaler.scaleByAnchor(orangeChicken, "chicken thigh", 750);
    assertEquals(3, scaled.getServings(), 0.001);
  }

  @Test
  void scaleByAnchor_caseInsensitive() {
    // "Chicken Thigh" should match "chicken thigh"
    Recipe scaled = scaler.scaleByAnchor(orangeChicken, "Chicken Thigh", 750);
    assertEquals(750, scaled.getIngredients().get(0).getQuantity());
  }

  @Test
  void scaleByAnchor_unknownIngredient_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> scaler.scaleByAnchor(orangeChicken, "dragon fruit", 500));
  }

  @Test
  void scaleByAnchor_nullRecipe_throwsException() {
    assertThrows(
        NullPointerException.class, () -> scaler.scaleByAnchor(null, "chicken thigh", 500));
  }

  @Test
  void scaleByAnchor_negativeQuantity_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> scaler.scaleByAnchor(orangeChicken, "chicken thigh", -100));
  }

  @Test
  void scaleByAnchor_doesNotMutateOriginal() {
    scaler.scaleByAnchor(orangeChicken, "chicken thigh", 1000);

    assertEquals(500, orangeChicken.getIngredients().get(0).getQuantity());
    assertEquals(2, orangeChicken.getServings());
  }

  // ======== getAnchorPercentages ========

  @Test
  void getAnchorPercentages_anchorIs100Percent() {
    Map<String, Double> percentages = scaler.getAnchorPercentages(orangeChicken, "chicken thigh");

    assertEquals(100.0, percentages.get("chicken thigh"), 0.001);
  }

  @Test
  void getAnchorPercentages_otherIngredientsScaleCorrectly() {
    // chicken 500g (100%), onion 100g (20%), garlic 10g (2%)
    Map<String, Double> percentages = scaler.getAnchorPercentages(orangeChicken, "chicken thigh");

    assertEquals(100.0, percentages.get("chicken thigh"), 0.001);
    assertEquals(20.0, percentages.get("onion"), 0.001);
    assertEquals(2.0, percentages.get("garlic"), 0.001);
  }

  @Test
  void getAnchorPercentages_nonMainIngredientAsAnchor() {
    // Anchor on garlic (10g = 100%) → chicken becomes 5000%, onion becomes 1000%
    Map<String, Double> percentages = scaler.getAnchorPercentages(orangeChicken, "garlic");

    assertEquals(5000.0, percentages.get("chicken thigh"), 0.001);
    assertEquals(1000.0, percentages.get("onion"), 0.001);
    assertEquals(100.0, percentages.get("garlic"), 0.001);
  }

  @Test
  void getAnchorPercentages_unknownAnchor_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> scaler.getAnchorPercentages(orangeChicken, "dragon fruit"));
  }

  @Test
  void getAnchorPercentages_preservesOrder() {
    Map<String, Double> percentages = scaler.getAnchorPercentages(orangeChicken, "chicken thigh");

    // LinkedHashMap should preserve recipe ingredient order
    List<String> keys = List.copyOf(percentages.keySet());
    assertEquals("chicken thigh", keys.get(0));
    assertEquals("onion", keys.get(1));
    assertEquals("garlic", keys.get(2));
  }
}
