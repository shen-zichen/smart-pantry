package com.smartpantry.repository;

import com.smartpantry.entity.MealPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealPlanRepository extends JpaRepository<MealPlanEntity, Long> {}
