package com.smartpantry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartpantry.dto.CreatePantryItemRequest;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.model.*;
import com.smartpantry.service.IPantryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Integration test for PantryRestController. Loads only the web layer — service is mocked. */
@WebMvcTest(PantryRestController.class)
class PantryRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private IPantryService pantryService;

  private PantryItem chickenItem;

  @BeforeEach
  void setUp() {
    Ingredient chicken = new Ingredient("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    chickenItem =
        new PantryItem(chicken, 500, LocalDate.of(2026, 4, 11), LocalDate.of(2026, 4, 16), 200);
    chickenItem.setId(1L);
  }

  @Test
  void listAll_returnsJsonArray() throws Exception {
    when(pantryService.getAllItems()).thenReturn(List.of(chickenItem));

    mockMvc
        .perform(get("/api/pantry"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].ingredientName").value("Chicken Thigh"))
        .andExpect(jsonPath("$[0].quantityInStock").value(500))
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  void getById_found_returnsItem() throws Exception {
    when(pantryService.getItemById(1L)).thenReturn(chickenItem);

    mockMvc
        .perform(get("/api/pantry/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ingredientName").value("Chicken Thigh"))
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(pantryService.getItemById(999L))
        .thenThrow(new ResourceNotFoundException("Pantry item not found: 999"));

    mockMvc
        .perform(get("/api/pantry/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Not Found"));
  }

  @Test
  void addItem_validRequest_returns201() throws Exception {
    when(pantryService.addItem(any(PantryItem.class))).thenReturn(chickenItem);

    CreatePantryItemRequest request = new CreatePantryItemRequest();
    request.setName("Chicken Thigh");
    request.setQuantity(500);
    request.setUnitType(UnitType.GRAM);
    request.setCategoryType(CategoryType.PROTEIN);
    request.setBoughtDate(LocalDate.of(2026, 4, 11));
    request.setLowStockThreshold(200);

    mockMvc
        .perform(
            post("/api/pantry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.ingredientName").value("Chicken Thigh"));
  }

  @Test
  void addItem_missingName_returns400() throws Exception {
    CreatePantryItemRequest request = new CreatePantryItemRequest();
    request.setQuantity(500);
    request.setUnitType(UnitType.GRAM);
    request.setCategoryType(CategoryType.PROTEIN);
    request.setBoughtDate(LocalDate.of(2026, 4, 11));

    mockMvc
        .perform(
            post("/api/pantry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  void consume_returnsUpdatedItem() throws Exception {
    chickenItem.consume(200); // now 300
    when(pantryService.consumeItem(1L, 200)).thenReturn(chickenItem);

    mockMvc
        .perform(put("/api/pantry/1/consume?amount=200"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantityInStock").value(300));
  }

  @Test
  void restock_returnsUpdatedItem() throws Exception {
    chickenItem.restock(300); // now 800
    when(pantryService.restockItem(1L, 300)).thenReturn(chickenItem);

    mockMvc
        .perform(put("/api/pantry/1/restock?amount=300"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantityInStock").value(800));
  }

  @Test
  void delete_returns204() throws Exception {
    doNothing().when(pantryService).deleteItem(1L);

    mockMvc.perform(delete("/api/pantry/1")).andExpect(status().isNoContent());
  }

  @Test
  void searchByName_returnsMatchingItems() throws Exception {
    when(pantryService.findByName("chicken")).thenReturn(List.of(chickenItem));

    mockMvc
        .perform(get("/api/pantry/search?name=chicken"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].ingredientName").value("Chicken Thigh"));
  }
}
