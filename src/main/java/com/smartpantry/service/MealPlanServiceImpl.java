package com.smartpantry.service;

import com.smartpantry.entity.MealPlanEntity;
import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.mapper.MealPlanMapper;
import com.smartpantry.mapper.RecipeMapper;
import com.smartpantry.model.Ingredient;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;
import com.smartpantry.repository.MealPlanRepository;
import com.smartpantry.repository.RecipeRepository;
import com.smartpantry.strategy.IMealPlanStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
      throw new ResourceNotFoundException(
          "Unknown strategy: " + strategyName + ". Available: " + strategies.keySet());
    }

    // Gather current state from database
    List<PantryItem> inventory = pantryService.getAllItems();
    List<Recipe> recipes = recipeRepository.findAll().stream().map(RecipeMapper::toDomain).toList();

    // Delegate to the strategy — same pattern as Phase 0's MealPlanGenerator
    MealPlan plan = strategy.generatePlan(inventory, recipes, days); // days is now interpreted as targetServings

    // Look up existing RecipeEntity objects by ID instead of creating new transient entities.
    // The recipes returned by the strategy all came from the database and have IDs set.
    List<RecipeEntity> recipeEntities = new ArrayList<>();
    List<Double> recipeScales = new ArrayList<>();

    for (Recipe r : plan.getRecipes()) {
      if (r.getId() == null) {
        throw new ResourceNotFoundException(
            "Recipe '" + r.getName() + "' has no ID — cannot persist in meal plan");
      }
      RecipeEntity dbEntity = recipeRepository
          .findById(r.getId())
          .orElseThrow(
              () ->
                  new ResourceNotFoundException(
                      "Recipe not found: " + r.getId()));
      recipeEntities.add(dbEntity);
      
      // Compute the scale factor (scaled.servings / db.servings)
      double scale = r.getServings() / dbEntity.getServings();
      recipeScales.add(scale);
    }

    MealPlanEntity entity = MealPlanMapper.toEntity(plan, recipeEntities);
    entity.setRecipeScales(recipeScales); // Persist the scale factors
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
            .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found: " + id));
    return MealPlanMapper.toDomain(entity);
  }

  @Override
  public MealPlan markRecipeCooked(Long planId, int recipeIndex) {
    MealPlanEntity entity =
        mealPlanRepository
            .findById(planId)
            .orElseThrow(() -> new ResourceNotFoundException("Meal plan not found: " + planId));

    if (recipeIndex < 0 || recipeIndex >= entity.getRecipes().size()) {
      throw new IllegalArgumentException(
          "Invalid recipe index: " + recipeIndex + ". Plan has " + entity.getRecipes().size() + " recipes.");
    }

    if (entity.getCookedRecipeIndexes().contains(recipeIndex)) {
      // Already cooked — just return current state
      return MealPlanMapper.toDomain(entity);
    }

    // Deduct ingredients for this specific recipe
    MealPlan plan = MealPlanMapper.toDomain(entity);
    Recipe recipe = plan.getRecipes().get(recipeIndex);
    consumeOneRecipe(recipe);

    // Mark as cooked and save
    entity.addCookedIndex(recipeIndex);
    MealPlanEntity saved = mealPlanRepository.save(entity);
    return MealPlanMapper.toDomain(saved);
  }

  @Override
  public void postMealConsume(Long planId) {
    MealPlan plan = getPlanById(planId);

    for (Recipe recipe : plan.getRecipes()) {
      consumeOneRecipe(recipe);
    }
  }

  @Override
  public void deletePlan(Long planId) {
    if (!mealPlanRepository.existsById(planId)) {
      throw new ResourceNotFoundException("Meal plan not found: " + planId);
    }
    mealPlanRepository.deleteById(planId);
  }

  @Override
  public Set<String> getAvailableStrategies() {
    return strategies.keySet();
  }

  /** Deducts a single recipe's ingredients from pantry stock. */
  private void consumeOneRecipe(Recipe recipe) {
    for (Ingredient needed : recipe.getIngredients()) {
      List<PantryItem> matches = pantryService.findByExactName(needed.getName());
      if (!matches.isEmpty()) {
        PantryItem match = matches.get(0);
        pantryService.consumeItem(match.getId(), needed.getQuantity());
      }
    }
  }
}
