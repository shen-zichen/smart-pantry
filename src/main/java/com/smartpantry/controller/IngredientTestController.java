package com.smartpantry.controller;

import com.smartpantry.mapper.IngredientMapper;
import com.smartpantry.model.Ingredient;
import com.smartpantry.repository.IngredientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IngredientTestController {

  private final IngredientRepository repository;

  public IngredientTestController(IngredientRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/api/test/ingredients")
  public List<Ingredient> listAll() {
    return repository.findAll().stream().map(IngredientMapper::toDomain).toList();
  }
}
