import { useState, useEffect } from "react";
import twinRecipeApi from "../api/twinRecipeApi";
import LoadingSpinner from "../components/LoadingSpinner";

export default function TwinRecipesPage() {
  const [twins, setTwins] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadTwins();
  }, []);

  async function loadTwins() {
    try {
      setLoading(true);
      const data = await twinRecipeApi.getAll();
      setTwins(data);
    } catch (err) {
      setError("Failed to load twin recipes");
    } finally {
      setLoading(false);
    }
  }

  async function handleSwap(id) {
    try {
      const updated = await twinRecipeApi.swap(id);
      setTwins(twins.map((t) => (t.id === id ? updated : t)));
    } catch (err) {
      setError("Failed to swap");
    }
  }

  async function handleDelete(id) {
    if (!confirm("Delete this twin recipe?")) return;
    try {
      await twinRecipeApi.delete(id);
      loadTwins();
    } catch (err) {
      setError("Failed to delete");
    }
  }

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">🔄 Twin Recipes</h1>

      {error && (
        <div className="bg-red-50 text-red-700 px-4 py-3 rounded-lg mb-4">
          {error}
          <button onClick={() => setError(null)} className="ml-4 underline">
            Dismiss
          </button>
        </div>
      )}

      {twins.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          <p className="text-lg">No twin recipes yet.</p>
          <p className="text-sm mt-1">
            Twin recipes pair a healthy and guilty version of the same dish.
          </p>
        </div>
      ) : (
        <div className="space-y-6">
          {twins.map((twin) => (
            <div
              key={twin.id}
              className="bg-white border border-gray-200 rounded-lg shadow-sm p-6"
            >
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-bold text-gray-900">{twin.name}</h2>
                <div className="flex gap-2">
                  <button
                    onClick={() => handleSwap(twin.id)}
                    className="bg-purple-100 text-purple-700 px-4 py-2 rounded-lg hover:bg-purple-200 text-sm font-medium"
                  >
                    🔄 Swap
                  </button>
                  <button
                    onClick={() => handleDelete(twin.id)}
                    className="text-red-500 hover:text-red-700 text-sm"
                  >
                    🗑
                  </button>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                {/* Healthy variant */}
                <div
                  className={`rounded-lg p-4 border-2 ${
                    twin.healthyActive
                      ? "border-green-400 bg-green-50"
                      : "border-gray-200 bg-gray-50 opacity-60"
                  }`}
                >
                  <div className="flex items-center gap-2 mb-2">
                    <span className="text-sm font-medium text-green-700">
                      🥗 Healthy
                    </span>
                    {twin.healthyActive && (
                      <span className="text-xs bg-green-200 text-green-800 px-2 py-0.5 rounded-full">
                        ACTIVE
                      </span>
                    )}
                  </div>
                  <h3 className="font-semibold">{twin.healthyVariant.name}</h3>
                  <p className="text-sm text-gray-500 mb-2">
                    {twin.healthyVariant.description}
                  </p>
                  <p className="text-xs text-gray-400">
                    Serves {twin.healthyVariant.servings}
                  </p>
                </div>

                {/* Guilty variant */}
                <div
                  className={`rounded-lg p-4 border-2 ${
                    !twin.healthyActive
                      ? "border-orange-400 bg-orange-50"
                      : "border-gray-200 bg-gray-50 opacity-60"
                  }`}
                >
                  <div className="flex items-center gap-2 mb-2">
                    <span className="text-sm font-medium text-orange-700">
                      🍕 Guilty Pleasure
                    </span>
                    {!twin.healthyActive && (
                      <span className="text-xs bg-orange-200 text-orange-800 px-2 py-0.5 rounded-full">
                        ACTIVE
                      </span>
                    )}
                  </div>
                  <h3 className="font-semibold">{twin.guiltyVariant.name}</h3>
                  <p className="text-sm text-gray-500 mb-2">
                    {twin.guiltyVariant.description}
                  </p>
                  <p className="text-xs text-gray-400">
                    Serves {twin.guiltyVariant.servings}
                  </p>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
