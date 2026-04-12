package com.smartpantry.service;

import com.smartpantry.model.GroceryItem;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.strategy.IGroceryListGenerator;
import org.springframework.stereotype.Service;

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
}
