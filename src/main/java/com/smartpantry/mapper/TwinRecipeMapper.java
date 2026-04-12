package com.smartpantry.mapper;

import com.smartpantry.entity.TwinRecipeEntity;
import com.smartpantry.model.Recipe;
import com.smartpantry.model.TwinRecipe;

public class TwinRecipeMapper {

  public static TwinRecipe toDomain(TwinRecipeEntity entity) {
    Recipe healthy = RecipeMapper.toDomain(entity.getHealthyVariant());
    Recipe guilty = RecipeMapper.toDomain(entity.getGuiltyVariant());

    TwinRecipe twin = new TwinRecipe(entity.getName(), healthy, guilty);

    // TwinRecipe defaults to healthy — only swap if guilty was active
    if (!entity.isHealthyActive()) {
      twin.swap();
    }

    return twin;
  }

  public static TwinRecipeEntity toEntity(TwinRecipe twinRecipe) {
    return new TwinRecipeEntity(
        twinRecipe.getName(),
        RecipeMapper.toEntity(twinRecipe.getHealthyVariant()),
        RecipeMapper.toEntity(twinRecipe.getGuiltyVariant()),
        twinRecipe.isHealthyActive());
  }

  private TwinRecipeMapper() {}
}
