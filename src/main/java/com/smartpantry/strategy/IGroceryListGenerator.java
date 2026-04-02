package com.smartpantry.strategy;

import com.smartpantry.model.GroceryItem;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;

import java.util.List;

/**
 * Generates a shopping list by comparing what a meal plan needs against what's currently in the
 * pantry. Any deficit becomes a GroceryItem.
 *
 * <p>Example: recipe needs 500g chicken, pantry has 200g → GroceryItem("chicken", 300g). If the
 * pantry fully covers a recipe, no GroceryItem is produced for it.
 *
 * <p>The active IUnitFormatter determines how the grocery list is displayed (casual: "buy 2 onions"
 * vs pro: "buy 300g onion"), but that's the View's concern — this interface just calculates the raw
 * deficits.
 */
public interface IGroceryListGenerator {

  /**
   * Compares a meal plan's ingredient requirements against current inventory and returns a list of
   * items that need to be purchased.
   *
   * @param mealPlan the planned meals whose ingredients to check
   * @param inventory the current pantry contents
   * @return a list of GroceryItems representing deficits, empty if fully stocked
   */
  List<GroceryItem> generateList(MealPlan mealPlan, List<PantryItem> inventory);
}
