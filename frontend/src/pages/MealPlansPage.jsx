import { useState, useEffect } from "react";
import mealPlanApi from "../api/mealPlanApi";
import LoadingSpinner from "../components/LoadingSpinner";

export default function MealPlansPage() {
  const [plans, setPlans] = useState([]);
  const [strategies, setStrategies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedStrategy, setSelectedStrategy] = useState("");
  const [days, setDays] = useState(3);
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
      const plan = await mealPlanApi.generate(selectedStrategy, days);
      setPlans([plan, ...plans]);
      setExpandedId(plan.id);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to generate meal plan");
    }
  }

  async function handleConsume(planId) {
    if (
      !confirm(
        "Mark this plan as cooked? Ingredients will be deducted from your pantry.",
      )
    )
      return;
    try {
      await mealPlanApi.postMealConsume(planId);
      alert("Pantry updated! Ingredients have been deducted.");
    } catch (err) {
      setError("Failed to consume meal plan");
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
              Days
            </label>
            <input
              type="number"
              min="1"
              max="14"
              value={days}
              onChange={(e) => setDays(parseInt(e.target.value))}
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
          {plans.map((plan) => (
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
                    <h3 className="font-semibold text-gray-900">
                      {plan.strategyName} — {plan.days} day
                      {plan.days > 1 ? "s" : ""}
                    </h3>
                    <p className="text-sm text-gray-500">
                      Created: {plan.createdDate} · {plan.recipes.length} recipe
                      {plan.recipes.length > 1 ? "s" : ""}
                    </p>
                  </div>
                  <span className="text-gray-400">
                    {expandedId === plan.id ? "▲" : "▼"}
                  </span>
                </div>
              </div>

              {expandedId === plan.id && (
                <div className="border-t border-gray-100 p-4 bg-gray-50">
                  {plan.recipes.map((recipe, i) => (
                    <div key={i} className="mb-4 last:mb-0">
                      <h4 className="font-medium text-gray-800">
                        Day {i + 1}: {recipe.name}
                      </h4>
                      <p className="text-sm text-gray-500 mb-1">
                        {recipe.description}
                      </p>
                      <div className="text-xs text-gray-500">
                        Ingredients:{" "}
                        {recipe.ingredients
                          .map(
                            (ing) =>
                              `${ing.quantity} ${ing.unitType} ${ing.name}`,
                          )
                          .join(", ")}
                      </div>
                    </div>
                  ))}
                  <div className="mt-4 pt-4 border-t border-gray-200">
                    <button
                      onClick={() => handleConsume(plan.id)}
                      className="bg-orange-500 text-white px-4 py-2 rounded-lg hover:bg-orange-600 text-sm"
                    >
                      🍳 I Cooked This — Deduct from Pantry
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
