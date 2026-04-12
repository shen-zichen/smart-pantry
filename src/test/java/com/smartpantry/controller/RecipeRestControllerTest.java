package com.smartpantry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartpantry.dto.CreateRecipeRequest;
import com.smartpantry.dto.IngredientDto;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.model.*;
import com.smartpantry.service.IRecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Integration test for RecipeRestController. */
@WebMvcTest(RecipeRestController.class)
class RecipeRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private IRecipeService recipeService;

  private Recipe orangeChicken;

  @BeforeEach
  void setUp() {
    orangeChicken =
        new Recipe(
            "Orange Chicken",
            "Crispy chicken in citrus glaze",
            List.of(new Ingredient("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Cut chicken", "Fry", "Add sauce"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE));
    orangeChicken.setId(1L);
  }

  @Test
  void listAll_returnsJsonArray() throws Exception {
    when(recipeService.getAllRecipes()).thenReturn(List.of(orangeChicken));

    mockMvc
        .perform(get("/api/recipes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Orange Chicken"))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].cuisineType").value("CHINESE"));
  }

  @Test
  void getById_found_returnsRecipe() throws Exception {
    when(recipeService.getRecipeById(1L)).thenReturn(orangeChicken);

    mockMvc
        .perform(get("/api/recipes/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Orange Chicken"))
        .andExpect(jsonPath("$.ingredients[0].name").value("Chicken Thigh"))
        .andExpect(jsonPath("$.steps[0]").value("Cut chicken"));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(recipeService.getRecipeById(999L))
        .thenThrow(new ResourceNotFoundException("Recipe not found: 999"));

    mockMvc
        .perform(get("/api/recipes/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Not Found"));
  }

  @Test
  void addRecipe_validRequest_returns201() throws Exception {
    when(recipeService.addRecipe(any(Recipe.class))).thenReturn(orangeChicken);

    CreateRecipeRequest request = new CreateRecipeRequest();
    request.setName("Orange Chicken");
    request.setDescription("Crispy chicken in citrus glaze");
    request.setIngredients(
        List.of(new IngredientDto("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN)));
    request.setSteps(List.of("Cut chicken", "Fry", "Add sauce"));
    request.setServings(2.0);
    request.setCuisineType(CuisineType.CHINESE);
    request.setTags(Set.of(RecipeTag.GUILTY_PLEASURE));

    mockMvc
        .perform(
            post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Orange Chicken"));
  }

  @Test
  void addRecipe_missingName_returns400() throws Exception {
    CreateRecipeRequest request = new CreateRecipeRequest();
    request.setDescription("Some description");
    request.setIngredients(
        List.of(new IngredientDto("Chicken", 500, UnitType.GRAM, CategoryType.PROTEIN)));
    request.setSteps(List.of("Cook"));
    request.setServings(2.0);
    request.setCuisineType(CuisineType.CHINESE);
    request.setTags(Set.of(RecipeTag.QUICK));

    mockMvc
        .perform(
            post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  void deleteRecipe_returns204() throws Exception {
    mockMvc.perform(delete("/api/recipes/1")).andExpect(status().isNoContent());
  }

  @Test
  void searchByName_returnsMatches() throws Exception {
    when(recipeService.searchByName("chicken")).thenReturn(List.of(orangeChicken));

    mockMvc
        .perform(get("/api/recipes/search?name=chicken"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Orange Chicken"));
  }

  @Test
  void filterByTag_returnsMatches() throws Exception {
    when(recipeService.filterByTag(RecipeTag.GUILTY_PLEASURE)).thenReturn(List.of(orangeChicken));

    mockMvc
        .perform(get("/api/recipes/filter/tag?tag=GUILTY_PLEASURE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Orange Chicken"));
  }

  @Test
  void filterByCuisine_returnsMatches() throws Exception {
    when(recipeService.filterByCuisine(CuisineType.CHINESE)).thenReturn(List.of(orangeChicken));

    mockMvc
        .perform(get("/api/recipes/filter/cuisine?cuisine=CHINESE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Orange Chicken"));
  }
}
