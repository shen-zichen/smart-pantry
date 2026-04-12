package com.smartpantry.service;

import com.smartpantry.entity.MealPlanEntity;
import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.mapper.MealPlanMapper;
import com.smartpantry.mapper.PantryItemMapper;
import com.smartpantry.mapper.RecipeMapper;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;
import com.smartpantry.repository.MealPlanRepository;
import com.smartpantry.repository.RecipeRepository;
import com.smartpantry.strategy.IMealPlanStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Orchestrates meal plan generation using the Strategy Pattern. Spring injects all {@link
 * IMealPlanStrategy} beans into a Map keyed by bean name, allowing runtime strategy selection by
 * name.
 */
@Service
public class MealPlanServiceImpl implements IMealPlanService {

  private final Map<String, IMealPlanStrategy> strategies;
  private final MealPlanRepository mealPlanRepository;
  private final RecipeRepository recipeRepository;
  private final IPantryService pantryService;

  /**
   * @param strategies all registered strategy beans, keyed by Spring bean name
   * @param mealPlanRepository persistence for generated plans
   * @param recipeRepository access to the recipe library
   * @param pantryService access to pantry inventory and consume operations
   */
  public MealPlanServiceImpl(
      Map<String, IMealPlanStrategy> strategies,
      MealPlanRepository mealPlanRepository,
      RecipeRepository recipeRepository,
      IPantryService pantryService) {
    this.strategies = strategies;
    this.mealPlanRepository = mealPlanRepository;
    this.recipeRepository = recipeRepository;
    this.pantryService = pantryService;
  }

  @Override
  public MealPlan generatePlan(String strategyName, int days) {
    IMealPlanStrategy strategy = strategies.get(strategyName);
    if (strategy == null) {
      throw new RuntimeException(
          "Unknown strategy: " + strategyName + ". Available: " + strategies.keySet());
    }

    // Gather current state from database
    List<PantryItem> inventory = pantryService.getAllItems();
    List<Recipe> recipes = recipeRepository.findAll().stream().map(RecipeMapper::toDomain).toList();

    // Delegate to the strategy — same pattern as Phase 0's MealPlanGenerator
    MealPlan plan = strategy.generatePlan(inventory, recipes);

    // Persist the generated plan
    List<RecipeEntity> recipeEntities =
        plan.getRecipes().stream().map(RecipeMapper::toEntity).toList();
    MealPlanEntity entity = MealPlanMapper.toEntity(plan, recipeEntities);
    MealPlanEntity saved = mealPlanRepository.save(entity);

    return MealPlanMapper.toDomain(saved);
  }

  @Override
  public List<MealPlan> getAllPlans() {
    return mealPlanRepository.findAll().stream().map(MealPlanMapper::toDomain).toList();
  }

  @Override
  public MealPlan getPlanById(Long id) {
    MealPlanEntity entity =
        mealPlanRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Meal plan not found: " + id));
    return MealPlanMapper.toDomain(entity);
  }

  @Override
  public void postMealConsume(Long planId) {
    MealPlan plan = getPlanById(planId);

    for (Recipe recipe : plan.getRecipes()) {
      for (Ingredient needed : recipe.getIngredients()) {
        // Find matching pantry items and consume
        List<PantryItem> matches = pantryService.findByName(needed.getName());
        if (!matches.isEmpty()) {
          // Consume from first match — same logic as Phase 0
          PantryItem match = matches.get(0);
          // We need the entity ID to consume, so look it up
          pantryService.consumeItem(getItemId(match), needed.getQuantity());
        }
      }
    }
  }

  @Override
  public Set<String> getAvailableStrategies() {
    return strategies.keySet();
  }

  /**
   * Temporary helper — in Phase 0, PantryItem didn't have an ID. We'll clean this up when we add ID
   * to the service layer responses.
   */
  private Long getItemId(PantryItem item) {
    // For now, search by name and return the first match's entity ID
    return pantryService.findByName(item.getName()).isEmpty()
        ? null
        : null; // TODO: resolve when DTOs carry IDs
  }
}
