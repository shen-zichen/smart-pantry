package com.smartpantry.io;

import com.smartpantry.model.CategoryType;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.UnitType;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reads pantry inventory from a CSV file.
 * Skips malformed rows and logs warnings instead of crashing.
 */
public class CsvInventoryReader {

    /**
     * Loads pantry items from a CSV file.
     *
     * @param filePath the file path to read from
     * @return list of parsed PantryItems (skips malformed rows)
     * @throws IOException if reading fails
     */
    public List<PantryItem> read(Path filePath) throws IOException {
        Objects.requireNonNull(filePath, "File path cannot be null");

        List<PantryItem> items = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String header = reader.readLine();  // skip header
            if (header == null) {
                return items;
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    PantryItem item = parseLine(line);
                    items.add(item);
                } catch (Exception e) {
                    System.err.println("WARNING: Skipping malformed row at line "
                            + lineNumber + ": " + e.getMessage());
                }
            }
        }

        return items;
    }

    /**
     * Parses a single CSV line into a PantryItem.
     * Format: name,quantityInStock,unitType,categoryType,boughtDate,expirationDate,threshold
     */
    private PantryItem parseLine(String line) {
        String[] parts = line.split(",", -1);  // -1 keeps trailing empty strings
        if (parts.length != 7) {
            throw new IllegalArgumentException("Expected 7 fields, got " + parts.length);
        }

        String name = parts[0].trim();
        double quantity = Double.parseDouble(parts[1].trim());
        UnitType unitType = UnitType.valueOf(parts[2].trim());
        CategoryType categoryType = CategoryType.valueOf(parts[3].trim());
        LocalDate boughtDate = LocalDate.parse(parts[4].trim());
        LocalDate expirationDate = parts[5].trim().isEmpty()
                ? null : LocalDate.parse(parts[5].trim());
        double threshold = Double.parseDouble(parts[6].trim());

        Ingredient ingredient = new Ingredient(name, 0, unitType, categoryType);
        return new PantryItem(ingredient, quantity, boughtDate, expirationDate, threshold);
    }
}