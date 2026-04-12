package com.smartpantry.service;

import com.smartpantry.entity.IngredientEntity;
import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.entity.TwinRecipeEntity;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.model.*;
import com.smartpantry.repository.TwinRecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Unit tests for TwinRecipeServiceImpl. */
@ExtendWith(MockitoExtension.class)
class TwinRecipeServiceImplTest {

  @Mock private TwinRecipeRepository repository;

  @InjectMocks private TwinRecipeServiceImpl twinRecipeService;

  private TwinRecipeEntity twinEntity;

  @BeforeEach
  void setUp() {
    RecipeEntity healthy =
        new RecipeEntity(
            "Healthy Orange Chicken",
            "Baked chicken with citrus",
            List.of(
                new IngredientEntity("Chicken Breast", 400, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Bake chicken", "Add sauce"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY));

    RecipeEntity guilty =
        new RecipeEntity(
            "Guilty Orange Chicken",
            "Deep fried chicken in sweet glaze",
            List.of(
                new IngredientEntity("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Fry chicken", "Coat in glaze"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE));

    twinEntity = new TwinRecipeEntity("Orange Chicken", healthy, guilty, true);
  }

  @Test
  void getAllTwinRecipes_returnsMappedDomainObjects() {
    when(repository.findAll()).thenReturn(List.of(twinEntity));

    List<TwinRecipe> result = twinRecipeService.getAllTwinRecipes();

    assertEquals(1, result.size());
    assertEquals("Orange Chicken", result.get(0).getName());
    assertTrue(result.get(0).isHealthyActive());
  }

  @Test
  void getTwinRecipeById_found_returnsDomainObject() {
    when(repository.findById(1L)).thenReturn(Optional.of(twinEntity));

    TwinRecipe result = twinRecipeService.getTwinRecipeById(1L);

    assertEquals("Orange Chicken", result.getName());
    assertTrue(result.isHealthyActive());
  }

  @Test
  void getTwinRecipeById_notFound_throwsResourceNotFoundException() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> twinRecipeService.getTwinRecipeById(999L));
  }

  @Test
  void addTwinRecipe_savesAndReturnsMappedDomain() {
    when(repository.save(any(TwinRecipeEntity.class))).thenReturn(twinEntity);

    Recipe healthy =
        new Recipe(
            "Healthy Orange Chicken",
            "Baked chicken with citrus",
            List.of(new Ingredient("Chicken Breast", 400, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Bake chicken", "Add sauce"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY));
    Recipe guilty =
        new Recipe(
            "Guilty Orange Chicken",
            "Deep fried chicken in sweet glaze",
            List.of(new Ingredient("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Fry chicken", "Coat in glaze"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE));
    TwinRecipe input = new TwinRecipe("Orange Chicken", healthy, guilty);

    TwinRecipe result = twinRecipeService.addTwinRecipe(input);

    assertEquals("Orange Chicken", result.getName());
    verify(repository).save(any(TwinRecipeEntity.class));
  }

  @Test
  void deleteTwinRecipe_exists_deletesSuccessfully() {
    when(repository.existsById(1L)).thenReturn(true);

    twinRecipeService.deleteTwinRecipe(1L);

    verify(repository).deleteById(1L);
  }

  @Test
  void deleteTwinRecipe_notFound_throwsResourceNotFoundException() {
    when(repository.existsById(999L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> twinRecipeService.deleteTwinRecipe(999L));
  }

  @Test
  void swap_togglesActiveVariant() {
    // Starts healthy active
    assertTrue(twinEntity.isHealthyActive());

    when(repository.findById(1L)).thenReturn(Optional.of(twinEntity));
    when(repository.save(any(TwinRecipeEntity.class))).thenReturn(twinEntity);

    twinRecipeService.swap(1L);

    // After swap, entity should be guilty active
    assertFalse(twinEntity.isHealthyActive());
    verify(repository).save(twinEntity);
  }

  @Test
  void swap_notFound_throwsResourceNotFoundException() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> twinRecipeService.swap(999L));
  }
}
