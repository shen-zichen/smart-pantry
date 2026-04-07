package com.smartpantry.config;

import com.smartpantry.entity.IngredientEntity;
import com.smartpantry.model.CategoryType;
import com.smartpantry.model.UnitType;
import com.smartpantry.repository.IngredientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

  private final IngredientRepository repository;

  public DataSeeder(IngredientRepository repository) {
    this.repository = repository;
  }

  @Override
  public void run(String... args) {
    repository.save(
        new IngredientEntity("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN));
    repository.save(
        new IngredientEntity("Soy Sauce", 200, UnitType.MILLILITER, CategoryType.OIL));
    repository.save(new IngredientEntity("Jasmine Rice", 1000, UnitType.GRAM, CategoryType.GRAIN));
    System.out.println("Seeded " + repository.count() + " ingredients");
  }
}
