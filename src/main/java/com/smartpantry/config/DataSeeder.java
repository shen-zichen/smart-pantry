package com.smartpantry.config;

import com.smartpantry.entity.IngredientEntity;
import com.smartpantry.entity.PantryItemEntity;
import com.smartpantry.model.CategoryType;
import com.smartpantry.model.UnitType;
import com.smartpantry.repository.IngredientRepository;
import com.smartpantry.repository.PantryItemRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

  private final IngredientRepository repository;
  private final PantryItemRepository pantryItemRepository;

  public DataSeeder(IngredientRepository repository, PantryItemRepository pantryItemRepository) {
    this.repository = repository;
    this.pantryItemRepository = pantryItemRepository;
  }

  @Override
  public void run(String... args) {
    IngredientEntity chicken =
        repository.save(
            new IngredientEntity("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN));
    IngredientEntity soy =
        repository.save(
            new IngredientEntity("Soy Sauce", 200, UnitType.MILLILITER, CategoryType.OIL));
    IngredientEntity rice =
        repository.save(
            new IngredientEntity("Jasmine Rice", 1000, UnitType.GRAM, CategoryType.GRAIN));

    pantryItemRepository.save(
        new PantryItemEntity(chicken, 500, LocalDate.now(), LocalDate.now().plusDays(5), 200));
    pantryItemRepository.save(new PantryItemEntity(soy, 200, LocalDate.now(), null, 50));
    pantryItemRepository.save(new PantryItemEntity(rice, 1000, LocalDate.now(), null, 300));

    System.out.println(
        "Seeded "
            + repository.count()
            + " ingredients, "
            + pantryItemRepository.count()
            + " pantry items");
  }
}
