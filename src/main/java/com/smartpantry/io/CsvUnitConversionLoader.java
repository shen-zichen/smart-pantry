package com.smartpantry.io;

import com.smartpantry.util.UnitConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Loads ballpark piece-to-gram conversion data from a CSV file
 * and creates a {@link UnitConverter} from it.
 *
 * <p>Format: ingredientName,gramsPerPiece
 * <p>Example: chicken thigh,250
 */
public class CsvUnitConversionLoader {

    /**
     * Loads conversion data and returns a configured UnitConverter.
     *
     * @param filePath the CSV file with conversion data
     * @return a UnitConverter populated with the loaded data
     * @throws IOException if reading fails
     */
    public UnitConverter load(Path filePath) throws IOException {
        Objects.requireNonNull(filePath, "File path cannot be null");

        Map<String, Double> conversions = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String header = reader.readLine();  // skip header
            if (header == null) {
                return new UnitConverter(conversions);
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(",", 2);
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Expected 2 fields, got " + parts.length);
                    }
                    String name = parts[0].trim();
                    double grams = Double.parseDouble(parts[1].trim());

                    if (grams <= 0) {
                        throw new IllegalArgumentException("Grams must be positive: " + grams);
                    }

                    conversions.put(name, grams);
                } catch (Exception e) {
                    System.err.println("WARNING: Skipping malformed conversion at line "
                            + lineNumber + ": " + e.getMessage());
                }
            }
        }

        return new UnitConverter(conversions);
    }
}