package com.smartpantry.service;

import com.smartpantry.entity.IngredientEntity;
import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.model.*;
import com.smartpantry.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RecipeServiceImpl. Mocks the repository so we test business logic in isolation —
 * no database needed.
 */
@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

  @Mock private RecipeRepository repository;

  @InjectMocks private RecipeServiceImpl recipeService;

  private RecipeEntity chickenEntity;
  private RecipeEntity riceEntity;

  @BeforeEach
  void setUp() {
    chickenEntity =
        new RecipeEntity(
            "Orange Chicken",
            "Crispy chicken in citrus glaze",
            List.of(
                new IngredientEntity("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Cut chicken", "Fry", "Add sauce"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE));

    riceEntity =
        new RecipeEntity(
            "Fried Rice",
            "Classic egg fried rice",
            List.of(new IngredientEntity("Jasmine Rice", 300, UnitType.GRAM, CategoryType.GRAIN)),
            List.of("Cook rice", "Scramble eggs", "Stir fry"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.QUICK));
  }

  @Test
  void getAllRecipes_returnsMappedDomainObjects() {
    when(repository.findAll()).thenReturn(List.of(chickenEntity, riceEntity));

    List<Recipe> result = recipeService.getAllRecipes();

    assertEquals(2, result.size());
    assertEquals("Orange Chicken", result.get(0).getName());
    assertEquals("Fried Rice", result.get(1).getName());
    verify(repository).findAll();
  }

  @Test
  void getAllRecipes_emptyRepo_returnsEmptyList() {
    when(repository.findAll()).thenReturn(List.of());

    List<Recipe> result = recipeService.getAllRecipes();

    assertTrue(result.isEmpty());
  }

  @Test
  void getRecipeById_found_returnsDomainObject() {
    when(repository.findById(1L)).thenReturn(Optional.of(chickenEntity));

    Recipe result = recipeService.getRecipeById(1L);

    assertEquals("Orange Chicken", result.getName());
    assertEquals(CuisineType.CHINESE, result.getCuisineType());
    assertEquals(1, result.getIngredients().size());
  }

  @Test
  void getRecipeById_notFound_throwsResourceNotFoundException() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> recipeService.getRecipeById(999L));
  }

  @Test
  void addRecipe_savesAndReturnsMappedDomain() {
    when(repository.save(any(RecipeEntity.class))).thenReturn(chickenEntity);

    Recipe input =
        new Recipe(
            "Orange Chicken",
            "Crispy chicken in citrus glaze",
            List.of(new Ingredient("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Cut chicken", "Fry", "Add sauce"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE));

    Recipe result = recipeService.addRecipe(input);

    assertEquals("Orange Chicken", result.getName());
    verify(repository).save(any(RecipeEntity.class));
  }

  @Test
  void deleteRecipe_exists_deletesSuccessfully() {
    when(repository.existsById(1L)).thenReturn(true);

    recipeService.deleteRecipe(1L);

    verify(repository).deleteById(1L);
  }

  @Test
  void deleteRecipe_notFound_throwsResourceNotFoundException() {
    when(repository.existsById(999L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> recipeService.deleteRecipe(999L));
  }

  @Test
  void searchByName_returnsMatchingRecipes() {
    when(repository.findByNameContainingIgnoreCase("chicken")).thenReturn(List.of(chickenEntity));

    List<Recipe> result = recipeService.searchByName("chicken");

    assertEquals(1, result.size());
    assertEquals("Orange Chicken", result.get(0).getName());
  }

  @Test
  void filterByTag_returnsMatchingRecipes() {
    when(repository.findByTag(RecipeTag.QUICK)).thenReturn(List.of(riceEntity));

    List<Recipe> result = recipeService.filterByTag(RecipeTag.QUICK);

    assertEquals(1, result.size());
    assertEquals("Fried Rice", result.get(0).getName());
  }

  @Test
  void filterByCuisine_returnsMatchingRecipes() {
    when(repository.findByCuisineType(CuisineType.CHINESE))
        .thenReturn(List.of(chickenEntity, riceEntity));

    List<Recipe> result = recipeService.filterByCuisine(CuisineType.CHINESE);

    assertEquals(2, result.size());
  }
}
