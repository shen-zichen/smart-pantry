package com.smartpantry.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TwinRecipe}. Covers: construction, tag validation, swap behavior, state queries,
 * toString.
 */
class TwinRecipeTest {

  private Recipe healthyChicken;
  private Recipe guiltyChicken;

  @BeforeEach
  void setUp() {
    Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    Ingredient onion = new Ingredient("onion", 1, UnitType.PIECE, CategoryType.VEGETABLE);
    List<Ingredient> ingredients = List.of(chicken, onion);
    List<String> steps = List.of("Prep", "Cook", "Serve");

    healthyChicken =
        new Recipe(
            "Orange Chicken Healthy",
            "Baked, light sauce",
            ingredients,
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY, RecipeTag.QUICK));

    guiltyChicken =
        new Recipe(
            "Orange Chicken Guilty",
            "Deep fried, extra glaze",
            ingredients,
            steps,
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE, RecipeTag.QUICK));
  }

  private TwinRecipe makeTwin() {
    return new TwinRecipe("Orange Chicken", healthyChicken, guiltyChicken);
  }

  // ---- Construction ----

  @Test
  void constructor_validInputs_defaultsToHealthy() {
    TwinRecipe twin = makeTwin();

    assertEquals("Orange Chicken", twin.getName());
    assertSame(healthyChicken, twin.getActiveRecipe());
    assertTrue(twin.isHealthyActive());
  }

  @Test
  void constructor_storesBothVariants() {
    TwinRecipe twin = makeTwin();

    assertSame(healthyChicken, twin.getHealthyVariant());
    assertSame(guiltyChicken, twin.getGuiltyVariant());
  }

  // ---- Validation ----

  @Test
  void constructor_nullName_throwsException() {
    assertThrows(
        NullPointerException.class, () -> new TwinRecipe(null, healthyChicken, guiltyChicken));
  }

  @Test
  void constructor_blankName_throwsException() {
    assertThrows(
        IllegalArgumentException.class, () -> new TwinRecipe("  ", healthyChicken, guiltyChicken));
  }

  @Test
  void constructor_nullHealthyVariant_throwsException() {
    assertThrows(NullPointerException.class, () -> new TwinRecipe("Test", null, guiltyChicken));
  }

  @Test
  void constructor_nullGuiltyVariant_throwsException() {
    assertThrows(NullPointerException.class, () -> new TwinRecipe("Test", healthyChicken, null));
  }

  @Test
  void constructor_healthyVariantMissingTag_throwsException() {
    // A recipe without the HEALTHY tag can't be the healthy variant
    Recipe noTag =
        new Recipe(
            "Bad",
            "No tag",
            List.of(new Ingredient("rice", 200, UnitType.GRAM, CategoryType.GRAIN)),
            List.of("Cook"),
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.QUICK));

    assertThrows(
        IllegalArgumentException.class, () -> new TwinRecipe("Test", noTag, guiltyChicken));
  }

  @Test
  void constructor_guiltyVariantMissingTag_throwsException() {
    Recipe noTag =
        new Recipe(
            "Bad",
            "No tag",
            List.of(new Ingredient("rice", 200, UnitType.GRAM, CategoryType.GRAIN)),
            List.of("Cook"),
            2,
            CuisineType.CHINESE,
            Set.of(RecipeTag.QUICK));

    assertThrows(
        IllegalArgumentException.class, () -> new TwinRecipe("Test", healthyChicken, noTag));
  }

  // ---- Swap ----

  @Test
  void swap_fromHealthy_switchesToGuilty() {
    TwinRecipe twin = makeTwin();

    twin.swap();

    assertSame(guiltyChicken, twin.getActiveRecipe());
    assertFalse(twin.isHealthyActive());
  }

  @Test
  void swap_twice_returnsToHealthy() {
    TwinRecipe twin = makeTwin();

    twin.swap();
    twin.swap();

    assertSame(healthyChicken, twin.getActiveRecipe());
    assertTrue(twin.isHealthyActive());
  }

  @Test
  void swap_thrice_endsOnGuilty() {
    TwinRecipe twin = makeTwin();

    twin.swap();
    twin.swap();
    twin.swap();

    assertSame(guiltyChicken, twin.getActiveRecipe());
  }

  // ---- toString ----

  @Test
  void toString_healthy_showsHealthyMode() {
    TwinRecipe twin = makeTwin();
    assertTrue(twin.toString().contains("Healthy"));
  }

  @Test
  void toString_afterSwap_showsGuiltyMode() {
    TwinRecipe twin = makeTwin();
    twin.swap();
    assertTrue(twin.toString().contains("Guilty"));
  }
}
