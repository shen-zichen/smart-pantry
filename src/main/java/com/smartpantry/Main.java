package com.smartpantry;

import com.smartpantry.controller.GroceryController;
import com.smartpantry.controller.MealPlanController;
import com.smartpantry.controller.PantryController;
import com.smartpantry.controller.RecipeController;
import com.smartpantry.io.CsvInventoryReader;
import com.smartpantry.io.CsvRecipeReader;
import com.smartpantry.io.CsvUnitConversionLoader;
import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;
import com.smartpantry.observer.ExpirationAlertObserver;
import com.smartpantry.observer.InventoryManager;
import com.smartpantry.observer.LowStockAlertObserver;
import com.smartpantry.strategy.*;
import com.smartpantry.util.RecipeScaler;
import com.smartpantry.util.UnitConverter;
import com.smartpantry.view.ConsoleView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Application entry point. Wires together all components: models, observers, strategies,
 * controllers, and the console view.
 *
 * <p>This is the only place where concrete classes are instantiated and connected. Everything else
 * talks through interfaces.
 */
public class Main {

  public static void main(String[] args) {
    System.out.println();
    System.out.println("  Starting up...");

    // ---- 1. Load data ----

    UnitConverter converter = loadUnitConverter();
    List<PantryItem> savedInventory = loadInventory();
    List<Recipe> savedRecipes = loadRecipes();

    // ---- 2. Core model layer ----

    InventoryManager inventoryManager = new InventoryManager(3);

    // Register observers — alerts fire automatically on consume/restock
    inventoryManager.registerObserver(new LowStockAlertObserver());
    inventoryManager.registerObserver(new ExpirationAlertObserver());

    // Load saved inventory into manager
    for (PantryItem item : savedInventory) {
      inventoryManager.addItem(item);
    }

    // ---- 3. Formatters (start in casual mode) ----

    IUnitFormatter casualFormatter = new CasualFormatter(converter);
    IUnitFormatter proFormatter = new ProfessionalFormatter(converter);
    IUnitFormatter activeFormatter = casualFormatter;

    // ---- 4. Strategy layer ----

    RecipeScaler scaler = new RecipeScaler();
    IMealPlanStrategy defaultStrategy = new StrictInventoryStrategy(3);
    MealPlanGenerator generator = new MealPlanGenerator(defaultStrategy);
    IGroceryListGenerator groceryGenerator = new GroceryListGenerator();

    // ---- 5. Controller layer ----

    PantryController pantryController =
        new PantryController(inventoryManager, activeFormatter, converter);

    RecipeController recipeController = new RecipeController(scaler, activeFormatter);

    MealPlanController mealPlanController = new MealPlanController(generator, activeFormatter);

    GroceryController groceryController = new GroceryController(groceryGenerator, activeFormatter);

    // Load saved recipes into controller
    for (Recipe recipe : savedRecipes) {
      recipeController.addRecipe(recipe);
    }

    // ---- 6. View layer ----

    ConsoleView view =
        new ConsoleView(pantryController, recipeController, mealPlanController, groceryController);

    // Wire up formatter toggle — when user switches mode,
    // all controllers update simultaneously
    view.setFormatterSwapCallback(
        choice -> {
          IUnitFormatter newFormatter = (choice == 1) ? casualFormatter : proFormatter;
          pantryController.setFormatter(newFormatter);
          recipeController.setFormatter(newFormatter);
          mealPlanController.setFormatter(newFormatter);
          groceryController.setFormatter(newFormatter);
        });

    // ---- 7. Launch ----

    System.out.println(
        "  Loaded "
            + savedInventory.size()
            + " pantry items and "
            + savedRecipes.size()
            + " recipes.");

    view.run();
  }

  // ---- Data Loading Helpers ----

  private static UnitConverter loadUnitConverter() {
    Path path = Path.of("src/main/resources/data/unit_conversions.csv");
    if (Files.exists(path)) {
      try {
        return new CsvUnitConversionLoader().load(path);
      } catch (IOException e) {
        System.err.println("  Warning: Could not load unit conversions: " + e.getMessage());
      }
    }
    return new UnitConverter();
  }

  private static List<PantryItem> loadInventory() {
    Path path = Path.of("src/main/resources/data/inventory.csv");
    if (Files.exists(path)) {
      try {
        return new CsvInventoryReader().read(path);
      } catch (IOException e) {
        System.err.println("  Warning: Could not load inventory: " + e.getMessage());
      }
    }
    return List.of();
  }

  private static List<Recipe> loadRecipes() {
    Path path = Path.of("src/main/resources/data/recipes.csv");
    if (Files.exists(path)) {
      try {
        return new CsvRecipeReader().read(path);
      } catch (IOException e) {
        System.err.println("  Warning: Could not load recipes: " + e.getMessage());
      }
    }
    return List.of();
  }
}
