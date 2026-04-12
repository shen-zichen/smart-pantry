package com.smartpantry.service;

import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.mapper.RecipeMapper;
import com.smartpantry.model.CuisineType;
import com.smartpantry.model.Recipe;
import com.smartpantry.model.RecipeTag;
import com.smartpantry.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServiceImpl implements IRecipeService {

  private final RecipeRepository repository;

  public RecipeServiceImpl(RecipeRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<Recipe> getAllRecipes() {
    return repository.findAll().stream().map(RecipeMapper::toDomain).toList();
  }

  @Override
  public Recipe getRecipeById(Long id) {
    RecipeEntity entity =
        repository.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found: " + id));
    return RecipeMapper.toDomain(entity);
  }

  @Override
  public Recipe addRecipe(Recipe recipe) {
    RecipeEntity entity = RecipeMapper.toEntity(recipe);
    RecipeEntity saved = repository.save(entity);
    return RecipeMapper.toDomain(saved);
  }

  @Override
  public void deleteRecipe(Long id) {
    if (!repository.existsById(id)) {
      throw new RuntimeException("Recipe not found: " + id);
    }
    repository.deleteById(id);
  }

  @Override
  public List<Recipe> searchByName(String name) {
    return repository.findByNameContainingIgnoreCase(name).stream()
        .map(RecipeMapper::toDomain)
        .toList();
  }

  @Override
  public List<Recipe> filterByTag(RecipeTag tag) {
    return repository.findByTag(tag).stream().map(RecipeMapper::toDomain).toList();
  }

  @Override
  public List<Recipe> filterByCuisine(CuisineType cuisineType) {
    return repository.findByCuisineType(cuisineType).stream().map(RecipeMapper::toDomain).toList();
  }
}
