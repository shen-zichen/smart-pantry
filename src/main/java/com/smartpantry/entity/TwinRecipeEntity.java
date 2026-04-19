package com.smartpantry.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "twin_recipes")
public class TwinRecipeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToOne
  @JoinColumn(name = "healthy_recipe_id")
  private RecipeEntity healthyVariant;

  @ManyToOne
  @JoinColumn(name = "guilty_recipe_id")
  private RecipeEntity guiltyVariant;

  private boolean healthyActive;

  protected TwinRecipeEntity() {}

  public TwinRecipeEntity(
      String name, RecipeEntity healthyVariant, RecipeEntity guiltyVariant, boolean healthyActive) {
    this.name = name;
    this.healthyVariant = healthyVariant;
    this.guiltyVariant = guiltyVariant;
    this.healthyActive = healthyActive;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RecipeEntity getHealthyVariant() {
    return healthyVariant;
  }

  public void setHealthyVariant(RecipeEntity healthyVariant) {
    this.healthyVariant = healthyVariant;
  }

  public RecipeEntity getGuiltyVariant() {
    return guiltyVariant;
  }

  public void setGuiltyVariant(RecipeEntity guiltyVariant) {
    this.guiltyVariant = guiltyVariant;
  }

  public boolean isHealthyActive() {
    return healthyActive;
  }

  public void setHealthyActive(boolean healthyActive) {
    this.healthyActive = healthyActive;
  }
}
