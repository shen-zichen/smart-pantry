package com.smartpantry.service;

import com.smartpantry.dto.BoughtItemRequest;
import com.smartpantry.model.CategoryType;
import com.smartpantry.model.GroceryItem;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.UnitType;
import com.smartpantry.strategy.IGroceryListGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Gathers inventory and meal plan data, then delegates deficit calculation to the injected {@link
 * IGroceryListGenerator} strategy.
 */
@Service
public class GroceryServiceImpl implements IGroceryService {

  private final IGroceryListGenerator groceryGenerator;
  private final IMealPlanService mealPlanService;
  private final IPantryService pantryService;

  public GroceryServiceImpl(
      IGroceryListGenerator groceryGenerator,
      IMealPlanService mealPlanService,
      IPantryService pantryService) {
    this.groceryGenerator = groceryGenerator;
    this.mealPlanService = mealPlanService;
    this.pantryService = pantryService;
  }

  @Override
  public List<GroceryItem> generateGroceryList(Long planId) {
    MealPlan plan = mealPlanService.getPlanById(planId);
    List<PantryItem> inventory = pantryService.getAllItems();
    return groceryGenerator.generateList(plan, inventory);
  }

  @Override
  public void markBought(List<BoughtItemRequest> items) {
    for (BoughtItemRequest item : items) {
      // Try to find existing pantry item by exact name
      List<PantryItem> matches = pantryService.findByExactName(item.getName());
      if (!matches.isEmpty()) {
        // Restock the first match
        PantryItem existing = matches.get(0);
        pantryService.restockItem(existing.getId(), item.getQuantity());
      } else {
        // Create a new pantry item
        UnitType unitType = UnitType.valueOf(item.getUnitType());
        CategoryType categoryType = CategoryType.valueOf(item.getCategoryType());
        Ingredient ingredient =
            new Ingredient(item.getName(), item.getQuantity(), unitType, categoryType);
        PantryItem newItem =
            new PantryItem(ingredient, item.getQuantity(), LocalDate.now(), null, 0);
        pantryService.addItem(newItem);
      }
    }
  }
}
