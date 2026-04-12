package com.smartpantry.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The output of a meal plan strategy. Immutable and read-only — strategies produce MealPlans,
 * controllers pass them to the view, the view displays them.
 *
 * <p>Think of it like a receipt: the strategy generates it, you read it, but the MealPlan itself
 * doesn't do any logic.
 */
public class MealPlan {

  private final String strategyName; // e.g., "Zero Waste", "Budget" — for display
  private final List<Recipe> recipes;
  private final int days; // how many days this plan covers
  private final LocalDate createdDate;
  private Long id;

  /**
   * Constructs a MealPlan with validated and defensively copied inputs.
   *
   * @param strategyName the name of the strategy that generated this plan
   * @param recipes the ordered list of recipes in this plan
   * @param days how many days this plan covers, must be positive
   * @param createdDate when this plan was generated
   */
  public MealPlan(String strategyName, List<Recipe> recipes, int days, LocalDate createdDate) {
    Objects.requireNonNull(strategyName, "Strategy name cannot be null");
    Objects.requireNonNull(recipes, "Recipes cannot be null");
    Objects.requireNonNull(createdDate, "Created date cannot be null");

    if (strategyName.isBlank()) {
      throw new IllegalArgumentException("Strategy name cannot be blank");
    }
    if (recipes.isEmpty()) {
      throw new IllegalArgumentException("Meal plan must contain at least one recipe");
    }
    if (days <= 0) {
      throw new IllegalArgumentException("Days must be positive: " + days);
    }

    this.strategyName = strategyName;
    this.recipes = new ArrayList<>(recipes); // defensive copy in
    this.days = days;
    this.createdDate = createdDate;
  }

  public String getStrategyName() {
    return strategyName;
  }

  // Defensive copy out
  public List<Recipe> getRecipes() {
    return new ArrayList<>(recipes);
  }

  /** Returns the number of recipes in this plan. */
  public int getRecipeCount() {
    return recipes.size();
  }

  public int getDays() {
    return days;
  }

  public LocalDate getCreatedDate() {
    return createdDate;
  }

  @Override
  public String toString() {
    return "MealPlan{"
        + "strategy='"
        + strategyName
        + '\''
        + ", recipes="
        + recipes.size()
        + ", days="
        + days
        + ", created="
        + createdDate
        + '}';
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
