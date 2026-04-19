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
  const [boughtItems, setBoughtItems] = useState(new Set());
  const [successMsg, setSuccessMsg] = useState(null);

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
      setBoughtItems(new Set());
      setSuccessMsg(null);
      const data = await groceryApi.getList(selectedPlanId);
      setGroceryList(data);
    } catch (err) {
      setError("Failed to generate grocery list");
    } finally {
      setLoading(false);
    }
  }

  async function handleBuyItem(item, index) {
    try {
      await groceryApi.markBought([
        {
          name: item.ingredientName,
          quantity: item.deficit,
          unitType: item.unitType,
          categoryType: item.categoryType,
        },
      ]);
      setBoughtItems(new Set([...boughtItems, index]));
      setSuccessMsg(`✓ ${item.ingredientName} added to pantry!`);
      setTimeout(() => setSuccessMsg(null), 3000);
    } catch (err) {
      setError("Failed to mark item as bought");
    }
  }

  async function handleBuyAll() {
    if (!groceryList || groceryList.length === 0) return;
    const unboughtItems = groceryList.filter((_, i) => !boughtItems.has(i));
    if (unboughtItems.length === 0) return;

    try {
      await groceryApi.markBought(
        unboughtItems.map((item) => ({
          name: item.ingredientName,
          quantity: item.deficit,
          unitType: item.unitType,
          categoryType: item.categoryType,
        })),
      );
      // Mark all as bought
      const allIndexes = new Set(groceryList.map((_, i) => i));
      setBoughtItems(allIndexes);
      setSuccessMsg("✓ All items purchased and added to pantry!");
      setTimeout(() => setSuccessMsg(null), 3000);
    } catch (err) {
      setError("Failed to mark all items as bought");
    }
  }

  // Group grocery items by category
  const groupedItems = groceryList
    ? groceryList.reduce((acc, item, index) => {
        const cat = item.categoryType || "OTHER";
        if (!acc[cat]) acc[cat] = [];
        acc[cat].push({ ...item, _index: index });
        return acc;
      }, {})
    : null;

  const unboughtCount = groceryList
    ? groceryList.filter((_, i) => !boughtItems.has(i)).length
    : 0;

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">
        🛒 Grocery List
      </h1>

      {error && (
        <div className="bg-red-50 text-red-700 px-4 py-3 rounded-lg mb-4">
          {error}
          <button onClick={() => setError(null)} className="ml-4 underline">
            Dismiss
          </button>
        </div>
      )}

      {successMsg && (
        <div className="bg-green-50 text-green-700 px-4 py-3 rounded-lg mb-4 flex items-center">
          {successMsg}
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
                  {plan.strategyName} — {plan.days} meals (Created:{" "}
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
            <div className="p-4 border-b border-gray-200 flex items-center justify-between">
              <h2 className="font-semibold text-gray-900">
                Shopping List ({unboughtCount} remaining)
              </h2>
              {unboughtCount > 0 && (
                <button
                  onClick={handleBuyAll}
                  className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 text-sm font-medium"
                >
                  ✓ Bought All
                </button>
              )}
            </div>
            <div className="p-4">
              {Object.entries(groupedItems).map(([category, items]) => (
                <div key={category} className="mb-4 last:mb-0">
                  <h3 className="text-sm font-medium text-gray-500 uppercase tracking-wide mb-2">
                    {category}
                  </h3>
                  {items.map((item) => {
                    const isBought = boughtItems.has(item._index);
                    return (
                      <div
                        key={item._index}
                        className={`flex items-center justify-between py-2 px-2 rounded border-b border-gray-50 last:border-0 transition-colors ${
                          isBought ? "bg-green-50 opacity-60" : ""
                        }`}
                      >
                        <span
                          className={`${
                            isBought
                              ? "text-gray-400 line-through"
                              : "text-gray-800"
                          }`}
                        >
                          {item.ingredientName}
                        </span>
                        <div className="flex items-center gap-3">
                          <span
                            className={`text-sm font-medium ${
                              isBought ? "text-gray-400" : "text-orange-600"
                            }`}
                          >
                            {isBought
                              ? "Purchased"
                              : `Buy ${item.deficit} ${item.unitType}`}
                          </span>
                          {!isBought && (
                            <button
                              onClick={() =>
                                handleBuyItem(item, item._index)
                              }
                              className="bg-green-100 text-green-700 px-3 py-1 rounded-lg text-sm font-medium hover:bg-green-200 transition-colors"
                            >
                              ✓ Bought
                            </button>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              ))}
            </div>
          </div>
        ))}
    </div>
  );
}
