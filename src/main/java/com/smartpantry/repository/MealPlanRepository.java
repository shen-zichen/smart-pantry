package com.smartpantry.repository;

import com.smartpantry.entity.MealPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MealPlanRepository extends JpaRepository<MealPlanEntity, Long> {
  List<MealPlanEntity> findByRecipes_Id(Long recipeId);
}
