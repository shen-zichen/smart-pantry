package com.smartpantry.io;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;
import com.smartpantry.util.UnitConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for CSV I/O classes.
 * Uses JUnit 5 TempDir for file isolation — files are auto-deleted after each test.
 */
class CsvIoTest {

    @TempDir
    Path tempDir;

    private CsvInventoryWriter inventoryWriter;
    private CsvInventoryReader inventoryReader;
    private CsvRecipeWriter recipeWriter;
    private CsvRecipeReader recipeReader;
    private CsvUnitConversionLoader conversionLoader;

    @BeforeEach
    void setUp() {
        inventoryWriter = new CsvInventoryWriter();
        inventoryReader = new CsvInventoryReader();
        recipeWriter = new CsvRecipeWriter();
        recipeReader = new CsvRecipeReader();
        conversionLoader = new CsvUnitConversionLoader();
    }

    // ---- Inventory round-trip ----

    @Test
    void inventory_writeAndRead_roundTrip() throws IOException {
        Ingredient chicken = new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
        Ingredient salt = new Ingredient("salt", 0, UnitType.GRAM, CategoryType.SPICE);

        PantryItem chickenItem = new PantryItem(chicken, 500, LocalDate.of(2026, 3, 30),
                LocalDate.of(2026, 4, 5), 100);
        PantryItem saltItem = new PantryItem(salt, 1000, LocalDate.of(2026, 1, 1),
                null, 50);  // no expiration

        Path file = tempDir.resolve("inventory.csv");
        inventoryWriter.write(List.of(chickenItem, saltItem), file);
        List<PantryItem> loaded = inventoryReader.read(file);

        assertEquals(2, loaded.size());
        assertEquals("chicken thigh", loaded.get(0).getName());
        assertEquals(500, loaded.get(0).getQuantityInStock());
        assertEquals(LocalDate.of(2026, 4, 5), loaded.get(0).getExpirationDate());
        assertEquals("salt", loaded.get(1).getName());
        assertNull(loaded.get(1).getExpirationDate());
    }

    // ---- Recipe round-trip ----

    @Test
    void recipe_writeAndRead_roundTrip() throws IOException {
        Ingredient chicken = new Ingredient("chicken thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
        Ingredient onion = new Ingredient("onion", 100, UnitType.GRAM, CategoryType.VEGETABLE);

        Recipe recipe = new Recipe("Orange Chicken", "Crispy citrus chicken",
                List.of(chicken, onion),
                List.of("Prep", "Cook", "Serve"),
                2, CuisineType.CHINESE,
                Set.of(RecipeTag.HEALTHY, RecipeTag.QUICK));

        Path file = tempDir.resolve("recipes.csv");
        recipeWriter.write(List.of(recipe), file);
        List<Recipe> loaded = recipeReader.read(file);

        assertEquals(1, loaded.size());
        Recipe r = loaded.get(0);
        assertEquals("Orange Chicken", r.getName());
        assertEquals(2, r.getServings());
        assertEquals(CuisineType.CHINESE, r.getCuisineType());
        assertEquals(2, r.getIngredients().size());
        assertEquals(3, r.getSteps().size());
        assertTrue(r.hasTag(RecipeTag.HEALTHY));
        assertTrue(r.hasTag(RecipeTag.QUICK));
    }

    // ---- Unit conversion loader ----

    @Test
    void conversionLoader_loadsDataIntoConverter() throws IOException {
        Path file = tempDir.resolve("conversions.csv");
        Files.writeString(file,
                "ingredientName,gramsPerPiece\n"
                        + "chicken thigh,250\n"
                        + "onion,150\n"
                        + "egg,50\n");

        UnitConverter converter = conversionLoader.load(file);

        assertTrue(converter.hasBallpark("chicken thigh"));
        assertTrue(converter.hasBallpark("onion"));
        assertTrue(converter.hasBallpark("egg"));
        assertEquals(500, converter.piecesToGrams("chicken thigh", 2));
    }

    // ---- Error handling ----

    @Test
    void inventoryReader_malformedRow_skipsAndContinues() throws IOException {
        Path file = tempDir.resolve("bad_inventory.csv");
        Files.writeString(file,
                "name,quantityInStock,unitType,categoryType,boughtDate,expirationDate,lowStockThreshold\n"
                        + "chicken thigh,500,GRAM,PROTEIN,2026-03-30,2026-04-05,100\n"
                        + "THIS,IS,BAD\n"
                        + "salt,1000,GRAM,SPICE,2026-01-01,,50\n");

        List<PantryItem> loaded = inventoryReader.read(file);

        assertEquals(2, loaded.size());  // bad row skipped, good rows kept
    }

    @Test
    void recipeReader_malformedRow_skipsAndContinues() throws IOException {
        Path file = tempDir.resolve("bad_recipes.csv");
        Files.writeString(file,
                "name,description,servings,cuisineType,tags,ingredients,steps\n"
                        + "BAD ROW\n"
                        + "Fried Rice,Classic rice,2,CHINESE,QUICK,rice:300:GRAM:GRAIN,Cook;Serve\n");

        List<Recipe> loaded = recipeReader.read(file);

        assertEquals(1, loaded.size());
        assertEquals("Fried Rice", loaded.get(0).getName());
    }

    @Test
    void conversionLoader_malformedRow_skipsAndContinues() throws IOException {
        Path file = tempDir.resolve("bad_conversions.csv");
        Files.writeString(file,
                "ingredientName,gramsPerPiece\n"
                        + "chicken thigh,250\n"
                        + "BAD ROW\n"
                        + "onion,150\n");

        UnitConverter converter = conversionLoader.load(file);

        assertTrue(converter.hasBallpark("chicken thigh"));
        assertTrue(converter.hasBallpark("onion"));
    }

    // ---- Empty files ----

    @Test
    void inventoryReader_emptyFile_returnsEmptyList() throws IOException {
        Path file = tempDir.resolve("empty.csv");
        Files.writeString(file, "");

        List<PantryItem> loaded = inventoryReader.read(file);
        assertTrue(loaded.isEmpty());
    }

    @Test
    void recipeReader_headerOnly_returnsEmptyList() throws IOException {
        Path file = tempDir.resolve("header_only.csv");
        Files.writeString(file, "name,description,servings,cuisineType,tags,ingredients,steps\n");

        List<Recipe> loaded = recipeReader.read(file);
        assertTrue(loaded.isEmpty());
    }
}