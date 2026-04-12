package com.smartpantry.controller;

import com.smartpantry.dto.CreateTwinRecipeRequest;
import com.smartpantry.dto.TwinRecipeDtoMapper;
import com.smartpantry.dto.TwinRecipeResponse;
import com.smartpantry.model.TwinRecipe;
import com.smartpantry.service.ITwinRecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for twin recipe management. */
@RestController
@RequestMapping("/api/twin-recipes")
public class TwinRecipeRestController {

  private final ITwinRecipeService twinRecipeService;

  public TwinRecipeRestController(ITwinRecipeService twinRecipeService) {
    this.twinRecipeService = twinRecipeService;
  }

  /** GET /api/twin-recipes — list all twin recipes. */
  @GetMapping
  public List<TwinRecipeResponse> listAll() {
    return twinRecipeService.getAllTwinRecipes().stream()
        .map(TwinRecipeDtoMapper::toResponse)
        .toList();
  }

  /** GET /api/twin-recipes/{id} — get a specific twin recipe. */
  @GetMapping("/{id}")
  public TwinRecipeResponse getById(@PathVariable Long id) {
    TwinRecipe twin = twinRecipeService.getTwinRecipeById(id);
    return TwinRecipeDtoMapper.toResponse(twin);
  }

  /** POST /api/twin-recipes — create a new twin recipe pair. */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TwinRecipeResponse create(@Valid @RequestBody CreateTwinRecipeRequest request) {
    TwinRecipe twin = TwinRecipeDtoMapper.fromRequest(request);
    TwinRecipe saved = twinRecipeService.addTwinRecipe(twin);
    return TwinRecipeDtoMapper.toResponse(saved);
  }

  /** PUT /api/twin-recipes/{id}/swap — toggle between healthy and guilty. */
  @PutMapping("/{id}/swap")
  public TwinRecipeResponse swap(@PathVariable Long id) {
    TwinRecipe swapped = twinRecipeService.swap(id);
    return TwinRecipeDtoMapper.toResponse(swapped);
  }

  /** DELETE /api/twin-recipes/{id} — delete a twin recipe. */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    twinRecipeService.deleteTwinRecipe(id);
  }
}
