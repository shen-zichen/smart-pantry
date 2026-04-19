package com.smartpantry.controller;

import com.smartpantry.dto.GenerateMealPlanRequest;
import com.smartpantry.dto.MealPlanDtoMapper;
import com.smartpantry.dto.MealPlanResponse;
import com.smartpantry.model.MealPlan;
import com.smartpantry.service.IMealPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/** REST endpoints for meal plan generation and retrieval. */
@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanRestController {

  private final IMealPlanService mealPlanService;

  public MealPlanRestController(IMealPlanService mealPlanService) {
    this.mealPlanService = mealPlanService;
  }

  /** POST /api/meal-plans/generate — generate a new meal plan. */
  @PostMapping("/generate")
  @ResponseStatus(HttpStatus.CREATED)
  public MealPlanResponse generate(@Valid @RequestBody GenerateMealPlanRequest request) {
    MealPlan plan = mealPlanService.generatePlan(request.getStrategyName(), request.getDays());
    return MealPlanDtoMapper.toResponse(plan);
  }

  /** GET /api/meal-plans — list all generated plans. */
  @GetMapping
  public List<MealPlanResponse> listAll() {
    return mealPlanService.getAllPlans().stream().map(MealPlanDtoMapper::toResponse).toList();
  }

  /** GET /api/meal-plans/{id} — get a specific plan. */
  @GetMapping("/{id}")
  public MealPlanResponse getById(@PathVariable Long id) {
    MealPlan plan = mealPlanService.getPlanById(id);
    return MealPlanDtoMapper.toResponse(plan);
  }

  /** POST /api/meal-plans/{id}/cook/{recipeIndex} — mark one recipe as cooked and deduct from pantry. */
  @PostMapping("/{id}/cook/{recipeIndex}")
  public MealPlanResponse markCooked(@PathVariable Long id, @PathVariable int recipeIndex) {
    MealPlan plan = mealPlanService.markRecipeCooked(id, recipeIndex);
    return MealPlanDtoMapper.toResponse(plan);
  }

  /** POST /api/meal-plans/{id}/consume — deduct ALL ingredients from pantry (legacy). */
  @PostMapping("/{id}/consume")
  public void postMealConsume(@PathVariable Long id) {
    mealPlanService.postMealConsume(id);
  }

  /** DELETE /api/meal-plans/{id} — delete a meal plan. */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePlan(@PathVariable Long id) {
    mealPlanService.deletePlan(id);
  }

  /** GET /api/meal-plans/strategies — list available strategy names. */
  @GetMapping("/strategies")
  public Set<String> getStrategies() {
    return mealPlanService.getAvailableStrategies();
  }
}
