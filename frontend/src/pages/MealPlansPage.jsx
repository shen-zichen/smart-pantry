import { useState, useEffect } from "react";
import mealPlanApi from "../api/mealPlanApi";
import LoadingSpinner from "../components/LoadingSpinner";

export default function MealPlansPage() {
  const [plans, setPlans] = useState([]);
  const [strategies, setStrategies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedStrategy, setSelectedStrategy] = useState("");
  const [meals, setMeals] = useState(3);
  const [expandedId, setExpandedId] = useState(null);

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      setLoading(true);
      const [plansData, strategiesData] = await Promise.all([
        mealPlanApi.getAll(),
        mealPlanApi.getStrategies(),
      ]);
      setPlans(plansData);
      setStrategies(strategiesData);
      if (strategiesData.length > 0) setSelectedStrategy(strategiesData[0]);
    } catch (err) {
      setError("Failed to load data");
    } finally {
      setLoading(false);
    }
  }

  async function handleGenerate(e) {
    e.preventDefault();
    try {
      const plan = await mealPlanApi.generate(selectedStrategy, meals);
      setPlans([plan, ...plans]);
      setExpandedId(plan.id);
      if (plan.requiresGroceryRun) {
        window.alert("Not enough food in stock to complete this plan entirely from your pantry. Please check the Grocery List to buy missing items!");
      }
    } catch (err) {
      setError(err.response?.data?.message || "Failed to generate meal plan");
    }
  }

  async function handleMarkCooked(planId, recipeIndex) {
    try {
      const updated = await mealPlanApi.markCooked(planId, recipeIndex);
      setPlans(plans.map((p) => (p.id === planId ? updated : p)));
    } catch (err) {
      setError(
        err.response?.data?.message || "Failed to mark recipe as cooked",
      );
    }
  }

  async function handleDelete(planId) {
    if (!confirm("Delete this meal plan?")) return;
    try {
      await mealPlanApi.deletePlan(planId);
      setPlans(plans.filter((p) => p.id !== planId));
      if (expandedId === planId) setExpandedId(null);
    } catch (err) {
      setError("Failed to delete meal plan");
    }
  }

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">📅 Meal Plans</h1>

      {error && (
        <div className="bg-red-50 text-red-700 px-4 py-3 rounded-lg mb-4">
          {error}
          <button onClick={() => setError(null)} className="ml-4 underline">
            Dismiss
          </button>
        </div>
      )}

      {/* Generate Form */}
      <div className="bg-white border border-gray-200 rounded-lg p-6 mb-6 shadow-sm">
        <h2 className="text-lg font-semibold mb-4">Generate a New Plan</h2>
        <form onSubmit={handleGenerate} className="flex gap-4 items-end">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Strategy
            </label>
            <select
              value={selectedStrategy}
              onChange={(e) => setSelectedStrategy(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-2"
            >
              {strategies.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Meals
            </label>
            <input
              type="number"
              min="1"
              max="14"
              value={meals}
              onChange={(e) => setMeals(parseInt(e.target.value))}
              className="w-20 border border-gray-300 rounded-lg px-3 py-2"
            />
          </div>
          <button
            type="submit"
            className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700"
          >
            Generate Plan
          </button>
        </form>
      </div>

      {/* Plans List */}
      {plans.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          <p className="text-lg">No meal plans yet.</p>
          <p className="text-sm mt-1">Generate one above!</p>
        </div>
      ) : (
        <div className="space-y-4">
          {plans.map((plan) => {
            const cookedSet = new Set(plan.cookedIndexes || []);
            const totalRecipes = plan.recipes.length;
            const cookedCount = cookedSet.size;
            const allCooked = cookedCount === totalRecipes;

            return (
              <div
                key={plan.id}
                className="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden"
              >
                <div
                  className="p-4 cursor-pointer hover:bg-gray-50"
                  onClick={() =>
                    setExpandedId(expandedId === plan.id ? null : plan.id)
                  }
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <h3 className="font-semibold text-gray-900 flex items-center flex-wrap gap-2">
                        {plan.strategyName} — {plan.days} meal
                        {plan.days > 1 ? "s" : ""}
                        {plan.requiresGroceryRun && (
                          <span className="bg-orange-100 text-orange-800 text-xs px-2 py-1 rounded-full flex items-center gap-1 shadow-sm border border-orange-200">
                            🛒 Requires Groceries
                          </span>
                        )}
                      </h3>
                      <p className="text-sm text-gray-500">
                        Created: {plan.createdDate} · {totalRecipes} recipe
                        {totalRecipes > 1 ? "s" : ""}
                      </p>
                    </div>
                    <div className="flex items-center gap-3">
                      {/* Progress badge */}
                      <span
                        className={`text-xs px-2 py-1 rounded-full font-medium ${
                          allCooked
                            ? "bg-green-100 text-green-800"
                            : cookedCount > 0
                              ? "bg-yellow-100 text-yellow-800"
                              : "bg-gray-100 text-gray-600"
                        }`}
                      >
                        {cookedCount}/{totalRecipes} cooked
                      </span>
                      {/* Delete button */}
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(plan.id);
                        }}
                        className="text-red-400 hover:text-red-600 text-sm"
                        title="Delete plan"
                      >
                        🗑
                      </button>
                      <span className="text-gray-400">
                        {expandedId === plan.id ? "▲" : "▼"}
                      </span>
                    </div>
                  </div>
                </div>

                {expandedId === plan.id && (
                  <div className="border-t border-gray-100 p-4 bg-gray-50">
                    {plan.recipes.map((recipe, i) => {
                      const isCooked = cookedSet.has(i);
                      return (
                        <div
                          key={i}
                          className={`flex items-start gap-3 mb-4 last:mb-0 p-3 rounded-lg transition-colors ${
                            isCooked ? "bg-green-50" : "bg-white"
                          }`}
                        >
                          {/* Checkbox */}
                          <button
                            onClick={() =>
                              !isCooked && handleMarkCooked(plan.id, i)
                            }
                            disabled={isCooked}
                            className={`mt-0.5 flex-shrink-0 w-6 h-6 rounded border-2 flex items-center justify-center transition-colors ${
                              isCooked
                                ? "bg-green-500 border-green-500 text-white cursor-default"
                                : "border-gray-300 hover:border-green-400 cursor-pointer"
                            }`}
                            title={
                              isCooked
                                ? "Already cooked"
                                : "Mark as cooked (deducts from pantry)"
                            }
                          >
                            {isCooked && (
                              <span className="text-xs font-bold">✓</span>
                            )}
                          </button>

                          {/* Recipe info */}
                          <div className="flex-1">
                            <h4
                              className={`font-medium ${
                                isCooked
                                  ? "text-gray-400 line-through"
                                  : "text-gray-800"
                              }`}
                            >
                              {recipe.name} <span className="text-sm font-normal text-gray-500">({recipe.servings} serving{recipe.servings !== 1 ? 's' : ''})</span>
                            </h4>
                            <p
                              className={`text-sm mb-1 ${
                                isCooked ? "text-gray-300" : "text-gray-500"
                              }`}
                            >
                              {recipe.description}
                            </p>
                            <div
                              className={`text-xs ${
                                isCooked ? "text-gray-300" : "text-gray-500"
                              }`}
                            >
                              Ingredients:{" "}
                              {recipe.ingredients
                                .map(
                                  (ing) =>
                                    `${ing.quantity} ${ing.unitType} ${ing.name}`,
                                )
                                .join(", ")}
                            </div>
                          </div>

                          {/* Status label */}
                          {isCooked && (
                            <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full font-medium">
                              ✓ Cooked
                            </span>
                          )}
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
