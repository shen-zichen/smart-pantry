package com.smartpantry.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GroceryListGenerator}.
 * Covers: full coverage, partial coverage, no coverage, aggregation, empty cases.
 */
class GroceryListGeneratorTest {

    private GroceryListGenerator generator;

    private Ingredient chickenId;
    private Ingredient onionId;
    private Ingredient riceId;
    private Ingredient garlicId;

    private PantryItem chickenItem;
    private PantryItem onionItem;

    private Recipe orangeChicken;
    private Recipe friedRice;

    @BeforeEach
    void setUp() {
        generator = new GroceryListGenerator();

        chickenId = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
        onionId = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);
        riceId = new Ingredient("rice", 300, UnitType.GRAM, CategoryType.GRAIN);
        garlicId = new Ingredient("garlic", 10, UnitType.GRAM, CategoryType.SPICE);

        Ingredient chickenPantry = new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
        chickenItem = new PantryItem(chickenPantry, 600, LocalDate.now(),
                LocalDate.now().plusDays(5), 100);

        Ingredient onionPantry = new Ingredient("onion", 0, UnitType.GRAM, CategoryType.VEGETABLE);
        onionItem = new PantryItem(onionPantry, 200, LocalDate.now(),
                LocalDate.now().plusDays(7), 50);

        List<String> steps = List.of("Prep", "Cook", "Serve");

        orangeChicken = new Recipe("Orange Chicken", "Citrus chicken",
                List.of(chickenId, onionId), steps, 2, CuisineType.CHINESE,
                Set.of(RecipeTag.HEALTHY));

        friedRice = new Recipe("Fried Rice", "Classic rice",
                List.of(riceId, garlicId), steps, 2, CuisineType.CHINESE,
                Set.of(RecipeTag.BUDGET_FRIENDLY));
    }

    private MealPlan makePlan(List<Recipe> recipes) {
        return new MealPlan("Test", recipes, recipes.size(), LocalDate.now());
    }

    // ---- Full coverage — no shopping needed ----

    @Test
    void generateList_allInStock_returnsEmptyList() {
        // Pantry has 600g chicken (need 500) and 200g onion (need 100) — covered
        MealPlan plan = makePlan(List.of(orangeChicken));
        List<PantryItem> inventory = List.of(chickenItem, onionItem);

        List<GroceryItem> result = generator.generateList(plan, inventory);

        assertTrue(result.isEmpty());
    }

    // ---- Partial coverage — deficit calculated ----

    @Test
    void generateList_partialStock_returnsDeficit() {
        // Pantry has 200g chicken (need 500) — deficit is 300g
        Ingredient lowChickenPantry = new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
        PantryItem lowChicken = new PantryItem(lowChickenPantry, 200, LocalDate.now(),
                LocalDate.now().plusDays(5), 50);

        MealPlan plan = makePlan(List.of(orangeChicken));
        List<PantryItem> inventory = List.of(lowChicken, onionItem);

        List<GroceryItem> result = generator.generateList(plan, inventory);

        assertEquals(1, result.size());
        assertEquals("chicken thigh", result.get(0).getName());
        assertEquals(300, result.get(0).getDeficit(), 0.01);
    }

    // ---- No coverage — full amount needed ----

    @Test
    void generateList_notInStock_returnsFullAmount() {
        // Pantry has no rice or garlic
        MealPlan plan = makePlan(List.of(friedRice));
        List<PantryItem> inventory = List.of(chickenItem, onionItem);

        List<GroceryItem> result = generator.generateList(plan, inventory);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(g -> g.getName().equals("rice") && g.getDeficit() == 300));
        assertTrue(result.stream().anyMatch(g -> g.getName().equals("garlic") && g.getDeficit() == 10));
    }

    // ---- Aggregation across recipes ----

    @Test
    void generateList_multipleRecipes_aggregatesNeeds() {
        // Two recipes both need chicken: 500g + another recipe needing chicken
        Ingredient moreChicken = new Ingredient("chicken thigh", 400, UnitType.GRAM, CategoryType.PROTEIN);
        Recipe chickenSoup = new Recipe("Chicken Soup", "Warm soup",
                List.of(moreChicken), List.of("Cook"), 2, CuisineType.AMERICAN,
                Set.of(RecipeTag.HEALTHY));

        // Total chicken needed: 500 + 400 = 900g, pantry has 600g → deficit 300g
        MealPlan plan = makePlan(List.of(orangeChicken, chickenSoup));
        List<PantryItem> inventory = List.of(chickenItem, onionItem);

        List<GroceryItem> result = generator.generateList(plan, inventory);

        GroceryItem chickenDeficit = result.stream()
                .filter(g -> g.getName().equals("chicken thigh"))
                .findFirst()
                .orElse(null);

        assertNotNull(chickenDeficit);
        assertEquals(300, chickenDeficit.getDeficit(), 0.01);
    }

    // ---- Empty inventory ----

    @Test
    void generateList_emptyInventory_returnsAllIngredients() {
        MealPlan plan = makePlan(List.of(orangeChicken));
        List<PantryItem> inventory = List.of();

        List<GroceryItem> result = generator.generateList(plan, inventory);

        assertEquals(2, result.size());  // chicken + onion both needed
    }

    // ---- Validation ----

    @Test
    void generateList_nullMealPlan_throwsException() {
        assertThrows(NullPointerException.class,
                () -> generator.generateList(null, List.of()));
    }

    @Test
    void generateList_nullInventory_throwsException() {
        MealPlan plan = makePlan(List.of(orangeChicken));
        assertThrows(NullPointerException.class,
                () -> generator.generateList(plan, null));
    }
}