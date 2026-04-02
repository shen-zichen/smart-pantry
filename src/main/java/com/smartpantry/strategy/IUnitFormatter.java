package com.smartpantry.strategy;

import com.smartpantry.model.Ingredient;

/**
 * Strategy interface for formatting ingredient display. The user toggles between formatters at
 * runtime — same data, different presentation.
 *
 * <p>Implementations:
 *
 * <ul>
 *   <li>CasualFormatter — "2 chicken thighs", "1 onion", "a pinch of salt"
 *   <li>ProfessionalFormatter — "500g chicken (anchor 100%)", "100g onion (20%)"
 * </ul>
 *
 * <p>The formatter never modifies the Ingredient — it only reads its fields and produces a display
 * string. Pure function, no side effects.
 */
public interface IUnitFormatter {

  /**
   * Formats an ingredient for display based on the active mode.
   *
   * @param ingredient the ingredient to format
   * @return a human-readable string representation
   */
  String format(Ingredient ingredient);
}
