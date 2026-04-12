package com.smartpantry.service;

import com.smartpantry.model.GroceryItem;

import java.util.List;

/**
 * Generates grocery lists by comparing meal plan requirements against current pantry stock. The
 * deficit calculation is delegated to {@link com.smartpantry.strategy.IGroceryListGenerator}.
 */
public interface IGroceryService {

  /**
   * Produces a shopping list for a given meal plan.
   *
   * @param planId the meal plan to shop for
   * @return items that need to be purchased, with deficit quantities
   */
  List<GroceryItem> generateGroceryList(Long planId);
}
