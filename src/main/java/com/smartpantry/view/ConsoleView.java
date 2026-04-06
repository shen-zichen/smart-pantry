package com.smartpantry.view;

import com.smartpantry.controller.GroceryController;
import com.smartpantry.controller.MealPlanController;
import com.smartpantry.controller.PantryController;
import com.smartpantry.controller.RecipeController;
import com.smartpantry.model.*;
import com.smartpantry.strategy.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Text-based console UI for Smart Pantry & Meal Manager. Warm, conversational tone — not a robot,
 * not a spreadsheet. Routes user input to the appropriate controller.
 */
public class ConsoleView {

  private final PantryController pantryController;
  private final RecipeController recipeController;
  private final MealPlanController mealPlanController;
  private final GroceryController groceryController;
  private final Scanner scanner;
  private boolean running;

  // Display mode name for the status bar
  private String displayMode = "Casual";

  public ConsoleView(
      PantryController pantryController,
      RecipeController recipeController,
      MealPlanController mealPlanController,
      GroceryController groceryController) {
    this.pantryController = pantryController;
    this.recipeController = recipeController;
    this.mealPlanController = mealPlanController;
    this.groceryController = groceryController;
    this.scanner = new Scanner(System.in);
    this.running = true;
  }

  // ======== Main Loop ========

  /** Starts the application. */
  public void run() {
    printWelcome();

    while (running) {
      printMainMenu();
      int choice = readInt("Your pick: ", 0, 6);

      switch (choice) {
        case 1 -> pantryMenu();
        case 2 -> recipeMenu();
        case 3 -> mealPlanMenu();
        case 4 -> groceryMenu();
        case 5 -> settingsMenu();
        case 0 -> quit();
        default -> print("Hmm, that's not on the menu. Try again.");
      }
    }
  }

  // ======== Main Menu ========

  private void printWelcome() {
    println("");
    println("  ╔══════════════════════════════════════════╗");
    println("  ║                                          ║");
    println("  ║     Smart Pantry & Meal Manager          ║");
    println("  ║     Your kitchen, organized.             ║");
    println("  ║                                          ║");
    println("  ╚══════════════════════════════════════════╝");
    println("");
    println("  Hey there! Let's figure out what's for dinner.");
    println("");
  }

  private void printMainMenu() {
    println("");
    println("  ─────────────────────────────────────");
    println("  What would you like to do?       [" + displayMode + " mode]");
    println("  ─────────────────────────────────────");
    println("  1. My Pantry");
    println("  2. Recipes");
    println("  3. Plan My Meals");
    println("  4. Grocery List");
    println("  5. Settings");
    println("  0. Exit");
    println("  ─────────────────────────────────────");
  }

  // ======== Pantry Menu ========

  private void pantryMenu() {
    println("");
    println("  My Pantry");
    println("  ─────────────────────────────────────");
    println("  1. See what I have");
    println("  2. Add something");
    println("  3. I used something");
    println("  4. Restock something");
    println("  0. Back");
    println("  ─────────────────────────────────────");

    int choice = readInt("Your pick: ", 0, 4);
    switch (choice) {
      case 1 -> showPantry();
      case 2 -> addPantryItem();
      case 3 -> consumePantryItem();
      case 4 -> restockPantryItem();
      case 0 -> {}
    }
  }

  private void showPantry() {
    println("");
    println("  Here's what's in your pantry:");
    println("  ─────────────────────────────────────");
    String listing = pantryController.listAllItems();
    println("  " + listing.replace("\n", "\n  "));
    println("");
  }

  private void addPantryItem() {
    println("");
    println("  Let's add something to your pantry.");
    println("");

    String name = readString("  What is it? (e.g., chicken thigh): ");
    double quantity = readDouble("  How much? ");
    UnitType unit = readUnitType();
    CategoryType category = readCategoryType();
    LocalDate expiration =
        readOptionalDate("  Expiration date (YYYY-MM-DD, or press Enter to skip): ");
    double threshold = readDouble("  Alert me when it drops below: ");

    pantryController.addItem(
        name, quantity, unit, category, LocalDate.now(), expiration, threshold);
    println("");
    println("  Got it! Added " + quantity + unit.getAbbreviation() + " of " + name + ".");
  }

  private void consumePantryItem() {
    String name = readString("  What did you use? ");
    List<PantryItem> matches = pantryController.findItemsByName(name);

    if (matches.isEmpty()) {
      println("  Hmm, I don't see \"" + name + "\" in your pantry.");
      return;
    }

    PantryItem item = matches.get(0);
    double amount = readDouble("  How much did you use? ");
    pantryController.consumeItem(item, amount);
    println(
        "  Updated! "
            + item.getName()
            + " now has "
            + item.getQuantityInStock()
            + item.getIngredient().getUnitType().getAbbreviation()
            + " left.");
  }

  private void restockPantryItem() {
    String name = readString("  What are you restocking? ");
    List<PantryItem> matches = pantryController.findItemsByName(name);

    if (matches.isEmpty()) {
      println("  Hmm, I don't see \"" + name + "\" in your pantry. Try adding it first.");
      return;
    }

    PantryItem item = matches.get(0);
    double amount = readDouble("  How much are you adding? ");
    pantryController.restockItem(item, amount);
    println(
        "  Nice! "
            + item.getName()
            + " is now at "
            + item.getQuantityInStock()
            + item.getIngredient().getUnitType().getAbbreviation()
            + ".");
  }

  // ======== Recipe Menu ========

  private void recipeMenu() {
    println("");
    println("  Recipes");
    println("  ─────────────────────────────────────");
    println("  1. Browse all recipes");
    println("  2. Search by name");
    println("  3. Filter by cuisine");
    println("  4. Filter by tag");
    println("  5. Scale a recipe");
    println("  6. Twin recipe swap");
    println("  0. Back");
    println("  ─────────────────────────────────────");

    int choice = readInt("Your pick: ", 0, 6);
    switch (choice) {
      case 1 -> browseRecipes();
      case 2 -> searchRecipeByName();
      case 3 -> filterByCuisine();
      case 4 -> filterByTag();
      case 5 -> scaleRecipe();
      case 6 -> twinSwap();
      case 0 -> {}
    }
  }

  private void browseRecipes() {
    List<Recipe> recipes = recipeController.getAllRecipes();
    if (recipes.isEmpty()) {
      println("  No recipes yet. Load some from CSV or add them!");
      return;
    }

    println("");
    println("  Your recipe library:");
    println("  ─────────────────────────────────────");
    for (int i = 0; i < recipes.size(); i++) {
      Recipe r = recipes.get(i);
      println(
          "  "
              + (i + 1)
              + ". "
              + r.getName()
              + " — "
              + r.getDescription()
              + " ("
              + r.getCuisineType().getDisplayName()
              + ", serves "
              + formatNumber(r.getServings())
              + ")");
    }
    println("");

    println("  Enter a number to see the full recipe, or 0 to go back:");
    int choice = readInt("  ", 0, recipes.size());
    if (choice > 0) {
      showFullRecipe(recipes.get(choice - 1));
    }
  }

  private void searchRecipeByName() {
    String name = readString("  Recipe name: ");
    Recipe found = recipeController.findByName(name);

    if (found == null) {
      println("  Couldn't find \"" + name + "\". Check the spelling?");
    } else {
      showFullRecipe(found);
    }
  }

  private void filterByCuisine() {
    println("  Pick a cuisine:");
    CuisineType[] cuisines = CuisineType.values();
    for (int i = 0; i < cuisines.length; i++) {
      println("  " + (i + 1) + ". " + cuisines[i].getDisplayName());
    }
    int choice = readInt("  ", 1, cuisines.length);
    CuisineType selected = cuisines[choice - 1];

    List<Recipe> results = recipeController.findByCuisine(selected);
    if (results.isEmpty()) {
      println("  No " + selected.getDisplayName() + " recipes found.");
    } else {
      println("  Found " + results.size() + " " + selected.getDisplayName() + " recipe(s):");
      results.forEach(r -> println("  • " + r.getName()));
    }
  }

  private void filterByTag() {
    println("  Pick a tag:");
    RecipeTag[] tags = RecipeTag.values();
    for (int i = 0; i < tags.length; i++) {
      println("  " + (i + 1) + ". " + tags[i].getDisplayName());
    }
    int choice = readInt("  ", 1, tags.length);
    RecipeTag selected = tags[choice - 1];

    List<Recipe> results = recipeController.findByTag(selected);
    if (results.isEmpty()) {
      println("  No recipes tagged \"" + selected.getDisplayName() + "\".");
    } else {
      println("  Found " + results.size() + " recipe(s):");
      results.forEach(r -> println("  • " + r.getName()));
    }
  }

  private void showFullRecipe(Recipe recipe) {
    println("");
    println("  " + recipeController.formatRecipe(recipe).replace("\n", "\n  "));

    // If pro mode, offer anchor percentages
    if (displayMode.equals("Professional")) {
      println("");
      String anchorName =
          readString("  Show anchor percentages? Enter ingredient name (or Enter to skip): ");
      if (!anchorName.isBlank()) {
        try {
          Map<String, Double> pct = recipeController.getAnchorPercentages(recipe, anchorName);
          println("  Anchor: " + anchorName + " = 100%");
          pct.forEach(
              (name, percent) -> {
                if (!name.equalsIgnoreCase(anchorName)) {
                  println("  • " + name + " = " + formatNumber(percent) + "%");
                }
              });
        } catch (IllegalArgumentException e) {
          println("  Couldn't find \"" + anchorName + "\" in this recipe.");
        }
      }
    }
    println("");
  }

  private void scaleRecipe() {
    String name = readString("  Which recipe? ");
    Recipe recipe = recipeController.findByName(name);
    if (recipe == null) {
      println("  Couldn't find \"" + name + "\".");
      return;
    }

    println("  Scale by:");
    println("  1. Servings");
    println("  2. Anchor ingredient (pro)");
    int choice = readInt("  ", 1, 2);

    Recipe scaled;
    if (choice == 1) {
      double servings = readDouble("  How many servings? ");
      scaled = recipeController.scaleByServings(recipe, servings);
    } else {
      String anchor = readString("  Anchor ingredient name: ");
      double qty = readDouble("  New quantity for " + anchor + ": ");
      scaled = recipeController.scaleByAnchor(recipe, anchor, qty);
    }

    println("  Scaled recipe:");
    showFullRecipe(scaled);
  }

  private void twinSwap() {
    List<TwinRecipe> twins = recipeController.getAllTwinRecipes();
    if (twins.isEmpty()) {
      println("  No twin recipes set up yet.");
      return;
    }

    println("  Twin Recipes:");
    for (int i = 0; i < twins.size(); i++) {
      TwinRecipe t = twins.get(i);
      String activeMode = t.isHealthyActive() ? "Healthy" : "Guilty Pleasure";
      println("  " + (i + 1) + ". " + t.getName() + " (currently: " + activeMode + ")");
    }

    int choice = readInt("  Pick one to swap: ", 1, twins.size());
    TwinRecipe selected = twins.get(choice - 1);
    Recipe active = recipeController.swapTwinVariant(selected);

    String mode = selected.isHealthyActive() ? "Healthy" : "Guilty Pleasure";
    println("  Swapped! Now showing the " + mode + " version.");
    showFullRecipe(active);
  }

  // ======== Meal Plan Menu ========

  private void mealPlanMenu() {
    println("");
    println("  Plan My Meals");
    println("  ─────────────────────────────────────");
    println("  1. Generate a meal plan");
    println("  2. View current plan");
    println("  3. I just cooked something");
    println("  0. Back");
    println("  ─────────────────────────────────────");

    int choice = readInt("Your pick: ", 0, 3);
    switch (choice) {
      case 1 -> generateMealPlan();
      case 2 -> viewCurrentPlan();
      case 3 -> postMealFlow();
      case 0 -> {}
    }
  }

  private void generateMealPlan() {
    println("  What's your priority?");
    println("  1. Use up expiring food first (Zero Waste)");
    println("  2. Use what I already have (Pantry First)");
    println("  3. Only suggest recipes I can fully make (Strict Inventory)");

    int choice = readInt("  ", 1, 3);
    int maxRecipes = (int) readDouble("  How many recipes? ");

    switch (choice) {
      case 1 -> {
        int window = (int) readDouble("  Expiration window (days): ");
        mealPlanController.setStrategy(new ZeroWasteStrategy(window, maxRecipes));
      }
      case 2 -> mealPlanController.setStrategy(new PantryFirstStrategy(maxRecipes));
      case 3 -> mealPlanController.setStrategy(new StrictInventoryStrategy(maxRecipes));
    }

    List<PantryItem> inventory = pantryController.getInventory();
    List<Recipe> recipes = recipeController.getAllRecipes();

    if (recipes.isEmpty()) {
      println("  You don't have any recipes loaded. Add some first!");
      return;
    }

    MealPlan plan = mealPlanController.generatePlan(inventory, recipes);
    println("");
    println("  " + mealPlanController.formatPlan(plan).replace("\n", "\n  "));

    // Offer grocery list if anything is missing
    println("  Want to see what you need to buy for this plan? (y/n)");
    String answer = readString("  ");
    if (answer.equalsIgnoreCase("y")) {
      String groceryList = groceryController.generateAndFormat(plan, inventory);
      println("  " + groceryList.replace("\n", "\n  "));
    }
  }

  private void viewCurrentPlan() {
    MealPlan plan = mealPlanController.getCurrentPlan();
    if (plan == null) {
      println("  No meal plan generated yet. Go make one!");
      return;
    }
    println("");
    println("  " + mealPlanController.formatPlan(plan).replace("\n", "\n  "));
  }

  private void postMealFlow() {
    String name = readString("  What did you cook? ");
    Recipe recipe = recipeController.findByName(name);

    if (recipe == null) {
      println("  Couldn't find \"" + name + "\" in your recipes.");
      return;
    }

    // Deduct ingredients
    mealPlanController.postMealConsume(recipe, pantryController);
    println("  Nice! Updated your pantry after making " + recipe.getName() + ".");

    // Check for leftovers
    List<PantryItem> leftovers = mealPlanController.getLeftovers(pantryController, 200);
    if (!leftovers.isEmpty()) {
      println("");
      println("  " + mealPlanController.formatLeftovers(leftovers).replace("\n", "\n  "));
      println("");
      String answer = readString("  ");
      if (answer.equalsIgnoreCase("y")) {
        // Switch to zero waste and regenerate
        mealPlanController.setStrategy(new ZeroWasteStrategy(3, 2));
        List<PantryItem> inventory = pantryController.getInventory();
        List<Recipe> recipes = recipeController.getAllRecipes();
        MealPlan plan = mealPlanController.generatePlan(inventory, recipes);
        println("");
        println("  " + mealPlanController.formatPlan(plan).replace("\n", "\n  "));
      }
    }
  }

  // ======== Grocery Menu ========

  private void groceryMenu() {
    MealPlan plan = mealPlanController.getCurrentPlan();
    if (plan == null) {
      println("  Generate a meal plan first, then I can tell you what to buy.");
      return;
    }

    println("");
    List<PantryItem> inventory = pantryController.getInventory();
    String result = groceryController.generateAndFormat(plan, inventory);
    println("  " + result.replace("\n", "\n  "));
  }

  // ======== Settings Menu ========

  private void settingsMenu() {
    println("");
    println("  Settings");
    println("  ─────────────────────────────────────");
    println("  Current display mode: " + displayMode);
    println("");
    println("  1. Casual mode  — \"2 chicken thighs, a pinch of salt\"");
    println("  2. Professional — \"500g chicken thigh (100%), 2g salt (0.4%)\"");
    println("  0. Back");
    println("  ─────────────────────────────────────");

    int choice = readInt("Your pick: ", 0, 2);
    if (choice == 1 || choice == 2) {
      switchDisplayMode(choice);
    }
  }

  private void switchDisplayMode(int choice) {
    // The View needs access to a formatter factory or the converter
    // For now, this is handled by the Main class which holds the converter
    // and passes formatters. We'll emit an event that Main can listen to.
    displayMode = (choice == 1) ? "Casual" : "Professional";
    println("  Switched to " + displayMode + " mode.");
    // Note: actual formatter swapping is done via the formatterSwapCallback
    if (formatterSwapCallback != null) {
      formatterSwapCallback.accept(choice);
    }
  }

  // Callback for Main to wire up formatter swapping across all controllers
  private java.util.function.IntConsumer formatterSwapCallback;

  public void setFormatterSwapCallback(java.util.function.IntConsumer callback) {
    this.formatterSwapCallback = callback;
  }

  // ======== Quit ========

  private void quit() {
    println("");
    println("  Thanks for cooking with Smart Pantry! See you next time.");
    println("");
    running = false;
  }

  // ======== Input Helpers ========

  private String readString(String prompt) {
    print(prompt);
    return scanner.nextLine().trim();
  }

  private double readDouble(String prompt) {
    while (true) {
      print(prompt);
      try {
        return Double.parseDouble(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        println("  That doesn't look like a number. Try again.");
      }
    }
  }

  private int readInt(String prompt, int min, int max) {
    while (true) {
      double val = readDouble(prompt);
      int intVal = (int) val;
      if (intVal >= min && intVal <= max) {
        return intVal;
      }
      println("  Pick a number between " + min + " and " + max + ".");
    }
  }

  private UnitType readUnitType() {
    println("  Unit type:");
    UnitType[] units = UnitType.values();
    for (int i = 0; i < units.length; i++) {
      println("  " + (i + 1) + ". " + units[i].name() + " (" + units[i].getAbbreviation() + ")");
    }
    int choice = readInt("  ", 1, units.length);
    return units[choice - 1];
  }

  private CategoryType readCategoryType() {
    println("  Category:");
    CategoryType[] categories = CategoryType.values();
    for (int i = 0; i < categories.length; i++) {
      println("  " + (i + 1) + ". " + categories[i].getDisplayName());
    }
    int choice = readInt("  ", 1, categories.length);
    return categories[choice - 1];
  }

  private LocalDate readOptionalDate(String prompt) {
    print(prompt);
    String input = scanner.nextLine().trim();
    if (input.isEmpty()) {
      return null;
    }
    try {
      return LocalDate.parse(input);
    } catch (DateTimeParseException e) {
      println("  Couldn't read that date. Skipping expiration.");
      return null;
    }
  }

  // ======== Output Helpers ========

  private void print(String message) {
    System.out.print(message);
  }

  private void println(String message) {
    System.out.println(message);
  }

  private String formatNumber(double value) {
    if (value == (long) value) {
      return String.valueOf((long) value);
    }
    return String.valueOf(value);
  }
}
