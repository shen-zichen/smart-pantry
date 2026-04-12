package com.smartpantry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartpantry.dto.CreateRecipeRequest;
import com.smartpantry.dto.CreateTwinRecipeRequest;
import com.smartpantry.dto.IngredientDto;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.model.*;
import com.smartpantry.service.ITwinRecipeService;
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

/** Integration test for TwinRecipeRestController. */
@WebMvcTest(TwinRecipeRestController.class)
class TwinRecipeRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ITwinRecipeService twinRecipeService;

  private TwinRecipe orangeChickenTwin;

  @BeforeEach
  void setUp() {
    Recipe healthy =
        new Recipe(
            "Healthy Orange Chicken",
            "Baked chicken with citrus",
            List.of(new Ingredient("Chicken Breast", 400, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Bake chicken", "Add sauce"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.HEALTHY));
    healthy.setId(1L);

    Recipe guilty =
        new Recipe(
            "Guilty Orange Chicken",
            "Deep fried in sweet glaze",
            List.of(new Ingredient("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Fry chicken", "Coat in glaze"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE));
    guilty.setId(2L);

    orangeChickenTwin = new TwinRecipe("Orange Chicken", healthy, guilty);
    orangeChickenTwin.setId(1L);
  }

  @Test
  void listAll_returnsJsonArray() throws Exception {
    when(twinRecipeService.getAllTwinRecipes()).thenReturn(List.of(orangeChickenTwin));

    mockMvc
        .perform(get("/api/twin-recipes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Orange Chicken"))
        .andExpect(jsonPath("$[0].healthyActive").value(true))
        .andExpect(jsonPath("$[0].healthyVariant.name").value("Healthy Orange Chicken"))
        .andExpect(jsonPath("$[0].guiltyVariant.name").value("Guilty Orange Chicken"));
  }

  @Test
  void getById_found_returnsTwinRecipe() throws Exception {
    when(twinRecipeService.getTwinRecipeById(1L)).thenReturn(orangeChickenTwin);

    mockMvc
        .perform(get("/api/twin-recipes/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Orange Chicken"))
        .andExpect(jsonPath("$.healthyActive").value(true));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(twinRecipeService.getTwinRecipeById(999L))
        .thenThrow(new ResourceNotFoundException("Twin recipe not found: 999"));

    mockMvc
        .perform(get("/api/twin-recipes/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Not Found"));
  }

  @Test
  void create_validRequest_returns201() throws Exception {
    when(twinRecipeService.addTwinRecipe(any(TwinRecipe.class))).thenReturn(orangeChickenTwin);

    CreateRecipeRequest healthyReq = new CreateRecipeRequest();
    healthyReq.setName("Healthy Orange Chicken");
    healthyReq.setDescription("Baked chicken with citrus");
    healthyReq.setIngredients(
        List.of(new IngredientDto("Chicken Breast", 400, UnitType.GRAM, CategoryType.PROTEIN)));
    healthyReq.setSteps(List.of("Bake chicken", "Add sauce"));
    healthyReq.setServings(2.0);
    healthyReq.setCuisineType(CuisineType.CHINESE);
    healthyReq.setTags(Set.of(RecipeTag.HEALTHY));

    CreateRecipeRequest guiltyReq = new CreateRecipeRequest();
    guiltyReq.setName("Guilty Orange Chicken");
    guiltyReq.setDescription("Deep fried in sweet glaze");
    guiltyReq.setIngredients(
        List.of(new IngredientDto("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN)));
    guiltyReq.setSteps(List.of("Fry chicken", "Coat in glaze"));
    guiltyReq.setServings(2.0);
    guiltyReq.setCuisineType(CuisineType.CHINESE);
    guiltyReq.setTags(Set.of(RecipeTag.GUILTY_PLEASURE));

    CreateTwinRecipeRequest request = new CreateTwinRecipeRequest();
    request.setName("Orange Chicken");
    request.setHealthyVariant(healthyReq);
    request.setGuiltyVariant(guiltyReq);

    mockMvc
        .perform(
            post("/api/twin-recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Orange Chicken"));
  }

  @Test
  void swap_togglesVariant() throws Exception {
    orangeChickenTwin.swap(); // now guilty active
    when(twinRecipeService.swap(1L)).thenReturn(orangeChickenTwin);

    mockMvc
        .perform(put("/api/twin-recipes/1/swap"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.healthyActive").value(false));
  }

  @Test
  void delete_returns204() throws Exception {
    mockMvc.perform(delete("/api/twin-recipes/1")).andExpect(status().isNoContent());
  }
}
