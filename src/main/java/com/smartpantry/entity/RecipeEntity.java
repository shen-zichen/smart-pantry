package com.smartpantry.entity;

import com.smartpantry.model.CuisineType;
import com.smartpantry.model.RecipeTag;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "recipes")
public class RecipeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private double servings;

  @Enumerated(EnumType.STRING)
  private CuisineType cuisineType;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "recipe_id")
  private List<IngredientEntity> ingredients = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
  @OrderColumn(name = "step_order")
  @Column(name = "step")
  private List<String> steps = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "tag")
  private Set<RecipeTag> tags = new HashSet<>();

  protected RecipeEntity() {}

  public RecipeEntity(
      String name,
      String description,
      List<IngredientEntity> ingredients,
      List<String> steps,
      double servings,
      CuisineType cuisineType,
      Set<RecipeTag> tags) {
    this.name = name;
    this.description = description;
    this.ingredients = ingredients;
    this.steps = steps;
    this.servings = servings;
    this.cuisineType = cuisineType;
    this.tags = tags;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getServings() {
    return servings;
  }

  public void setServings(double servings) {
    this.servings = servings;
  }

  public CuisineType getCuisineType() {
    return cuisineType;
  }

  public void setCuisineType(CuisineType cuisineType) {
    this.cuisineType = cuisineType;
  }

  public List<IngredientEntity> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<IngredientEntity> ingredients) {
    this.ingredients = ingredients;
  }

  public List<String> getSteps() {
    return steps;
  }

  public void setSteps(List<String> steps) {
    this.steps = steps;
  }

  public Set<RecipeTag> getTags() {
    return tags;
  }

  public void setTags(Set<RecipeTag> tags) {
    this.tags = tags;
  }
}
