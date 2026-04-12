package com.smartpantry.repository;

import com.smartpantry.entity.PantryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PantryItemRepository extends JpaRepository<PantryItemEntity, Long> {

  List<PantryItemEntity> findByIngredientNameContainingIgnoreCase(String name);
}
