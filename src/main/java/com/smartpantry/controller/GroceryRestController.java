package com.smartpantry.controller;

import com.smartpantry.dto.GroceryItemResponse;
import com.smartpantry.model.GroceryItem;
import com.smartpantry.service.IGroceryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoint for grocery list generation. */
@RestController
@RequestMapping("/api/grocery")
public class GroceryRestController {

  private final IGroceryService groceryService;

  public GroceryRestController(IGroceryService groceryService) {
    this.groceryService = groceryService;
  }

  /** GET /api/grocery?planId=1 — generate a grocery list for a meal plan. */
  @GetMapping
  public List<GroceryItemResponse> getGroceryList(@RequestParam Long planId) {
    return groceryService.generateGroceryList(planId).stream()
        .map(
            item ->
                new GroceryItemResponse(
                    item.getName(),
                    item.getDeficit(),
                    item.getUnitType().name(),
                    item.getIngredient().getCategoryType().name()))
        .toList();
  }
}
