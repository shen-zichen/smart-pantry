package com.smartpantry.controller;

import com.smartpantry.dto.CreateRecipeRequest;
import com.smartpantry.dto.RecipeDtoMapper;
import com.smartpantry.dto.RecipeResponse;
import com.smartpantry.model.CuisineType;
import com.smartpantry.model.Recipe;
import com.smartpantry.model.RecipeTag;
import com.smartpantry.service.IRecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST endpoints for recipe management. */
@RestController
@RequestMapping("/api/recipes")
public class RecipeRestController {

  private final IRecipeService recipeService;

  public RecipeRestController(IRecipeService recipeService) {
    this.recipeService = recipeService;
  }

  /** GET /api/recipes — list all recipes. */
  @GetMapping
  public List<RecipeResponse> listAll() {
    return recipeService.getAllRecipes().stream().map(RecipeDtoMapper::toResponse).toList();
  }

  /** GET /api/recipes/{id} — get a specific recipe. */
  @GetMapping("/{id}")
  public RecipeResponse getById(@PathVariable Long id) {
    Recipe recipe = recipeService.getRecipeById(id);
    return RecipeDtoMapper.toResponse(recipe);
  }

  /** POST /api/recipes — create a new recipe. */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RecipeResponse addRecipe(@Valid @RequestBody CreateRecipeRequest request) {
    Recipe recipe = RecipeDtoMapper.fromRequest(request);
    Recipe saved = recipeService.addRecipe(recipe);
    return RecipeDtoMapper.toResponse(saved);
  }

  /** DELETE /api/recipes/{id} — delete a recipe. */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    recipeService.deleteRecipe(id);
  }

  /** GET /api/recipes/search?name=chicken — search by name. */
  @GetMapping("/search")
  public List<RecipeResponse> searchByName(@RequestParam String name) {
    return recipeService.searchByName(name).stream().map(RecipeDtoMapper::toResponse).toList();
  }

  /** GET /api/recipes/filter/tag?tag=HEALTHY — filter by tag. */
  @GetMapping("/filter/tag")
  public List<RecipeResponse> filterByTag(@RequestParam RecipeTag tag) {
    return recipeService.filterByTag(tag).stream().map(RecipeDtoMapper::toResponse).toList();
  }

  /** GET /api/recipes/filter/cuisine?cuisine=CHINESE — filter by cuisine. */
  @GetMapping("/filter/cuisine")
  public List<RecipeResponse> filterByCuisine(@RequestParam CuisineType cuisine) {
    return recipeService.filterByCuisine(cuisine).stream()
        .map(RecipeDtoMapper::toResponse)
        .toList();
  }
}
