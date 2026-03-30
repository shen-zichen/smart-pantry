package com.smartpantry.model;

/**
 * Represents the unit of measurement for an ingredient. Each constant carries a display
 * abbreviation for formatted output.
 */
public enum UnitType {

  // --- Weight ---
  GRAM("g"),
  KILOGRAM("kg"),
  OUNCE("oz"),
  POUND("lb"),

  // --- Volume ---
  MILLILITER("mL"),
  LITER("L"),
  TEASPOON("tsp"),
  TABLESPOON("tbsp"),
  CUP("cup"),
  FLUID_OUNCE("fl oz"),

  // --- Countable (eggs, bananas, bags, cans, etc.) ---
  PIECE("pc"),
  BAG("bag"),
  CAN("can"),
  BOTTLE("bottle"),
  BOX("box");

  private final String abbreviation;

  UnitType(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  /**
   * Returns the short display abbreviation for this unit.
   *
   * @return abbreviation string (e.g., "g", "mL", "pc")
   */
  public String getAbbreviation() {
    return this.abbreviation;
  }
}
