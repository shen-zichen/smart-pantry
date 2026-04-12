package com.smartpantry.mapper;

import com.smartpantry.entity.MealPlanEntity;
import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.Recipe;

import java.util.List;

public class MealPlanMapper {

  public static MealPlan toDomain(MealPlanEntity entity) {
    List<Recipe> recipes = entity.getRecipes().stream().map(RecipeMapper::toDomain).toList();

    MealPlan plan =
        new MealPlan(entity.getStrategyName(), recipes, entity.getDays(), entity.getCreatedDate());
    plan.setId(entity.getId());
    return plan;
  }

  public static MealPlanEntity toEntity(MealPlan mealPlan, List<RecipeEntity> recipeEntities) {
    return new MealPlanEntity(
        mealPlan.getStrategyName(), recipeEntities, mealPlan.getDays(), mealPlan.getCreatedDate());
  }

  private MealPlanMapper() {}
}
