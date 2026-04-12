package com.smartpantry.service;

import com.smartpantry.model.Recipe;
import com.smartpantry.model.CuisineType;
import com.smartpantry.model.RecipeTag;

import java.util.List;

public interface IRecipeService {

  List<Recipe> getAllRecipes();

  Recipe getRecipeById(Long id);

  Recipe addRecipe(Recipe recipe);

  void deleteRecipe(Long id);

  List<Recipe> searchByName(String name);

  List<Recipe> filterByTag(RecipeTag tag);

  List<Recipe> filterByCuisine(CuisineType cuisineType);
}
