package com.smartpantry.service;

import com.smartpantry.entity.IngredientEntity;
import com.smartpantry.entity.PantryItemEntity;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.model.*;
import com.smartpantry.repository.PantryItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Unit tests for PantryServiceImpl. */
@ExtendWith(MockitoExtension.class)
class PantryServiceImplTest {

  @Mock private PantryItemRepository repository;

  @InjectMocks private PantryServiceImpl pantryService;

  private IngredientEntity chickenIngredient;
  private PantryItemEntity chickenEntity;

  @BeforeEach
  void setUp() {
    chickenIngredient =
        new IngredientEntity("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    chickenEntity =
        new PantryItemEntity(
            chickenIngredient, 500, LocalDate.now(), LocalDate.now().plusDays(5), 200);
  }

  @Test
  void getAllItems_returnsMappedDomainObjects() {
    when(repository.findAll()).thenReturn(List.of(chickenEntity));

    List<PantryItem> result = pantryService.getAllItems();

    assertEquals(1, result.size());
    assertEquals("Chicken Thigh", result.get(0).getName());
    assertEquals(500, result.get(0).getQuantityInStock());
  }

  @Test
  void getItemById_found_returnsDomainObject() {
    when(repository.findById(1L)).thenReturn(Optional.of(chickenEntity));

    PantryItem result = pantryService.getItemById(1L);

    assertEquals("Chicken Thigh", result.getName());
  }

  @Test
  void getItemById_notFound_throwsResourceNotFoundException() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> pantryService.getItemById(999L));
  }

  @Test
  void addItem_savesAndReturnsMappedDomain() {
    when(repository.save(any(PantryItemEntity.class))).thenReturn(chickenEntity);

    Ingredient ingredient =
        new Ingredient("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    PantryItem input =
        new PantryItem(ingredient, 500, LocalDate.now(), LocalDate.now().plusDays(5), 200);

    PantryItem result = pantryService.addItem(input);

    assertEquals("Chicken Thigh", result.getName());
    verify(repository).save(any(PantryItemEntity.class));
  }

  @Test
  void deleteItem_exists_deletesSuccessfully() {
    when(repository.existsById(1L)).thenReturn(true);

    pantryService.deleteItem(1L);

    verify(repository).deleteById(1L);
  }

  @Test
  void deleteItem_notFound_throwsResourceNotFoundException() {
    when(repository.existsById(999L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> pantryService.deleteItem(999L));
  }

  @Test
  void consumeItem_reducesQuantity() {
    when(repository.findById(1L)).thenReturn(Optional.of(chickenEntity));
    when(repository.save(any(PantryItemEntity.class))).thenReturn(chickenEntity);

    PantryItem result = pantryService.consumeItem(1L, 200);

    // Entity was modified — verify save was called
    verify(repository).save(chickenEntity);
    // Quantity should have been reduced on the entity
    assertEquals(300, chickenEntity.getQuantityInStock());
  }

  @Test
  void consumeItem_doesNotGoBelowZero() {
    when(repository.findById(1L)).thenReturn(Optional.of(chickenEntity));
    when(repository.save(any(PantryItemEntity.class))).thenReturn(chickenEntity);

    pantryService.consumeItem(1L, 9999);

    assertEquals(0, chickenEntity.getQuantityInStock());
  }

  @Test
  void consumeItem_notFound_throwsResourceNotFoundException() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> pantryService.consumeItem(999L, 100));
  }

  @Test
  void restockItem_increasesQuantity() {
    when(repository.findById(1L)).thenReturn(Optional.of(chickenEntity));
    when(repository.save(any(PantryItemEntity.class))).thenReturn(chickenEntity);

    pantryService.restockItem(1L, 300);

    assertEquals(800, chickenEntity.getQuantityInStock());
    verify(repository).save(chickenEntity);
  }

  @Test
  void findByName_delegatesToRepository() {
    when(repository.findByIngredientNameContainingIgnoreCase("chicken"))
        .thenReturn(List.of(chickenEntity));

    List<PantryItem> result = pantryService.findByName("chicken");

    assertEquals(1, result.size());
    assertEquals("Chicken Thigh", result.get(0).getName());
  }

  @Test
  void getLowStockItems_filtersCorrectly() {
    // Chicken has 500 in stock, threshold 200 — NOT low stock
    when(repository.findAll()).thenReturn(List.of(chickenEntity));

    List<PantryItem> result = pantryService.getLowStockItems();

    assertTrue(result.isEmpty());
  }

  @Test
  void getLowStockItems_returnsLowStockItems() {
    // Set quantity below threshold
    chickenEntity.setQuantityInStock(100); // threshold is 200

    when(repository.findAll()).thenReturn(List.of(chickenEntity));

    List<PantryItem> result = pantryService.getLowStockItems();

    assertEquals(1, result.size());
  }

  @Test
  void getExpiringSoonItems_filtersCorrectly() {
    // Chicken expires in 5 days, checking with 3-day window — NOT expiring soon
    when(repository.findAll()).thenReturn(List.of(chickenEntity));

    List<PantryItem> result = pantryService.getExpiringSoonItems(3);

    assertTrue(result.isEmpty());
  }

  @Test
  void getExpiringSoonItems_returnsExpiringSoonItems() {
    // Chicken expires in 5 days, checking with 7-day window — IS expiring soon
    when(repository.findAll()).thenReturn(List.of(chickenEntity));

    List<PantryItem> result = pantryService.getExpiringSoonItems(7);

    assertEquals(1, result.size());
  }
}
