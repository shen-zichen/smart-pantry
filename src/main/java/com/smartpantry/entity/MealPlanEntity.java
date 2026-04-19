package com.smartpantry.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "meal_plans")
public class MealPlanEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String strategyName;
  private int days;
  private LocalDate createdDate;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "meal_plan_recipes",
      joinColumns = @JoinColumn(name = "meal_plan_id"),
      inverseJoinColumns = @JoinColumn(name = "recipe_id"))
  private List<RecipeEntity> recipes = new ArrayList<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "meal_plan_cooked",
      joinColumns = @JoinColumn(name = "meal_plan_id"))
  @Column(name = "recipe_index")
  private Set<Integer> cookedRecipeIndexes = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "meal_plan_scales",
      joinColumns = @JoinColumn(name = "meal_plan_id"))
  @Column(name = "scale_factor")
  @OrderColumn(name = "recipe_index")
  private List<Double> recipeScales = new ArrayList<>();

  private boolean requiresGroceryRun;

  protected MealPlanEntity() {}

  public MealPlanEntity(
      String strategyName, List<RecipeEntity> recipes, int days, LocalDate createdDate) {
    this.strategyName = strategyName;
    this.recipes = recipes;
    this.days = days;
    this.createdDate = createdDate;
  }

  public Long getId() {
    return id;
  }

  public String getStrategyName() {
    return strategyName;
  }

  public void setStrategyName(String strategyName) {
    this.strategyName = strategyName;
  }

  public List<RecipeEntity> getRecipes() {
    return recipes;
  }

  public void setRecipes(List<RecipeEntity> recipes) {
    this.recipes = recipes;
  }

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }

  public LocalDate getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDate createdDate) {
    this.createdDate = createdDate;
  }

  public Set<Integer> getCookedRecipeIndexes() {
    return cookedRecipeIndexes;
  }

  public void setCookedRecipeIndexes(Set<Integer> cookedRecipeIndexes) {
    this.cookedRecipeIndexes = cookedRecipeIndexes;
  }

  public void addCookedIndex(int index) {
    this.cookedRecipeIndexes.add(index);
  }

  public List<Double> getRecipeScales() {
    return recipeScales;
  }

  public void setRecipeScales(List<Double> recipeScales) {
    this.recipeScales = recipeScales;
  }

  public boolean isRequiresGroceryRun() {
    return requiresGroceryRun;
  }

  public void setRequiresGroceryRun(boolean requiresGroceryRun) {
    this.requiresGroceryRun = requiresGroceryRun;
  }
}
