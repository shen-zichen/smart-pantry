package com.smartpantry.io;

import com.smartpantry.model.CategoryType;
import com.smartpantry.model.CuisineType;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.Recipe;
import com.smartpantry.model.RecipeTag;
import com.smartpantry.model.UnitType;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reads recipes from a CSV file.
 * Skips malformed rows and logs warnings instead of crashing.
 */
public class CsvRecipeReader {

    /**
     * Loads recipes from a CSV file.
     *
     * @param filePath the file path to read from
     * @return list of parsed Recipes (skips malformed rows)
     * @throws IOException if reading fails
     */
    public List<Recipe> read(Path filePath) throws IOException {
        Objects.requireNonNull(filePath, "File path cannot be null");

        List<Recipe> recipes = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String header = reader.readLine();
            if (header == null) {
                return recipes;
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    Recipe recipe = parseLine(line);
                    recipes.add(recipe);
                } catch (Exception e) {
                    System.err.println("WARNING: Skipping malformed recipe at line "
                            + lineNumber + ": " + e.getMessage());
                }
            }
        }

        return recipes;
    }

    /**
     * Parses a single CSV line into a Recipe.
     * Format: name,description,servings,cuisineType,tags,ingredients,steps
     */
    private Recipe parseLine(String line) {
        String[] parts = line.split(",", 7);  // limit to 7 — description might have commas
        if (parts.length != 7) {
            throw new IllegalArgumentException("Expected 7 fields, got " + parts.length);
        }

        String name = parts[0].trim();
        String description = parts[1].trim();
        double servings = Double.parseDouble(parts[2].trim());
        CuisineType cuisineType = CuisineType.valueOf(parts[3].trim());

        // Tags: HEALTHY;QUICK
        Set<RecipeTag> tags = Arrays.stream(parts[4].trim().split(";"))
                .map(String::trim)
                .map(RecipeTag::valueOf)
                .collect(Collectors.toSet());

        // Ingredients: chicken thigh:500.0:GRAM:PROTEIN;onion:100.0:GRAM:VEGETABLE
        List<Ingredient> ingredients = new ArrayList<>();
        for (String ingredientStr : parts[5].trim().split(";")) {
            String[] iParts = ingredientStr.trim().split(":");
            if (iParts.length != 4) {
                throw new IllegalArgumentException("Bad ingredient format: " + ingredientStr);
            }
            ingredients.add(new Ingredient(
                    iParts[0].trim(),
                    Double.parseDouble(iParts[1].trim()),
                    UnitType.valueOf(iParts[2].trim()),
                    CategoryType.valueOf(iParts[3].trim())
            ));
        }

        // Steps: Prep;Cook;Serve
        List<String> steps = Arrays.stream(parts[6].trim().split(";"))
                .map(String::trim)
                .collect(Collectors.toList());

        return new Recipe(name, description, ingredients, steps, servings, cuisineType, tags);
    }
}