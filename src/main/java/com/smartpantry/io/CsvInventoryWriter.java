package com.smartpantry.io;

import com.smartpantry.model.PantryItem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Writes pantry inventory to a CSV file.
 * Format: name,quantityInStock,unitType,categoryType,boughtDate,expirationDate,threshold
 */
public class CsvInventoryWriter {

    /**
     * Saves a list of pantry items to a CSV file.
     *
     * @param items    the pantry items to save
     * @param filePath the file path to write to
     * @throws IOException if writing fails
     */
    public void write(List<PantryItem> items, Path filePath) throws IOException {
        Objects.requireNonNull(items, "Items cannot be null");
        Objects.requireNonNull(filePath, "File path cannot be null");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            // Header
            writer.write("name,quantityInStock,unitType,categoryType,boughtDate,expirationDate,lowStockThreshold");
            writer.newLine();

            for (PantryItem item : items) {
                String expiration = item.getExpirationDate() != null
                        ? item.getExpirationDate().toString() : "";

                String line = String.join(",",
                        item.getName(),
                        String.valueOf(item.getQuantityInStock()),
                        item.getIngredient().getUnitType().name(),
                        item.getIngredient().getCategoryType().name(),
                        item.getBoughtDate().toString(),
                        expiration,
                        String.valueOf(item.getLowStockThreshold())
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }
}