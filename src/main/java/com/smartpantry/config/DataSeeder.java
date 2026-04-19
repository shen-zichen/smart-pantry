package com.smartpantry.config;

import com.smartpantry.entity.IngredientEntity;
import com.smartpantry.entity.PantryItemEntity;
import com.smartpantry.entity.RecipeEntity;
import com.smartpantry.model.CategoryType;
import com.smartpantry.model.CuisineType;
import com.smartpantry.model.RecipeTag;
import com.smartpantry.model.UnitType;
import com.smartpantry.repository.PantryItemRepository;
import com.smartpantry.repository.RecipeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

  private final PantryItemRepository pantryItemRepository;
  private final RecipeRepository recipeRepository;

  public DataSeeder(PantryItemRepository pantryItemRepository, RecipeRepository recipeRepository) {
    this.pantryItemRepository = pantryItemRepository;
    this.recipeRepository = recipeRepository;
  }

  @Override
  public void run(String... args) {
    seedPantryItems();
    seedRecipes();

    System.out.println(
        "Seeded "
            + pantryItemRepository.count()
            + " pantry items, "
            + recipeRepository.count()
            + " recipes");
  }

  private void seedPantryItems() {
    // Proteins
    IngredientEntity chicken =
        new IngredientEntity("Chicken Thigh", 500, UnitType.GRAM, CategoryType.PROTEIN);
    IngredientEntity groundBeef =
        new IngredientEntity("Ground Beef", 400, UnitType.GRAM, CategoryType.PROTEIN);
    IngredientEntity eggs =
        new IngredientEntity("Eggs", 12, UnitType.PIECE, CategoryType.PROTEIN);
    IngredientEntity tofu =
        new IngredientEntity("Firm Tofu", 300, UnitType.GRAM, CategoryType.PROTEIN);
    IngredientEntity salmon =
        new IngredientEntity("Salmon Fillet", 250, UnitType.GRAM, CategoryType.PROTEIN);

    // Vegetables
    IngredientEntity broccoli =
        new IngredientEntity("Broccoli", 300, UnitType.GRAM, CategoryType.VEGETABLE);
    IngredientEntity garlic =
        new IngredientEntity("Garlic", 10, UnitType.PIECE, CategoryType.VEGETABLE);
    IngredientEntity onion =
        new IngredientEntity("Onion", 5, UnitType.PIECE, CategoryType.VEGETABLE);
    IngredientEntity bellPepper =
        new IngredientEntity("Bell Pepper", 3, UnitType.PIECE, CategoryType.VEGETABLE);
    IngredientEntity spinach =
        new IngredientEntity("Spinach", 200, UnitType.GRAM, CategoryType.VEGETABLE);
    IngredientEntity ginger =
        new IngredientEntity("Ginger", 50, UnitType.GRAM, CategoryType.VEGETABLE);

    // Grains
    IngredientEntity rice =
        new IngredientEntity("Jasmine Rice", 1000, UnitType.GRAM, CategoryType.GRAIN);
    IngredientEntity pasta =
        new IngredientEntity("Spaghetti", 500, UnitType.GRAM, CategoryType.GRAIN);
    IngredientEntity tortillas =
        new IngredientEntity("Flour Tortillas", 8, UnitType.PIECE, CategoryType.GRAIN);

    // Condiments & Oils
    IngredientEntity soySauce =
        new IngredientEntity("Soy Sauce", 200, UnitType.MILLILITER, CategoryType.OIL);
    IngredientEntity oliveOil =
        new IngredientEntity("Olive Oil", 500, UnitType.MILLILITER, CategoryType.OIL);
    IngredientEntity sesameSeed =
        new IngredientEntity("Sesame Oil", 100, UnitType.MILLILITER, CategoryType.OIL);

    // Dairy
    IngredientEntity butter =
        new IngredientEntity("Butter", 200, UnitType.GRAM, CategoryType.DAIRY);
    IngredientEntity cheese =
        new IngredientEntity("Cheddar Cheese", 200, UnitType.GRAM, CategoryType.DAIRY);

    // Spices
    IngredientEntity salt =
        new IngredientEntity("Salt", 500, UnitType.GRAM, CategoryType.SPICE);
    IngredientEntity blackPepper =
        new IngredientEntity("Black Pepper", 100, UnitType.GRAM, CategoryType.SPICE);

    // Save pantry items — cascade handles ingredient persistence
    LocalDate today = LocalDate.now();

    pantryItemRepository.save(
        new PantryItemEntity(chicken, 500, today, today.plusDays(5), 200));
    pantryItemRepository.save(
        new PantryItemEntity(groundBeef, 400, today, today.plusDays(4), 200));
    pantryItemRepository.save(
        new PantryItemEntity(eggs, 12, today, today.plusDays(14), 4));
    pantryItemRepository.save(
        new PantryItemEntity(tofu, 300, today, today.plusDays(7), 100));
    pantryItemRepository.save(
        new PantryItemEntity(salmon, 250, today, today.plusDays(3), 100));
    pantryItemRepository.save(
        new PantryItemEntity(broccoli, 300, today, today.plusDays(5), 100));
    pantryItemRepository.save(
        new PantryItemEntity(garlic, 10, today, today.plusDays(30), 3));
    pantryItemRepository.save(
        new PantryItemEntity(onion, 5, today, today.plusDays(21), 2));
    pantryItemRepository.save(
        new PantryItemEntity(bellPepper, 3, today, today.plusDays(7), 1));
    pantryItemRepository.save(
        new PantryItemEntity(spinach, 200, today, today.plusDays(4), 50));
    pantryItemRepository.save(
        new PantryItemEntity(ginger, 50, today, today.plusDays(14), 10));
    pantryItemRepository.save(
        new PantryItemEntity(rice, 1000, today, null, 300));
    pantryItemRepository.save(
        new PantryItemEntity(pasta, 500, today, null, 200));
    pantryItemRepository.save(
        new PantryItemEntity(tortillas, 8, today, today.plusDays(10), 2));
    pantryItemRepository.save(
        new PantryItemEntity(soySauce, 200, today, null, 50));
    pantryItemRepository.save(
        new PantryItemEntity(oliveOil, 500, today, null, 100));
    pantryItemRepository.save(
        new PantryItemEntity(sesameSeed, 100, today, null, 20));
    pantryItemRepository.save(
        new PantryItemEntity(butter, 200, today, today.plusDays(30), 50));
    pantryItemRepository.save(
        new PantryItemEntity(cheese, 200, today, today.plusDays(14), 50));
    pantryItemRepository.save(
        new PantryItemEntity(salt, 500, today, null, 100));
    pantryItemRepository.save(
        new PantryItemEntity(blackPepper, 100, today, null, 20));
  }

  private void seedRecipes() {
    // 1. Chicken Stir-Fry (Chinese, Healthy, Quick)
    recipeRepository.save(new RecipeEntity(
        "Chicken Stir-Fry",
        "Tender chicken with crisp vegetables in savory sauce",
        List.of(
            new IngredientEntity("Chicken Thigh", 300, UnitType.GRAM, CategoryType.PROTEIN),
            new IngredientEntity("Broccoli", 150, UnitType.GRAM, CategoryType.VEGETABLE),
            new IngredientEntity("Bell Pepper", 1, UnitType.PIECE, CategoryType.VEGETABLE),
            new IngredientEntity("Soy Sauce", 30, UnitType.MILLILITER, CategoryType.OIL),
            new IngredientEntity("Garlic", 3, UnitType.PIECE, CategoryType.VEGETABLE),
            new IngredientEntity("Jasmine Rice", 200, UnitType.GRAM, CategoryType.GRAIN)
        ),
        List.of(
            "Slice chicken into bite-sized pieces and marinate with soy sauce",
            "Cook rice according to package directions",
            "Heat oil in wok over high heat",
            "Stir-fry chicken until golden, about 5 minutes",
            "Add garlic, broccoli, and bell pepper — stir-fry 3 minutes",
            "Serve over steamed rice"
        ),
        4, CuisineType.CHINESE,
        Set.of(RecipeTag.HEALTHY, RecipeTag.QUICK)
    ));

    // 2. Spaghetti Aglio e Olio (Italian, Quick, Beginner, Budget)
    recipeRepository.save(new RecipeEntity(
        "Spaghetti Aglio e Olio",
        "Classic Roman pasta with garlic, olive oil, and chili flakes",
        List.of(
            new IngredientEntity("Spaghetti", 200, UnitType.GRAM, CategoryType.GRAIN),
            new IngredientEntity("Garlic", 6, UnitType.PIECE, CategoryType.VEGETABLE),
            new IngredientEntity("Olive Oil", 60, UnitType.MILLILITER, CategoryType.OIL),
            new IngredientEntity("Black Pepper", 2, UnitType.GRAM, CategoryType.SPICE)
        ),
        List.of(
            "Boil spaghetti in salted water until al dente",
            "Thinly slice garlic cloves",
            "Heat olive oil in a pan, sauté garlic until golden",
            "Toss drained pasta into the pan with garlic oil",
            "Season with black pepper and serve immediately"
        ),
        2, CuisineType.ITALIAN,
        Set.of(RecipeTag.QUICK, RecipeTag.BEGINNER, RecipeTag.BUDGET_FRIENDLY, RecipeTag.VEGAN)
    ));

    // 3. Beef Tacos (Mexican, Quick)
    recipeRepository.save(new RecipeEntity(
        "Beef Tacos",
        "Seasoned ground beef tacos with fresh toppings",
        List.of(
            new IngredientEntity("Ground Beef", 300, UnitType.GRAM, CategoryType.PROTEIN),
            new IngredientEntity("Flour Tortillas", 4, UnitType.PIECE, CategoryType.GRAIN),
            new IngredientEntity("Onion", 1, UnitType.PIECE, CategoryType.VEGETABLE),
            new IngredientEntity("Cheddar Cheese", 50, UnitType.GRAM, CategoryType.DAIRY),
            new IngredientEntity("Salt", 5, UnitType.GRAM, CategoryType.SPICE)
        ),
        List.of(
            "Dice the onion finely",
            "Brown ground beef in a skillet over medium-high heat",
            "Add diced onion and salt, cook until onion is soft",
            "Warm tortillas in a dry pan",
            "Fill tortillas with beef mixture and top with cheese"
        ),
        2, CuisineType.MEXICAN,
        Set.of(RecipeTag.QUICK, RecipeTag.BUDGET_FRIENDLY)
    ));

    // 4. Teriyaki Salmon Bowl (Japanese, Healthy, Meal Prep)
    recipeRepository.save(new RecipeEntity(
        "Teriyaki Salmon Bowl",
        "Glazed salmon over rice with steamed vegetables",
        List.of(
            new IngredientEntity("Salmon Fillet", 200, UnitType.GRAM, CategoryType.PROTEIN),
            new IngredientEntity("Jasmine Rice", 200, UnitType.GRAM, CategoryType.GRAIN),
            new IngredientEntity("Soy Sauce", 30, UnitType.MILLILITER, CategoryType.OIL),
            new IngredientEntity("Ginger", 10, UnitType.GRAM, CategoryType.VEGETABLE),
            new IngredientEntity("Broccoli", 100, UnitType.GRAM, CategoryType.VEGETABLE),
            new IngredientEntity("Sesame Oil", 10, UnitType.MILLILITER, CategoryType.OIL)
        ),
        List.of(
            "Cook jasmine rice and set aside",
            "Mix soy sauce, grated ginger, and sesame oil for teriyaki glaze",
            "Pan-sear salmon skin-side down for 4 minutes",
            "Flip and brush with teriyaki glaze, cook 3 more minutes",
            "Steam broccoli until tender-crisp",
            "Assemble bowl: rice, salmon, and broccoli. Drizzle remaining glaze"
        ),
        2, CuisineType.JAPANESE,
        Set.of(RecipeTag.HEALTHY, RecipeTag.MEAL_PREP)
    ));

    // 5. Garlic Butter Eggs (American, Quick, Beginner, Budget)
    recipeRepository.save(new RecipeEntity(
        "Garlic Butter Scrambled Eggs",
        "Fluffy scrambled eggs cooked in garlic butter",
        List.of(
            new IngredientEntity("Eggs", 4, UnitType.PIECE, CategoryType.PROTEIN),
            new IngredientEntity("Butter", 20, UnitType.GRAM, CategoryType.DAIRY),
            new IngredientEntity("Garlic", 2, UnitType.PIECE, CategoryType.VEGETABLE),
            new IngredientEntity("Salt", 2, UnitType.GRAM, CategoryType.SPICE),
            new IngredientEntity("Black Pepper", 1, UnitType.GRAM, CategoryType.SPICE)
        ),
        List.of(
            "Mince garlic finely",
            "Melt butter in a non-stick pan over low heat",
            "Sauté garlic for 30 seconds until fragrant",
            "Whisk eggs with salt and pepper, pour into pan",
            "Stir gently with a spatula until just set — don't overcook"
        ),
        2, CuisineType.AMERICAN,
        Set.of(RecipeTag.QUICK, RecipeTag.BEGINNER, RecipeTag.BUDGET_FRIENDLY, RecipeTag.VEGETARIAN, RecipeTag.GLUTEN_FREE)
    ));

    // 6. Tofu Stir-Fry (Thai, Healthy, Vegan)
    recipeRepository.save(new RecipeEntity(
        "Spicy Thai Tofu Stir-Fry",
        "Crispy tofu with vegetables in a sweet-spicy sauce",
        List.of(
            new IngredientEntity("Firm Tofu", 300, UnitType.GRAM, CategoryType.PROTEIN),
            new IngredientEntity("Bell Pepper", 1, UnitType.PIECE, CategoryType.VEGETABLE),
            new IngredientEntity("Onion", 1, UnitType.PIECE, CategoryType.VEGETABLE),
            new IngredientEntity("Soy Sauce", 20, UnitType.MILLILITER, CategoryType.OIL),
            new IngredientEntity("Sesame Oil", 10, UnitType.MILLILITER, CategoryType.OIL),
            new IngredientEntity("Ginger", 10, UnitType.GRAM, CategoryType.VEGETABLE),
            new IngredientEntity("Jasmine Rice", 200, UnitType.GRAM, CategoryType.GRAIN)
        ),
        List.of(
            "Press tofu for 15 minutes, then cut into cubes",
            "Cook rice according to package directions",
            "Pan-fry tofu cubes until golden on all sides",
            "Stir-fry sliced onion, bell pepper, and ginger",
            "Add soy sauce and sesame oil, toss with tofu",
            "Serve over steamed jasmine rice"
        ),
        2, CuisineType.THAI,
        Set.of(RecipeTag.HEALTHY, RecipeTag.VEGAN, RecipeTag.SPICY)
    ));

    // 7. Cheesy Beef Burrito (Mexican, Guilty Pleasure)
    recipeRepository.save(new RecipeEntity(
        "Loaded Cheesy Beef Burrito",
        "Oversized burrito packed with seasoned beef and melted cheese",
        List.of(
            new IngredientEntity("Ground Beef", 300, UnitType.GRAM, CategoryType.PROTEIN),
            new IngredientEntity("Flour Tortillas", 2, UnitType.PIECE, CategoryType.GRAIN),
            new IngredientEntity("Cheddar Cheese", 100, UnitType.GRAM, CategoryType.DAIRY),
            new IngredientEntity("Jasmine Rice", 150, UnitType.GRAM, CategoryType.GRAIN),
            new IngredientEntity("Onion", 1, UnitType.PIECE, CategoryType.VEGETABLE),
            new IngredientEntity("Salt", 3, UnitType.GRAM, CategoryType.SPICE)
        ),
        List.of(
            "Cook rice and set aside",
            "Brown beef with diced onion and salt",
            "Warm tortillas, layer rice, beef, and cheese",
            "Roll tightly into burritos",
            "Optional: toast on a pan until cheese melts and tortilla is crispy"
        ),
        2, CuisineType.MEXICAN,
        Set.of(RecipeTag.GUILTY_PLEASURE, RecipeTag.MEAL_PREP)
    ));

    // 8. Spinach & Egg Fried Rice (Chinese, Quick, Zero Waste)
    recipeRepository.save(new RecipeEntity(
        "Spinach Egg Fried Rice",
        "Quick fried rice to use up leftover rice and expiring spinach",
        List.of(
            new IngredientEntity("Jasmine Rice", 300, UnitType.GRAM, CategoryType.GRAIN),
            new IngredientEntity("Eggs", 2, UnitType.PIECE, CategoryType.PROTEIN),
            new IngredientEntity("Spinach", 100, UnitType.GRAM, CategoryType.VEGETABLE),
            new IngredientEntity("Soy Sauce", 15, UnitType.MILLILITER, CategoryType.OIL),
            new IngredientEntity("Garlic", 2, UnitType.PIECE, CategoryType.VEGETABLE),
            new IngredientEntity("Sesame Oil", 5, UnitType.MILLILITER, CategoryType.OIL)
        ),
        List.of(
            "Use day-old rice for best results (or cook and spread to cool)",
            "Scramble eggs in a hot wok, set aside",
            "Sauté minced garlic in sesame oil",
            "Add rice, stir-fry on high heat for 2 minutes",
            "Add spinach and soy sauce, toss until spinach wilts",
            "Mix scrambled eggs back in and serve"
        ),
        2, CuisineType.CHINESE,
        Set.of(RecipeTag.QUICK, RecipeTag.ZERO_WASTE, RecipeTag.BUDGET_FRIENDLY)
    ));
  }
}

