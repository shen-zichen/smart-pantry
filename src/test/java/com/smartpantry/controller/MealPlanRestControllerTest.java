package com.smartpantry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartpantry.dto.GenerateMealPlanRequest;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.model.*;
import com.smartpantry.service.IMealPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Integration test for MealPlanRestController. */
@WebMvcTest(MealPlanRestController.class)
class MealPlanRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private IMealPlanService mealPlanService;

  private MealPlan testPlan;

  @BeforeEach
  void setUp() {
    Recipe recipe =
        new Recipe(
            "Orange Chicken",
            "Crispy chicken in citrus glaze",
            List.of(new Ingredient("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN)),
            List.of("Cut chicken", "Fry", "Add sauce"),
            2.0,
            CuisineType.CHINESE,
            Set.of(RecipeTag.GUILTY_PLEASURE));
    recipe.setId(1L);

    testPlan = new MealPlan("zeroWaste", List.of(recipe), 3, LocalDate.of(2026, 4, 12));
    testPlan.setId(1L);
  }

  @Test
  void generate_validRequest_returns201() throws Exception {
    when(mealPlanService.generatePlan("zeroWaste", 3)).thenReturn(testPlan);

    GenerateMealPlanRequest request = new GenerateMealPlanRequest();
    request.setStrategyName("zeroWaste");
    request.setDays(3);

    mockMvc
        .perform(
            post("/api/meal-plans/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.strategyName").value("zeroWaste"))
        .andExpect(jsonPath("$.days").value(3))
        .andExpect(jsonPath("$.recipes[0].name").value("Orange Chicken"));
  }

  @Test
  void generate_missingStrategy_returns400() throws Exception {
    GenerateMealPlanRequest request = new GenerateMealPlanRequest();
    request.setDays(3);

    mockMvc
        .perform(
            post("/api/meal-plans/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  void listAll_returnsJsonArray() throws Exception {
    when(mealPlanService.getAllPlans()).thenReturn(List.of(testPlan));

    mockMvc
        .perform(get("/api/meal-plans"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].strategyName").value("zeroWaste"))
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  void getById_found_returnsPlan() throws Exception {
    when(mealPlanService.getPlanById(1L)).thenReturn(testPlan);

    mockMvc
        .perform(get("/api/meal-plans/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.strategyName").value("zeroWaste"))
        .andExpect(jsonPath("$.recipes").isArray());
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(mealPlanService.getPlanById(999L))
        .thenThrow(new ResourceNotFoundException("Meal plan not found: 999"));

    mockMvc
        .perform(get("/api/meal-plans/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Not Found"));
  }

  @Test
  void getStrategies_returnsAvailableNames() throws Exception {
    when(mealPlanService.getAvailableStrategies())
        .thenReturn(Set.of("zeroWaste", "pantryFirst", "strict"));

    mockMvc
        .perform(get("/api/meal-plans/strategies"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }
}
