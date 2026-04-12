package com.smartpantry.service;

import com.smartpantry.model.TwinRecipe;

import java.util.List;

public interface ITwinRecipeService {

  List<TwinRecipe> getAllTwinRecipes();

  TwinRecipe getTwinRecipeById(Long id);

  TwinRecipe addTwinRecipe(TwinRecipe twinRecipe);

  void deleteTwinRecipe(Long id);

  TwinRecipe swap(Long id);
}
