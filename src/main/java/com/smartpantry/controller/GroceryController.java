package com.smartpantry.controller;

import com.smartpantry.model.GroceryItem;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.strategy.IGroceryListGenerator;
import com.smartpantry.strategy.IUnitFormatter;

import java.util.List;
import java.util.Objects;

/**
 * Orchestrates grocery list generation: compares meal plan needs against pantry inventory and
 * produces a formatted shopping list.
 *
 * <p>Delegates deficit calculation to {@link IGroceryListGenerator} and formatting to the active
 * {@link IUnitFormatter}.
 */
public class GroceryController {

  private final IGroceryListGenerator groceryGenerator;
  private IUnitFormatter formatter;

  /**
   * Constructs a GroceryController.
   *
   * @param groceryGenerator the generator that calculates deficits
   * @param formatter the active display formatter
   */
  public GroceryController(IGroceryListGenerator groceryGenerator, IUnitFormatter formatter) {
    Objects.requireNonNull(groceryGenerator, "GroceryListGenerator cannot be null");
    Objects.requireNonNull(formatter, "Formatter cannot be null");

    this.groceryGenerator = groceryGenerator;
    this.formatter = formatter;
  }

  // ======== Grocery List Generation ========

  /**
   * Generates a grocery list for a meal plan given current inventory.
   *
   * @param mealPlan the planned meals
   * @param inventory the current pantry contents
   * @return list of GroceryItems representing what needs to be purchased
   */
  public List<GroceryItem> generateGroceryList(MealPlan mealPlan, List<PantryItem> inventory) {
    Objects.requireNonNull(mealPlan, "MealPlan cannot be null");
    Objects.requireNonNull(inventory, "Inventory cannot be null");

    return groceryGenerator.generateList(mealPlan, inventory);
  }

  // ======== Display ========

  /**
   * Formats a grocery list for display using the active formatter. Casual: "Buy 2 onions, 1 can
   * black beans" Pro: "Buy 300g onion, 400g black beans"
   */
  public String formatGroceryList(List<GroceryItem> groceryList) {
    Objects.requireNonNull(groceryList, "Grocery list cannot be null");

    if (groceryList.isEmpty()) {
      return "Your pantry has everything you need — no shopping required!";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Shopping List:\n");

    for (int i = 0; i < groceryList.size(); i++) {
      GroceryItem item = groceryList.get(i);
      // Create an Ingredient with the deficit quantity for formatting
      Ingredient display =
          new Ingredient(
              item.getName(),
              item.getDeficit(),
              item.getUnitType(),
              item.getIngredient().getCategoryType());

      sb.append("  ").append(i + 1).append(". Buy ").append(formatter.format(display)).append("\n");
    }

    return sb.toString();
  }

  /** Convenience method: generates and formats a grocery list in one call. */
  public String generateAndFormat(MealPlan mealPlan, List<PantryItem> inventory) {
    List<GroceryItem> list = generateGroceryList(mealPlan, inventory);
    return formatGroceryList(list);
  }

  // ======== Formatter Toggle ========

  public void setFormatter(IUnitFormatter formatter) {
    Objects.requireNonNull(formatter, "Formatter cannot be null");
    this.formatter = formatter;
  }

  public IUnitFormatter getFormatter() {
    return formatter;
  }
}
