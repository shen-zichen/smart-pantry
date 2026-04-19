package com.smartpantry.dto;

import com.smartpantry.model.MealPlan;

import java.util.List;

/** Converts between domain MealPlan and its DTOs. */
public class MealPlanDtoMapper {

  public static MealPlanResponse toResponse(MealPlan plan) {
    List<RecipeResponse> recipeDtos =
        plan.getRecipes().stream().map(RecipeDtoMapper::toResponse).toList();

    return new MealPlanResponse(
        plan.getId(), plan.getStrategyName(), recipeDtos, plan.getDays(), plan.getCreatedDate(),
        plan.getCookedIndexes(), plan.isRequiresGroceryRun());
  }

  private MealPlanDtoMapper() {}
}
