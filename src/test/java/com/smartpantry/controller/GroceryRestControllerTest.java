package com.smartpantry.controller;

import com.smartpantry.model.*;
import com.smartpantry.service.IGroceryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Integration test for GroceryRestController. */
@WebMvcTest(GroceryRestController.class)
class GroceryRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private IGroceryService groceryService;

  @Test
  void getGroceryList_returnsDeficitItems() throws Exception {
    Ingredient chicken = new Ingredient("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    GroceryItem item = new GroceryItem(chicken, 300);

    when(groceryService.generateGroceryList(1L)).thenReturn(List.of(item));

    mockMvc
        .perform(get("/api/grocery?planId=1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].ingredientName").value("Chicken Thigh"))
        .andExpect(jsonPath("$[0].deficit").value(300));
  }

  @Test
  void getGroceryList_nothingNeeded_returnsEmptyArray() throws Exception {
    when(groceryService.generateGroceryList(1L)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/grocery?planId=1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void getGroceryList_missingPlanId_returns400() throws Exception {
    mockMvc.perform(get("/api/grocery")).andExpect(status().isBadRequest());
  }
}
