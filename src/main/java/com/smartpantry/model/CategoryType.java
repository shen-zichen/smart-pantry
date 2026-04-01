package com.smartpantry.model;

/**
 * Represents the category of a food or household item. Each constant carries a display label for
 * formatted output.
 */
public enum CategoryType {

  // --- Food ---
  PROTEIN("Protein"), // chicken, beef, tofu, eggs, fish
  DAIRY("Dairy"), // milk, cheese, yogurt, butter
  VEGETABLE("Vegetable"), // broccoli, carrots, spinach
  FRUIT("Fruit"), // apples, bananas, berries
  GRAIN("Grain"), // rice, pasta, bread, oats
  SPICE("Spice"), // salt, pepper, cumin, paprika
  OIL("Oil & Condiment"), // olive oil, soy sauce, vinegar
  BEVERAGE("Beverage"), // juice, coffee, tea
  SNACK("Snack"), // chips, nuts, crackers
  FROZEN("Frozen"), // frozen veggies, ice cream

  // --- Household (your app tracks household items too) ---
  HOUSEHOLD("Household"); // paper towels, soap, trash bags

  private final String displayName;

  CategoryType(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Returns the human-readable display name for this category.
   *
   * @return display name string (e.g., "Protein", "Oil & Condiment")
   */
  public String getDisplayName() {
    return this.displayName;
  }
}
