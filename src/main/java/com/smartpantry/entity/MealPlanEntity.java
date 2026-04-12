package com.smartpantry.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_plans")
public class MealPlanEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String strategyName;
  private int days;
  private LocalDate createdDate;

  @ManyToMany
  @JoinTable(
      name = "meal_plan_recipes",
      joinColumns = @JoinColumn(name = "meal_plan_id"),
      inverseJoinColumns = @JoinColumn(name = "recipe_id"))
  private List<RecipeEntity> recipes = new ArrayList<>();

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
}
