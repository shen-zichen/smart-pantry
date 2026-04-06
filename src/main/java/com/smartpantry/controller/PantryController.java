package com.smartpantry.controller;

import com.smartpantry.model.CategoryType;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.UnitType;
import com.smartpantry.observer.InventoryManager;
import com.smartpantry.strategy.IUnitFormatter;
import com.smartpantry.util.UnitConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Orchestrates pantry operations: add, remove, consume, restock, list. Sits between the View (user
 * input) and the Model (InventoryManager).
 *
 * <p>The controller never does business logic itself — it translates user input into model
 * operations and formats model data for display.
 */
public class PantryController {

  private final InventoryManager inventoryManager;
  private final UnitConverter converter;
  private IUnitFormatter formatter; // mutable — user can toggle at runtime

  /**
   * Constructs a PantryController.
   *
   * @param inventoryManager the inventory model to orchestrate
   * @param formatter the active display formatter (casual or pro)
   * @param converter the unit converter for ballpark conversions
   */
  public PantryController(
      InventoryManager inventoryManager, IUnitFormatter formatter, UnitConverter converter) {
    Objects.requireNonNull(inventoryManager, "InventoryManager cannot be null");
    Objects.requireNonNull(formatter, "Formatter cannot be null");
    Objects.requireNonNull(converter, "UnitConverter cannot be null");

    this.inventoryManager = inventoryManager;
    this.formatter = formatter;
    this.converter = converter;
  }

  // ======== Pantry Operations ========

  /**
   * Creates a pantry item from raw user input and adds it to inventory.
   *
   * @param name ingredient name
   * @param quantity how much to add
   * @param unit the unit of measurement
   * @param category the food category
   * @param boughtDate when purchased
   * @param expirationDate when it expires (null for non-perishables)
   * @param threshold low-stock alert threshold
   */
  public void addItem(
      String name,
      double quantity,
      UnitType unit,
      CategoryType category,
      LocalDate boughtDate,
      LocalDate expirationDate,
      double threshold) {
    Ingredient ingredient = new Ingredient(name, quantity, unit, category);
    PantryItem item = new PantryItem(ingredient, quantity, boughtDate, expirationDate, threshold);
    inventoryManager.addItem(item);
  }

  /**
   * Removes an item from the pantry entirely.
   *
   * @param item the item to remove
   * @return true if the item was found and removed
   */
  public boolean removeItem(PantryItem item) {
    return inventoryManager.removeItem(item);
  }

  /**
   * Consumes a quantity of a pantry item. Automatically triggers observer alerts if stock drops
   * below threshold.
   */
  public void consumeItem(PantryItem item, double amount) {
    inventoryManager.consumeItem(item, amount);
  }

  /** Restocks a pantry item with additional quantity. */
  public void restockItem(PantryItem item, double amount) {
    inventoryManager.restockItem(item, amount);
  }

  // ======== Display ========

  /**
   * Returns a formatted string of everything in the pantry, using the active formatter (casual or
   * professional).
   */
  public String listAllItems() {
    List<PantryItem> items = inventoryManager.getAllItems();
    if (items.isEmpty()) {
      return "Your pantry is empty.";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < items.size(); i++) {
      PantryItem item = items.get(i);
      // Format the ingredient using the active formatter
      String formatted =
          formatter.format(
              new Ingredient(
                  item.getName(),
                  item.getQuantityInStock(),
                  item.getIngredient().getUnitType(),
                  item.getIngredient().getCategoryType()));

      if (i > 0) {
        sb.append("\n");
      }
      sb.append(formatted);

      // Append expiration info if present
      if (item.getExpirationDate() != null) {
        sb.append(" (expires ").append(item.getExpirationDate()).append(")");
      }
    }
    return sb.toString();
  }

  // ======== Queries ========

  /**
   * Searches pantry by ingredient name (case-insensitive). Returns all matching pantry items (there
   * may be multiple entries for the same ingredient in different units).
   */
  public List<PantryItem> findItemsByName(String name) {
    Objects.requireNonNull(name, "Name cannot be null");
    return inventoryManager.getAllItems().stream()
        .filter(item -> item.getName().equalsIgnoreCase(name))
        .collect(Collectors.toList());
  }

  /**
   * Returns the full pantry inventory. Used by other controllers (MealPlan, Grocery) that need
   * inventory data.
   */
  public List<PantryItem> getInventory() {
    return inventoryManager.getAllItems(); // already a defensive copy
  }

  // ======== Formatter Toggle ========

  /**
   * Swaps the display formatter at runtime. Casual → "2 chicken thighs" Professional → "500g
   * chicken thigh"
   */
  public void setFormatter(IUnitFormatter formatter) {
    Objects.requireNonNull(formatter, "Formatter cannot be null");
    this.formatter = formatter;
  }

  public IUnitFormatter getFormatter() {
    return formatter;
  }
}
