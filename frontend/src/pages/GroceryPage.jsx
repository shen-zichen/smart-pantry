import { useState, useEffect } from "react";
import groceryApi from "../api/groceryApi";
import mealPlanApi from "../api/mealPlanApi";
import LoadingSpinner from "../components/LoadingSpinner";

export default function GroceryPage() {
  const [plans, setPlans] = useState([]);
  const [selectedPlanId, setSelectedPlanId] = useState("");
  const [groceryList, setGroceryList] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    mealPlanApi
      .getAll()
      .then((data) => {
        setPlans(data);
        setLoading(false);
      })
      .catch(() => {
        setError("Failed to load meal plans");
        setLoading(false);
      });
  }, []);

  async function handleGenerate() {
    if (!selectedPlanId) return;
    try {
      setLoading(true);
      const data = await groceryApi.getList(selectedPlanId);
      setGroceryList(data);
    } catch (err) {
      setError("Failed to generate grocery list");
    } finally {
      setLoading(false);
    }
  }

  // Group grocery items by category
  const groupedItems = groceryList
    ? groceryList.reduce((acc, item) => {
        const cat = item.categoryType || "OTHER";
        if (!acc[cat]) acc[cat] = [];
        acc[cat].push(item);
        return acc;
      }, {})
    : null;

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">🛒 Grocery List</h1>

      {error && (
        <div className="bg-red-50 text-red-700 px-4 py-3 rounded-lg mb-4">
          {error}
          <button onClick={() => setError(null)} className="ml-4 underline">
            Dismiss
          </button>
        </div>
      )}

      {/* Plan Selector */}
      <div className="bg-white border border-gray-200 rounded-lg p-6 mb-6 shadow-sm">
        <h2 className="text-lg font-semibold mb-4">Select a Meal Plan</h2>
        <div className="flex gap-4 items-end">
          <div className="flex-1">
            <select
              value={selectedPlanId}
              onChange={(e) => setSelectedPlanId(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            >
              <option value="">Choose a plan...</option>
              {plans.map((plan) => (
                <option key={plan.id} value={plan.id}>
                  {plan.strategyName} — {plan.days} days (Created:{" "}
                  {plan.createdDate})
                </option>
              ))}
            </select>
          </div>
          <button
            onClick={handleGenerate}
            disabled={!selectedPlanId}
            className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Generate List
          </button>
        </div>
      </div>

      {/* Grocery List */}
      {groceryList &&
        (groceryList.length === 0 ? (
          <div className="bg-green-50 border border-green-200 rounded-lg p-6 text-center">
            <p className="text-green-800 text-lg font-medium">
              Your pantry has everything you need — no shopping required! 🎉
            </p>
          </div>
        ) : (
          <div className="bg-white border border-gray-200 rounded-lg shadow-sm">
            <div className="p-4 border-b border-gray-200">
              <h2 className="font-semibold text-gray-900">
                Shopping List ({groceryList.length} items)
              </h2>
            </div>
            <div className="p-4">
              {Object.entries(groupedItems).map(([category, items]) => (
                <div key={category} className="mb-4 last:mb-0">
                  <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wide mb-2">
                    {category}
                  </h3>
                  {items.map((item, i) => (
                    <div
                      key={i}
                      className="flex items-center justify-between py-2 border-b border-gray-50 last:border-0"
                    >
                      <span className="text-gray-800">
                        {item.ingredientName}
                      </span>
                      <span className="text-sm font-medium text-orange-600">
                        Buy {item.deficit} {item.unitType}
                      </span>
                    </div>
                  ))}
                </div>
              ))}
            </div>
          </div>
        ))}
    </div>
  );
}
