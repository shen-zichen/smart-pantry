package com.smartpantry.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Recipe}. Covers: construction, validation, defensive copies, equals/hashCode,
 * tags, toString.
 */
class RecipeTest {

  private Ingredient chicken;
  private Ingredient onion;
  private List<Ingredient> ingredients;
  private List<String> steps;
  private Set<RecipeTag> tags;

  @BeforeEach
  void setUp() {
    chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    onion = new Ingredient("onion", 1, UnitType.PIECE, CategoryType.VEGETABLE);
    ingredients = new ArrayList<>(List.of(chicken, onion));
    steps = new ArrayList<>(List.of("Chop onion", "Cook chicken", "Combine and serve"));
    tags = new HashSet<>(Set.of(RecipeTag.QUICK, RecipeTag.HEALTHY));
  }

  private Recipe makeRecipe() {
    return new Recipe(
        "Orange Chicken",
        "Crispy chicken in citrus glaze",
        ingredients,
        steps,
        2,
        CuisineType.CHINESE,
        tags);
  }

  // ---- Construction & Getters ----

  @Test
  void constructor_validInputs_storesAllFields() {
    Recipe r = makeRecipe();

    assertEquals("Orange Chicken", r.getName());
    assertEquals("Crispy chicken in citrus glaze", r.getDescription());
    assertEquals(2, r.getIngredients().size());
    assertEquals(3, r.getSteps().size());
    assertEquals(2, r.getServings());
    assertEquals(CuisineType.CHINESE, r.getCuisineType());
    assertEquals(2, r.getTags().size());
  }

  // ---- Validation ----

  @Test
  void constructor_nullName_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new Recipe(null, "desc", ingredients, steps, 2, CuisineType.CHINESE, tags));
  }

  @Test
  void constructor_blankName_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Recipe("  ", "desc", ingredients, steps, 2, CuisineType.CHINESE, tags));
  }

  @Test
  void constructor_nullIngredients_throwsException() {
    assertThrows(
        NullPointerException.class,
        () -> new Recipe("Test", "desc", null, steps, 2, CuisineType.CHINESE, tags));
  }

  @Test
  void constructor_emptyIngredients_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Recipe("Test", "desc", List.of(), steps, 2, CuisineType.CHINESE, tags));
  }

  @Test
  void constructor_emptySteps_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Recipe("Test", "desc", ingredients, List.of(), 2, CuisineType.CHINESE, tags));
  }

  @Test
  void constructor_zeroServings_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Recipe("Test", "desc", ingredients, steps, 0, CuisineType.CHINESE, tags));
  }

  @Test
  void constructor_negativeServings_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Recipe("Test", "desc", ingredients, steps, -1, CuisineType.CHINESE, tags));
  }

  // ---- Defensive Copies ----

  @Test
  void constructor_defensiveCopy_callerCannotMutateIngredients() {
    Recipe r = makeRecipe();
    ingredients.clear(); // mutate the original list
    assertEquals(2, r.getIngredients().size()); // recipe is unaffected
  }

  @Test
  void getIngredients_returnsCopy_cannotMutateInternal() {
    Recipe r = makeRecipe();
    List<Ingredient> returned = r.getIngredients();
    returned.clear(); // mutate the returned list
    assertEquals(2, r.getIngredients().size()); // recipe is unaffected
  }

  @Test
  void constructor_defensiveCopy_callerCannotMutateSteps() {
    Recipe r = makeRecipe();
    steps.clear();
    assertEquals(3, r.getSteps().size());
  }

  @Test
  void constructor_defensiveCopy_callerCannotMutateTags() {
    Recipe r = makeRecipe();
    tags.clear();
    assertEquals(2, r.getTags().size());
  }

  // ---- hasTag ----

  @Test
  void hasTag_presentTag_returnsTrue() {
    Recipe r = makeRecipe();
    assertTrue(r.hasTag(RecipeTag.QUICK));
    assertTrue(r.hasTag(RecipeTag.HEALTHY));
  }

  @Test
  void hasTag_absentTag_returnsFalse() {
    Recipe r = makeRecipe();
    assertFalse(r.hasTag(RecipeTag.SPICY));
    assertFalse(r.hasTag(RecipeTag.VEGAN));
  }

  // ---- Equals & HashCode ----

  @Test
  void equals_sameNameAndIngredients_isEqual() {
    Recipe a = makeRecipe();
    Recipe b =
        new Recipe(
            "Orange Chicken",
            "Different description",
            List.of(chicken, onion),
            List.of("Step 1"),
            4,
            CuisineType.CHINESE,
            Set.of(RecipeTag.SPICY));
    assertEquals(a, b); // same name + same ingredients = equal
  }

  @Test
  void equals_differentName_notEqual() {
    Recipe a = makeRecipe();
    Recipe b =
        new Recipe(
            "Lemon Chicken",
            "Crispy chicken in citrus glaze",
            ingredients,
            steps,
            2,
            CuisineType.CHINESE,
            tags);
    assertNotEquals(a, b);
  }

  @Test
  void equals_differentIngredients_notEqual() {
    Recipe a = makeRecipe();
    Ingredient rice = new Ingredient("rice", 300, UnitType.GRAM, CategoryType.GRAIN);
    Recipe b =
        new Recipe(
            "Orange Chicken",
            "Crispy chicken in citrus glaze",
            List.of(chicken, rice),
            steps,
            2,
            CuisineType.CHINESE,
            tags);
    assertNotEquals(a, b);
  }

  @Test
  void equals_null_notEqual() {
    assertNotEquals(null, makeRecipe());
  }

  @Test
  void equals_sameReference_isEqual() {
    Recipe r = makeRecipe();
    assertEquals(r, r);
  }

  @Test
  void hashCode_equalObjects_sameHash() {
    Recipe a = makeRecipe();
    Recipe b =
        new Recipe(
            "Orange Chicken",
            "Whatever",
            List.of(chicken, onion),
            List.of("Do stuff"),
            10,
            CuisineType.ITALIAN,
            Set.of(RecipeTag.VEGAN));
    assertEquals(a.hashCode(), b.hashCode());
  }

  // ---- toString ----

  @Test
  void toString_containsNameAndServings() {
    Recipe r = makeRecipe();
    String result = r.toString();
    assertTrue(result.contains("Orange Chicken"));
    assertTrue(result.contains("2"));
  }
}
