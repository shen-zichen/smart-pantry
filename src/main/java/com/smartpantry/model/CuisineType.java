package com.smartpantry.model;

/**
 * Represents the cuisine origin of a recipe. Used for filtering and organizing the recipe library.
 */
public enum CuisineType {
  CHINESE("Chinese"),
  JAPANESE("Japanese"),
  KOREAN("Korean"),
  THAI("Thai"),
  VIETNAMESE("Vietnamese"),
  INDIAN("Indian"),
  ITALIAN("Italian"),
  MEXICAN("Mexican"),
  AMERICAN("American"),
  MEDITERRANEAN("Mediterranean"),
  FRENCH("French"),
  OTHER("Other");

  private final String displayName;

  CuisineType(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Returns the human-readable name for this cuisine.
   *
   * @return display name string (e.g., "Chinese", "Italian")
   */
  public String getDisplayName() {
    return displayName;
  }
}
