package com.smartpantry.io;

import com.smartpantry.model.Ingredient;
import com.smartpantry.model.Recipe;
import com.smartpantry.model.RecipeTag;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Writes recipes to a CSV file.
 * Format: name,description,servings,cuisineType,tags,ingredients,steps
 * Tags separated by ;, ingredients use : within and ; between, steps separated by ;
 */
public class CsvRecipeWriter {

    /**
     * Saves a list of recipes to a CSV file.
     *
     * @param recipes  the recipes to save
     * @param filePath the file path to write to
     * @throws IOException if writing fails
     */
    public void write(List<Recipe> recipes, Path filePath) throws IOException {
        Objects.requireNonNull(recipes, "Recipes cannot be null");
        Objects.requireNonNull(filePath, "File path cannot be null");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("name,description,servings,cuisineType,tags,ingredients,steps");
            writer.newLine();

            for (Recipe recipe : recipes) {
                // Tags: HEALTHY;QUICK
                String tags = recipe.getTags().stream()
                        .map(RecipeTag::name)
                        .collect(Collectors.joining(";"));

                // Ingredients: chicken thigh:500.0:GRAM:PROTEIN;onion:100.0:GRAM:VEGETABLE
                String ingredients = recipe.getIngredients().stream()
                        .map(i -> i.getName() + ":" + i.getQuantity() + ":"
                                + i.getUnitType().name() + ":" + i.getCategoryType().name())
                        .collect(Collectors.joining(";"));

                // Steps: Prep;Cook;Serve
                String steps = String.join(";", recipe.getSteps());

                String line = String.join(",",
                        recipe.getName(),
                        recipe.getDescription(),
                        String.valueOf(recipe.getServings()),
                        recipe.getCuisineType().name(),
                        tags,
                        ingredients,
                        steps
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }
}