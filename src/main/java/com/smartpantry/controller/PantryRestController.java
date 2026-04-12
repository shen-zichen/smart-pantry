package com.smartpantry.controller;

import com.smartpantry.dto.CreatePantryItemRequest;
import com.smartpantry.dto.PantryItemDtoMapper;
import com.smartpantry.dto.PantryItemResponse;
import com.smartpantry.model.PantryItem;
import com.smartpantry.service.IPantryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for pantry management. Thin layer — validates input, delegates to service, maps
 * output to DTOs.
 */
@RestController
@RequestMapping("/api/pantry")
public class PantryRestController {

  private final IPantryService pantryService;

  public PantryRestController(IPantryService pantryService) {
    this.pantryService = pantryService;
  }

  @GetMapping
  public List<PantryItemResponse> listAll() {
    return pantryService.getAllItems().stream().map(PantryItemDtoMapper::toResponse).toList();
  }

  @GetMapping("/{id}")
  public PantryItemResponse getById(@PathVariable Long id) {
    PantryItem item = pantryService.getItemById(id);
    return PantryItemDtoMapper.toResponse(item);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PantryItemResponse addItem(@Valid @RequestBody CreatePantryItemRequest request) {
    PantryItem item = PantryItemDtoMapper.fromRequest(request);
    PantryItem saved = pantryService.addItem(item);
    return PantryItemDtoMapper.toResponse(saved);
  }

  @PutMapping("/{id}/consume")
  public PantryItemResponse consume(@PathVariable Long id, @RequestParam double amount) {
    PantryItem updated = pantryService.consumeItem(id, amount);
    return PantryItemDtoMapper.toResponse(updated);
  }

  @PutMapping("/{id}/restock")
  public PantryItemResponse restock(@PathVariable Long id, @RequestParam double amount) {
    PantryItem updated = pantryService.restockItem(id, amount);
    return PantryItemDtoMapper.toResponse(updated);
  }

  /** DELETE /api/pantry/{id} — remove a pantry item. */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    pantryService.deleteItem(id);
  }

  @GetMapping("/search")
  public List<PantryItemResponse> searchByName(@RequestParam String name) {
    return pantryService.findByName(name).stream().map(PantryItemDtoMapper::toResponse).toList();
  }
}
