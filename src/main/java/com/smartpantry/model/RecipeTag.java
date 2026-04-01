package com.smartpantry.model;

/**
 * Describes characteristics of a recipe. Strategies and filters use these tags to select
 * appropriate recipes. A recipe can have multiple tags (stored in a Set).
 */
public enum RecipeTag {
  HEALTHY("Healthy"), // low calorie, balanced — used by TwinRecipe healthy variant
  GUILTY_PLEASURE("Guilty Pleasure"), // indulgent version — used by TwinRecipe guilty variant
  QUICK("Quick"), // under 30 minutes
  BUDGET_FRIENDLY("Budget Friendly"), // uses cheap, common ingredients
  ZERO_WASTE("Zero Waste"), // designed to use up leftovers or expiring items
  MEAL_PREP("Meal Prep"), // scales well, stores well
  BEGINNER("Beginner"), // simple techniques, few steps
  SPICY("Spicy"),
  VEGETARIAN("Vegetarian"),
  VEGAN("Vegan"),
  GLUTEN_FREE("Gluten Free");

  private final String displayName;

  RecipeTag(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Returns the human-readable label for this tag.
   *
   * @return display name string (e.g., "Budget Friendly", "Zero Waste")
   */
  public String getDisplayName() {
    return displayName;
  }
}
