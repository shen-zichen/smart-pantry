import { useState, useEffect } from "react";
import recipeApi from "../api/recipeApi";
import LoadingSpinner from "../components/LoadingSpinner";

const CUISINE_TYPES = [
  "CHINESE",
  "ITALIAN",
  "JAPANESE",
  "KOREAN",
  "MEXICAN",
  "INDIAN",
  "THAI",
  "VIETNAMESE",
  "FRENCH",
  "AMERICAN",
  "MEDITERRANEAN",
  "OTHER",
];

const RECIPE_TAGS = [
  "HEALTHY",
  "GUILTY_PLEASURE",
  "QUICK",
  "ZERO_WASTE",
  "BUDGET_FRIENDLY",
  "VEGETARIAN",
  "VEGAN",
  "GLUTEN_FREE",
  "SPICY",
  "MEAL_PREP",
  "BEGINNER",
];

const UNIT_TYPES = [
  "GRAM",
  "KILOGRAM",
  "OUNCE",
  "POUND",
  "MILLILITER",
  "LITER",
  "CUP",
  "TABLESPOON",
  "TEASPOON",
  "FLUID_OUNCE",
  "PIECE",
  "BAG",
  "BOTTLE",
  "BOX",
  "CAN",
];

const CATEGORY_TYPES = [
  "PROTEIN",
  "DAIRY",
  "VEGETABLE",
  "FRUIT",
  "GRAIN",
  "SPICE",
  "OIL",
  "BEVERAGE",
  "SNACK",
  "FROZEN",
  "HOUSEHOLD",
];

export default function RecipesPage() {
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterTag, setFilterTag] = useState("");
  const [filterCuisine, setFilterCuisine] = useState("");
  const [expandedId, setExpandedId] = useState(null);
  const [showAddForm, setShowAddForm] = useState(false);
  const [newRecipe, setNewRecipe] = useState({
    name: "",
    description: "",
    servings: "",
    cuisineType: "CHINESE",
    tags: [],
    ingredients: [
      { name: "", quantity: "", unitType: "GRAM", categoryType: "PROTEIN" },
    ],
    steps: [""],
  });

  useEffect(() => {
    loadRecipes();
  }, []);

  async function loadRecipes() {
    try {
      setLoading(true);
      const data = await recipeApi.getAll();
      setRecipes(data);
      setError(null);
    } catch (err) {
      setError("Failed to load recipes");
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch(e) {
    e.preventDefault();
    if (!searchQuery.trim()) {
      loadRecipes();
      return;
    }
    try {
      setLoading(true);
      const data = await recipeApi.searchByName(searchQuery);
      setRecipes(data);
    } catch (err) {
      setError("Search failed");
    } finally {
      setLoading(false);
    }
  }

  async function handleFilterTag(tag) {
    setFilterTag(tag);
    setFilterCuisine("");
    if (!tag) {
      loadRecipes();
      return;
    }
    try {
      setLoading(true);
      const data = await recipeApi.filterByTag(tag);
      setRecipes(data);
    } catch (err) {
      setError("Filter failed");
    } finally {
      setLoading(false);
    }
  }

  async function handleFilterCuisine(cuisine) {
    setFilterCuisine(cuisine);
    setFilterTag("");
    if (!cuisine) {
      loadRecipes();
      return;
    }
    try {
      setLoading(true);
      const data = await recipeApi.filterByCuisine(cuisine);
      setRecipes(data);
    } catch (err) {
      setError("Filter failed");
    } finally {
      setLoading(false);
    }
  }

  async function handleAdd(e) {
    e.preventDefault();
    try {
      await recipeApi.create({
        ...newRecipe,
        servings: parseFloat(newRecipe.servings),
        ingredients: newRecipe.ingredients.map((i) => ({
          ...i,
          quantity: parseFloat(i.quantity),
        })),
      });
      setShowAddForm(false);
      setNewRecipe({
        name: "",
        description: "",
        servings: "",
        cuisineType: "CHINESE",
        tags: [],
        ingredients: [
          { name: "", quantity: "", unitType: "GRAM", categoryType: "PROTEIN" },
        ],
        steps: [""],
      });
      loadRecipes();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to add recipe");
    }
  }

  async function handleDelete(id) {
    if (!confirm("Delete this recipe?")) return;
    try {
      await recipeApi.delete(id);
      loadRecipes();
    } catch (err) {
      setError("Failed to delete");
    }
  }

  // Dynamic form helpers
  function addIngredientRow() {
    setNewRecipe({
      ...newRecipe,
      ingredients: [
        ...newRecipe.ingredients,
        { name: "", quantity: "", unitType: "GRAM", categoryType: "PROTEIN" },
      ],
    });
  }

  function removeIngredientRow(index) {
    setNewRecipe({
      ...newRecipe,
      ingredients: newRecipe.ingredients.filter((_, i) => i !== index),
    });
  }

  function updateIngredient(index, field, value) {
    const updated = [...newRecipe.ingredients];
    updated[index] = { ...updated[index], [field]: value };
    setNewRecipe({ ...newRecipe, ingredients: updated });
  }

  function addStep() {
    setNewRecipe({ ...newRecipe, steps: [...newRecipe.steps, ""] });
  }

  function removeStep(index) {
    setNewRecipe({
      ...newRecipe,
      steps: newRecipe.steps.filter((_, i) => i !== index),
    });
  }

  function updateStep(index, value) {
    const updated = [...newRecipe.steps];
    updated[index] = value;
    setNewRecipe({ ...newRecipe, steps: updated });
  }

  function toggleTag(tag) {
    const tags = newRecipe.tags.includes(tag)
      ? newRecipe.tags.filter((t) => t !== tag)
      : [...newRecipe.tags, tag];
    setNewRecipe({ ...newRecipe, tags });
  }

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">📖 Recipes</h1>
        <button
          onClick={() => setShowAddForm(!showAddForm)}
          className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700"
        >
          {showAddForm ? "Cancel" : "+ Add Recipe"}
        </button>
      </div>

      {error && (
        <div className="bg-red-50 text-red-700 px-4 py-3 rounded-lg mb-4">
          {error}
          <button onClick={() => setError(null)} className="ml-4 underline">
            Dismiss
          </button>
        </div>
      )}

      {/* Search and Filters */}
      <div className="mb-6 space-y-3">
        <form onSubmit={handleSearch} className="flex gap-2">
          <input
            type="text"
            placeholder="Search by recipe name..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="flex-1 border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
          />
          <button
            type="submit"
            className="bg-gray-100 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-200"
          >
            Search
          </button>
        </form>
        <div className="flex gap-4">
          <select
            value={filterTag}
            onChange={(e) => handleFilterTag(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-2"
          >
            <option value="">All Tags</option>
            {RECIPE_TAGS.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
          <select
            value={filterCuisine}
            onChange={(e) => handleFilterCuisine(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-2"
          >
            <option value="">All Cuisines</option>
            {CUISINE_TYPES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
          <button
            onClick={() => {
              setFilterTag("");
              setFilterCuisine("");
              setSearchQuery("");
              loadRecipes();
            }}
            className="text-gray-500 hover:text-gray-700"
          >
            Clear Filters
          </button>
        </div>
      </div>

      {/* Add Recipe Form */}
      {showAddForm && (
        <div className="bg-white border border-gray-200 rounded-lg p-6 mb-6 shadow-sm">
          <h2 className="text-lg font-semibold mb-4">Add New Recipe</h2>
          <form onSubmit={handleAdd}>
            <div className="grid grid-cols-2 gap-4 mb-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Name *
                </label>
                <input
                  type="text"
                  required
                  value={newRecipe.name}
                  onChange={(e) =>
                    setNewRecipe({ ...newRecipe, name: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  placeholder="e.g., Orange Chicken"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Description *
                </label>
                <input
                  type="text"
                  required
                  value={newRecipe.description}
                  onChange={(e) =>
                    setNewRecipe({ ...newRecipe, description: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  placeholder="e.g., Crispy chicken in citrus glaze"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Servings *
                </label>
                <input
                  type="number"
                  required
                  min="0.5"
                  step="0.5"
                  value={newRecipe.servings}
                  onChange={(e) =>
                    setNewRecipe({ ...newRecipe, servings: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Cuisine *
                </label>
                <select
                  value={newRecipe.cuisineType}
                  onChange={(e) =>
                    setNewRecipe({ ...newRecipe, cuisineType: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                >
                  {CUISINE_TYPES.map((c) => (
                    <option key={c} value={c}>
                      {c}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Tags */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Tags
              </label>
              <div className="flex flex-wrap gap-2">
                {RECIPE_TAGS.map((tag) => (
                  <button
                    key={tag}
                    type="button"
                    onClick={() => toggleTag(tag)}
                    className={`text-xs px-3 py-1 rounded-full border transition-colors ${
                      newRecipe.tags.includes(tag)
                        ? "bg-green-100 border-green-400 text-green-800"
                        : "bg-gray-50 border-gray-300 text-gray-600 hover:bg-gray-100"
                    }`}
                  >
                    {tag}
                  </button>
                ))}
              </div>
            </div>

            {/* Ingredients */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Ingredients *
              </label>
              {newRecipe.ingredients.map((ing, i) => (
                <div key={i} className="flex gap-2 mb-2">
                  <input
                    type="text"
                    required
                    placeholder="Name"
                    value={ing.name}
                    onChange={(e) =>
                      updateIngredient(i, "name", e.target.value)
                    }
                    className="flex-1 border border-gray-300 rounded px-2 py-1 text-sm"
                  />
                  <input
                    type="number"
                    required
                    min="0.01"
                    step="0.01"
                    placeholder="Qty"
                    value={ing.quantity}
                    onChange={(e) =>
                      updateIngredient(i, "quantity", e.target.value)
                    }
                    className="w-20 border border-gray-300 rounded px-2 py-1 text-sm"
                  />
                  <select
                    value={ing.unitType}
                    onChange={(e) =>
                      updateIngredient(i, "unitType", e.target.value)
                    }
                    className="border border-gray-300 rounded px-2 py-1 text-sm"
                  >
                    {UNIT_TYPES.map((u) => (
                      <option key={u} value={u}>
                        {u}
                      </option>
                    ))}
                  </select>
                  <select
                    value={ing.categoryType}
                    onChange={(e) =>
                      updateIngredient(i, "categoryType", e.target.value)
                    }
                    className="border border-gray-300 rounded px-2 py-1 text-sm"
                  >
                    {CATEGORY_TYPES.map((c) => (
                      <option key={c} value={c}>
                        {c}
                      </option>
                    ))}
                  </select>
                  {newRecipe.ingredients.length > 1 && (
                    <button
                      type="button"
                      onClick={() => removeIngredientRow(i)}
                      className="text-red-500 text-sm"
                    >
                      ✕
                    </button>
                  )}
                </div>
              ))}
              <button
                type="button"
                onClick={addIngredientRow}
                className="text-sm text-green-600 hover:text-green-800"
              >
                + Add Ingredient
              </button>
            </div>

            {/* Steps */}
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Steps *
              </label>
              {newRecipe.steps.map((step, i) => (
                <div key={i} className="flex gap-2 mb-2">
                  <span className="text-sm text-gray-500 pt-1">{i + 1}.</span>
                  <input
                    type="text"
                    required
                    placeholder={`Step ${i + 1}`}
                    value={step}
                    onChange={(e) => updateStep(i, e.target.value)}
                    className="flex-1 border border-gray-300 rounded px-2 py-1 text-sm"
                  />
                  {newRecipe.steps.length > 1 && (
                    <button
                      type="button"
                      onClick={() => removeStep(i)}
                      className="text-red-500 text-sm"
                    >
                      ✕
                    </button>
                  )}
                </div>
              ))}
              <button
                type="button"
                onClick={addStep}
                className="text-sm text-green-600 hover:text-green-800"
              >
                + Add Step
              </button>
            </div>

            <div className="flex justify-end">
              <button
                type="submit"
                className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700"
              >
                Create Recipe
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Recipe Cards */}
      {recipes.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          <p className="text-lg">No recipes found.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {recipes.map((recipe) => (
            <div
              key={recipe.id}
              className="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden"
            >
              <div
                className="p-4 cursor-pointer hover:bg-gray-50"
                onClick={() =>
                  setExpandedId(expandedId === recipe.id ? null : recipe.id)
                }
              >
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-semibold text-gray-900">
                      {recipe.name}
                    </h3>
                    <p className="text-sm text-gray-500">
                      {recipe.description}
                    </p>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-xs bg-purple-100 text-purple-700 px-2 py-1 rounded">
                      {recipe.cuisineType}
                    </span>
                    <span className="text-sm text-gray-500">
                      Serves {recipe.servings}
                    </span>
                    <span className="text-gray-400">
                      {expandedId === recipe.id ? "▲" : "▼"}
                    </span>
                  </div>
                </div>
                <div className="flex gap-2 mt-2">
                  {recipe.tags.map((tag) => (
                    <span
                      key={tag}
                      className="text-xs bg-green-50 text-green-700 px-2 py-0.5 rounded-full"
                    >
                      {tag}
                    </span>
                  ))}
                </div>
              </div>

              {/* Expanded detail */}
              {expandedId === recipe.id && (
                <div className="border-t border-gray-100 p-4 bg-gray-50">
                  <div className="grid grid-cols-2 gap-6">
                    <div>
                      <h4 className="font-medium text-gray-700 mb-2">
                        Ingredients
                      </h4>
                      <ul className="space-y-1">
                        {recipe.ingredients.map((ing, i) => (
                          <li key={i} className="text-sm text-gray-600">
                            {ing.quantity} {ing.unitType} — {ing.name}
                          </li>
                        ))}
                      </ul>
                    </div>
                    <div>
                      <h4 className="font-medium text-gray-700 mb-2">Steps</h4>
                      <ol className="space-y-1">
                        {recipe.steps.map((step, i) => (
                          <li key={i} className="text-sm text-gray-600">
                            {i + 1}. {step}
                          </li>
                        ))}
                      </ol>
                    </div>
                  </div>
                  <div className="mt-4 flex justify-end">
                    <button
                      onClick={() => handleDelete(recipe.id)}
                      className="text-sm text-red-500 hover:text-red-700"
                    >
                      Delete Recipe
                    </button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
