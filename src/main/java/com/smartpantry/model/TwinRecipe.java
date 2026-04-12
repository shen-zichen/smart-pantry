package com.smartpantry.model;

import java.util.Objects;

/**
 * A pairing of two Recipe variants for the same dish — one healthy, one indulgent. The user can
 * swap between them at runtime. The rest of the system interacts through {@link #getActiveRecipe()}
 * and just gets a normal Recipe back.
 *
 * <p>Composition over inheritance: TwinRecipe is not a Recipe. It HAS two Recipes and manages which
 * one is active.
 */
public class TwinRecipe {

  private final String name;
  private final Recipe healthyVariant;
  private final Recipe guiltyVariant;
  private Recipe activeRecipe; // mutable — changes on swap()
  private Long id;

  /**
   * Constructs a TwinRecipe. Active variant defaults to healthy.
   *
   * @param name the shared dish name (e.g., "Orange Chicken")
   * @param healthyVariant the healthy version — must be tagged HEALTHY
   * @param guiltyVariant the guilty version — must be tagged GUILTY_PLEASURE
   * @throws IllegalArgumentException if variants are missing required tags
   */
  public TwinRecipe(String name, Recipe healthyVariant, Recipe guiltyVariant) {
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(healthyVariant, "Healthy variant cannot be null");
    Objects.requireNonNull(guiltyVariant, "Guilty variant cannot be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be blank");
    }
    if (!healthyVariant.hasTag(RecipeTag.HEALTHY)) {
      throw new IllegalArgumentException("Healthy variant must have HEALTHY tag");
    }
    if (!guiltyVariant.hasTag(RecipeTag.GUILTY_PLEASURE)) {
      throw new IllegalArgumentException("Guilty variant must have GUILTY_PLEASURE tag");
    }

    this.name = name;
    this.healthyVariant = healthyVariant;
    this.guiltyVariant = guiltyVariant;
    this.activeRecipe = healthyVariant; // default to healthy
  }

  /** Toggles between healthy and guilty variant. */
  public void swap() {
    // Reference equality — activeRecipe is always one of our two variant objects
    activeRecipe = (activeRecipe == healthyVariant) ? guiltyVariant : healthyVariant;
  }

  /** Returns whichever variant is currently selected. */
  public Recipe getActiveRecipe() {
    return activeRecipe;
  }

  public String getName() {
    return name;
  }

  public Recipe getHealthyVariant() {
    return healthyVariant;
  }

  public Recipe getGuiltyVariant() {
    return guiltyVariant;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /** Returns true if the healthy variant is currently active. */
  public boolean isHealthyActive() {
    return activeRecipe == healthyVariant;
  }

  @Override
  public String toString() {
    String mode = isHealthyActive() ? "Healthy" : "Guilty Pleasure";
    return "TwinRecipe{" + name + ", active=" + mode + "}";
  }
}
