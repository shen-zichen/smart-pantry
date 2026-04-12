package com.smartpantry.dto;

/** Outbound DTO for twin recipes. Shows both variants and which is active. */
public class TwinRecipeResponse {

  private Long id;
  private String name;
  private RecipeResponse healthyVariant;
  private RecipeResponse guiltyVariant;
  private boolean healthyActive;

  public TwinRecipeResponse() {}

  public TwinRecipeResponse(
      Long id,
      String name,
      RecipeResponse healthyVariant,
      RecipeResponse guiltyVariant,
      boolean healthyActive) {
    this.id = id;
    this.name = name;
    this.healthyVariant = healthyVariant;
    this.guiltyVariant = guiltyVariant;
    this.healthyActive = healthyActive;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RecipeResponse getHealthyVariant() {
    return healthyVariant;
  }

  public void setHealthyVariant(RecipeResponse healthyVariant) {
    this.healthyVariant = healthyVariant;
  }

  public RecipeResponse getGuiltyVariant() {
    return guiltyVariant;
  }

  public void setGuiltyVariant(RecipeResponse guiltyVariant) {
    this.guiltyVariant = guiltyVariant;
  }

  public boolean isHealthyActive() {
    return healthyActive;
  }

  public void setHealthyActive(boolean healthyActive) {
    this.healthyActive = healthyActive;
  }
}
