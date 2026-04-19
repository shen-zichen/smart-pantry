package com.smartpantry.mapper;

import com.smartpantry.entity.MealPlanEntity;
import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.model.MealPlan;
import com.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MealPlanMapper {

  public static MealPlan toDomain(MealPlanEntity entity) {
    List<Recipe> recipes = new ArrayList<>();
    
    // Scale recipes back when mapping to domain
    for (int i = 0; i < entity.getRecipes().size(); i++) {
      Recipe base = RecipeMapper.toDomain(entity.getRecipes().get(i));
      double scale = 1.0;
      if (entity.getRecipeScales() != null && i < entity.getRecipeScales().size()) {
        scale = entity.getRecipeScales().get(i);
      }
      if (scale != 1.0) {
        recipes.add(base.scale(base.getServings() * scale));
      } else {
        recipes.add(base);
      }
    }

    MealPlan plan =
        new MealPlan(entity.getStrategyName(), recipes, entity.getDays(), entity.getCreatedDate());
    plan.setId(entity.getId());
    plan.setCookedIndexes(new HashSet<>(entity.getCookedRecipeIndexes()));
    plan.setRequiresGroceryRun(entity.isRequiresGroceryRun());
    return plan;
  }

  public static MealPlanEntity toEntity(MealPlan mealPlan, List<RecipeEntity> recipeEntities) {
    MealPlanEntity entity = new MealPlanEntity(
        mealPlan.getStrategyName(), recipeEntities, mealPlan.getDays(), mealPlan.getCreatedDate());
    entity.setRequiresGroceryRun(mealPlan.isRequiresGroceryRun());
    return entity;
  }

  private MealPlanMapper() {}
}
