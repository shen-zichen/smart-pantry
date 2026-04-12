package com.smartpantry.repository;

import com.smartpantry.entity.TwinRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwinRecipeRepository extends JpaRepository<TwinRecipeEntity, Long> {}
