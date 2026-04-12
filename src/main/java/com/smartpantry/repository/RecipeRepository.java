package com.smartpantry.repository;

import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.model.CuisineType;
import com.smartpantry.model.RecipeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

  List<RecipeEntity> findByNameContainingIgnoreCase(String name);

  List<RecipeEntity> findByCuisineType(CuisineType cuisineType);

  @Query("SELECT r FROM RecipeEntity r JOIN r.tags t WHERE t = :tag")
  List<RecipeEntity> findByTag(RecipeTag tag);
}
