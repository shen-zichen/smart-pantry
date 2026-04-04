package com.smartpantry.strategy;

import com.smartpantry.model.GroceryItem;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Compares a meal plan's total ingredient requirements against current
 * pantry inventory and produces a shopping list of deficits.
 *
 * <p>If the pantry fully covers an ingredient, no GroceryItem is produced.
 * If partially covered, only the deficit is listed. If not covered at all,
 * the full required quantity becomes the deficit.
 *
 * <p>Aggregates across all recipes in the plan — if two recipes each need
 * 200g chicken, the total need is 400g, not two separate entries.
 */
public class GroceryListGenerator implements IGroceryListGenerator {

    /**
     * Generates a grocery list by comparing meal plan needs vs pantry stock.
     *
     * @param mealPlan  the planned meals
     * @param inventory the current pantry contents
     * @return list of GroceryItems representing what needs to be purchased,
     *         empty if the pantry fully covers everything
     */
    @Override
    public List<GroceryItem> generateList(MealPlan mealPlan, List<PantryItem> inventory) {
        Objects.requireNonNull(mealPlan, "MealPlan cannot be null");
        Objects.requireNonNull(inventory, "Inventory cannot be null");

        // Step 1: Aggregate total needs across all recipes
        Map<String, Ingredient> totalNeeds = aggregateNeeds(mealPlan);

        // Step 2: Compare each need against pantry and calculate deficit
        List<GroceryItem> groceryList = new ArrayList<>();
        for (Ingredient needed : totalNeeds.values()) {
            double inStock = findStockQuantity(needed, inventory);
            double deficit = needed.getQuantity() - inStock;

            if (deficit > 0) {
                // Create a new Ingredient with the deficit as the quantity
                Ingredient deficitIngredient = new Ingredient(
                        needed.getName(), deficit, needed.getUnitType(), needed.getCategoryType()
                );
                groceryList.add(new GroceryItem(deficitIngredient, deficit));
            }
        }

        return groceryList;
    }

    // ---- Private helpers ----

    /**
     * Aggregates total ingredient needs across all recipes in the meal plan.
     * If two recipes each need 200g chicken, the total is 400g.
     * Uses LinkedHashMap to preserve encounter order.
     */
    private Map<String, Ingredient> aggregateNeeds(MealPlan mealPlan) {
        Map<String, Ingredient> totals = new LinkedHashMap<>();

        for (Recipe recipe : mealPlan.getRecipes()) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                String key = ingredient.getName().toLowerCase();

                if (totals.containsKey(key)) {
                    // Add to existing total
                    Ingredient existing = totals.get(key);
                    double combined = existing.getQuantity() + ingredient.getQuantity();
                    totals.put(key, new Ingredient(
                            existing.getName(), combined, existing.getUnitType(), existing.getCategoryType()
                    ));
                } else {
                    totals.put(key, ingredient);
                }
            }
        }
        return totals;
    }

    /**
     * Finds how much of an ingredient is available in the pantry.
     * Matches by Ingredient identity (name + unit + category).
     * Returns 0 if the ingredient is not in the pantry at all.
     */
    private double findStockQuantity(Ingredient needed, List<PantryItem> inventory) {
        double total = 0;
        for (PantryItem item : inventory) {
            if (item.getIngredient().equals(needed)) {
                total += item.getQuantityInStock();
            }
        }
        return total;
    }
}