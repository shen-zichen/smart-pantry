package com.smartpantry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Inbound DTO for creating a twin recipe pair. */
public class CreateTwinRecipeRequest {

  @NotBlank(message = "Name is required")
  private String name;

  @NotNull(message = "Healthy variant is required")
  private CreateRecipeRequest healthyVariant;

  @NotNull(message = "Guilty variant is required")
  private CreateRecipeRequest guiltyVariant;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CreateRecipeRequest getHealthyVariant() {
    return healthyVariant;
  }

  public void setHealthyVariant(CreateRecipeRequest healthyVariant) {
    this.healthyVariant = healthyVariant;
  }

  public CreateRecipeRequest getGuiltyVariant() {
    return guiltyVariant;
  }

  public void setGuiltyVariant(CreateRecipeRequest guiltyVariant) {
    this.guiltyVariant = guiltyVariant;
  }
}
