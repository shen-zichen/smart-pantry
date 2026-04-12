package com.smartpantry.dto;

import com.smartpantry.model.TwinRecipe;

/** Converts between domain TwinRecipe and its DTOs. */
public class TwinRecipeDtoMapper {

  public static TwinRecipeResponse toResponse(TwinRecipe twin) {
    return new TwinRecipeResponse(
        twin.getId(),
        twin.getName(),
        RecipeDtoMapper.toResponse(twin.getHealthyVariant()),
        RecipeDtoMapper.toResponse(twin.getGuiltyVariant()),
        twin.isHealthyActive());
  }

  public static TwinRecipe fromRequest(CreateTwinRecipeRequest request) {
    return new TwinRecipe(
        request.getName(),
        RecipeDtoMapper.fromRequest(request.getHealthyVariant()),
        RecipeDtoMapper.fromRequest(request.getGuiltyVariant()));
  }

  private TwinRecipeDtoMapper() {}
}
