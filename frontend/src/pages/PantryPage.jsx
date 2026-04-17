import { useState, useEffect } from "react";
import pantryApi from "../api/pantryApi";
import LoadingSpinner from "../components/LoadingSpinner";

// Dropdown options — must match the Java enums exactly
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

export default function PantryPage() {
  // State
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [showAddForm, setShowAddForm] = useState(false);
  const [newItem, setNewItem] = useState({
    name: "",
    quantity: "",
    unitType: "GRAM",
    categoryType: "PROTEIN",
    boughtDate: new Date().toISOString().split("T")[0],
    expirationDate: "",
    lowStockThreshold: "",
  });

  // Load pantry items on page load
  useEffect(() => {
    loadItems();
  }, []);

  async function loadItems() {
    try {
      setLoading(true);
      const data = await pantryApi.getAll();
      setItems(data);
      setError(null);
    } catch (err) {
      setError("Failed to load pantry items");
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch(e) {
    e.preventDefault();
    if (!searchQuery.trim()) {
      loadItems();
      return;
    }
    try {
      setLoading(true);
      const data = await pantryApi.search(searchQuery);
      setItems(data);
    } catch (err) {
      setError("Search failed");
    } finally {
      setLoading(false);
    }
  }

  async function handleAdd(e) {
    e.preventDefault();
    try {
      await pantryApi.create({
        ...newItem,
        quantity: parseFloat(newItem.quantity),
        lowStockThreshold: parseFloat(newItem.lowStockThreshold) || 0,
        expirationDate: newItem.expirationDate || null,
      });
      setShowAddForm(false);
      setNewItem({
        name: "",
        quantity: "",
        unitType: "GRAM",
        categoryType: "PROTEIN",
        boughtDate: new Date().toISOString().split("T")[0],
        expirationDate: "",
        lowStockThreshold: "",
      });
      loadItems(); // refresh the list
    } catch (err) {
      setError(err.response?.data?.message || "Failed to add item");
    }
  }

  async function handleConsume(id) {
    const amount = prompt("How much to consume?");
    if (!amount || isNaN(amount)) return;
    try {
      await pantryApi.consume(id, parseFloat(amount));
      loadItems();
    } catch (err) {
      setError("Failed to consume");
    }
  }

  async function handleRestock(id) {
    const amount = prompt("How much to add?");
    if (!amount || isNaN(amount)) return;
    try {
      await pantryApi.restock(id, parseFloat(amount));
      loadItems();
    } catch (err) {
      setError("Failed to restock");
    }
  }

  async function handleDelete(id) {
    if (!confirm("Are you sure you want to delete this item?")) return;
    try {
      await pantryApi.delete(id);
      loadItems();
    } catch (err) {
      setError("Failed to delete");
    }
  }

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">🥩 Pantry</h1>
        <button
          onClick={() => setShowAddForm(!showAddForm)}
          className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors"
        >
          {showAddForm ? "Cancel" : "+ Add Item"}
        </button>
      </div>

      {/* Error banner */}
      {error && (
        <div className="bg-red-50 text-red-700 px-4 py-3 rounded-lg mb-4">
          {error}
          <button onClick={() => setError(null)} className="ml-4 underline">
            Dismiss
          </button>
        </div>
      )}

      {/* Search bar */}
      <form onSubmit={handleSearch} className="mb-6">
        <div className="flex gap-2">
          <input
            type="text"
            placeholder="Search by ingredient name..."
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
          <button
            type="button"
            onClick={() => {
              setSearchQuery("");
              loadItems();
            }}
            className="text-gray-500 px-3 py-2 hover:text-gray-700"
          >
            Clear
          </button>
        </div>
      </form>

      {/* Add Item Form */}
      {showAddForm && (
        <div className="bg-white border border-gray-200 rounded-lg p-6 mb-6 shadow-sm">
          <h2 className="text-lg font-semibold mb-4">Add New Item</h2>
          <form onSubmit={handleAdd}>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Name *
                </label>
                <input
                  type="text"
                  required
                  value={newItem.name}
                  onChange={(e) =>
                    setNewItem({ ...newItem, name: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="e.g., Chicken Thigh"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Quantity *
                </label>
                <input
                  type="number"
                  required
                  min="0.01"
                  step="0.01"
                  value={newItem.quantity}
                  onChange={(e) =>
                    setNewItem({ ...newItem, quantity: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="e.g., 500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Unit *
                </label>
                <select
                  value={newItem.unitType}
                  onChange={(e) =>
                    setNewItem({ ...newItem, unitType: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                >
                  {UNIT_TYPES.map((u) => (
                    <option key={u} value={u}>
                      {u}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Category *
                </label>
                <select
                  value={newItem.categoryType}
                  onChange={(e) =>
                    setNewItem({ ...newItem, categoryType: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                >
                  {CATEGORY_TYPES.map((c) => (
                    <option key={c} value={c}>
                      {c}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Bought Date *
                </label>
                <input
                  type="date"
                  required
                  value={newItem.boughtDate}
                  onChange={(e) =>
                    setNewItem({ ...newItem, boughtDate: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Expiration Date
                </label>
                <input
                  type="date"
                  value={newItem.expirationDate}
                  onChange={(e) =>
                    setNewItem({ ...newItem, expirationDate: e.target.value })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                />
                <span className="text-xs text-gray-500">
                  Leave empty for non-perishables
                </span>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Low Stock Threshold
                </label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  value={newItem.lowStockThreshold}
                  onChange={(e) =>
                    setNewItem({
                      ...newItem,
                      lowStockThreshold: e.target.value,
                    })
                  }
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                  placeholder="e.g., 200"
                />
              </div>
            </div>
            <div className="mt-4 flex justify-end">
              <button
                type="submit"
                className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700"
              >
                Add to Pantry
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Items Grid */}
      {items.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          <p className="text-lg">Your pantry is empty!</p>
          <p className="text-sm mt-1">
            Click "+ Add Item" to start stocking up.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {items.map((item) => (
            <div
              key={item.id}
              className="bg-white border border-gray-200 rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow"
            >
              {/* Item header */}
              <div className="flex items-start justify-between mb-2">
                <h3 className="font-semibold text-gray-900">
                  {item.ingredientName}
                </h3>
                <span className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded">
                  {item.categoryType}
                </span>
              </div>

              {/* Quantity */}
              <p className="text-2xl font-bold text-gray-800 mb-1">
                {item.quantityInStock}{" "}
                <span className="text-sm font-normal text-gray-500">
                  {item.unitType}
                </span>
              </p>

              {/* Badges */}
              <div className="flex gap-2 mb-3">
                {item.lowStock && (
                  <span className="text-xs bg-amber-100 text-amber-800 px-2 py-1 rounded-full">
                    ⚠️ Low Stock
                  </span>
                )}
                {item.expiringSoon && (
                  <span className="text-xs bg-red-100 text-red-800 px-2 py-1 rounded-full">
                    🔴 Expiring Soon
                  </span>
                )}
              </div>

              {/* Dates */}
              <div className="text-xs text-gray-500 mb-3">
                <p>Bought: {item.boughtDate}</p>
                {item.expirationDate && <p>Expires: {item.expirationDate}</p>}
              </div>

              {/* Action buttons */}
              <div className="flex gap-2">
                <button
                  onClick={() => handleConsume(item.id)}
                  className="flex-1 text-sm bg-orange-50 text-orange-700 px-3 py-1.5 rounded hover:bg-orange-100"
                >
                  Consume
                </button>
                <button
                  onClick={() => handleRestock(item.id)}
                  className="flex-1 text-sm bg-blue-50 text-blue-700 px-3 py-1.5 rounded hover:bg-blue-100"
                >
                  Restock
                </button>
                <button
                  onClick={() => handleDelete(item.id)}
                  className="text-sm text-red-500 px-2 py-1.5 hover:text-red-700"
                >
                  🗑
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
