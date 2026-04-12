package com.smartpantry.service;

import com.smartpantry.entity.TwinRecipeEntity;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.mapper.TwinRecipeMapper;
import com.smartpantry.model.TwinRecipe;
import com.smartpantry.repository.TwinRecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwinRecipeServiceImpl implements ITwinRecipeService {

  private final TwinRecipeRepository repository;

  public TwinRecipeServiceImpl(TwinRecipeRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<TwinRecipe> getAllTwinRecipes() {
    return repository.findAll().stream().map(TwinRecipeMapper::toDomain).toList();
  }

  @Override
  public TwinRecipe getTwinRecipeById(Long id) {
    TwinRecipeEntity entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Twin recipe not found: " + id));
    return TwinRecipeMapper.toDomain(entity);
  }

  @Override
  public TwinRecipe addTwinRecipe(TwinRecipe twinRecipe) {
    TwinRecipeEntity entity = TwinRecipeMapper.toEntity(twinRecipe);
    TwinRecipeEntity saved = repository.save(entity);
    return TwinRecipeMapper.toDomain(saved);
  }

  @Override
  public void deleteTwinRecipe(Long id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Twin recipe not found: " + id);
    }
    repository.deleteById(id);
  }

  @Override
  public TwinRecipe swap(Long id) {
    TwinRecipeEntity entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Twin recipe not found: " + id));

    entity.setHealthyActive(!entity.isHealthyActive());
    TwinRecipeEntity saved = repository.save(entity);
    return TwinRecipeMapper.toDomain(saved);
  }
}
