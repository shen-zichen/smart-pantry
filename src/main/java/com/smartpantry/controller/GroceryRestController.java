package com.smartpantry.controller;

import com.smartpantry.dto.BoughtItemRequest;
import com.smartpantry.dto.GroceryItemResponse;
import com.smartpantry.model.GroceryItem;
import com.smartpantry.service.IGroceryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for grocery list generation and purchase tracking. */
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

  /** POST /api/grocery/bought — mark items as purchased and restock pantry. */
  @PostMapping("/bought")
  @ResponseStatus(HttpStatus.OK)
  public void markBought(@Valid @RequestBody List<BoughtItemRequest> items) {
    groceryService.markBought(items);
  }
}
