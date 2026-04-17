import { useState } from "react";

export default function SettingsPage() {
  // Read from localStorage, default to 'casual'
  const [displayMode, setDisplayMode] = useState(
    localStorage.getItem("displayMode") || "casual",
  );

  function handleModeChange(mode) {
    setDisplayMode(mode);
    localStorage.setItem("displayMode", mode);
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">⚙️ Settings</h1>

      <div className="bg-white border border-gray-200 rounded-lg shadow-sm p-6 max-w-lg">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">
          Display Mode
        </h2>
        <p className="text-sm text-gray-500 mb-4">
          Controls how ingredient quantities are displayed throughout the app.
        </p>

        <div className="space-y-3">
          <label
            className={`flex items-center p-4 rounded-lg border-2 cursor-pointer transition-colors ${
              displayMode === "casual"
                ? "border-green-400 bg-green-50"
                : "border-gray-200 hover:bg-gray-50"
            }`}
          >
            <input
              type="radio"
              name="displayMode"
              value="casual"
              checked={displayMode === "casual"}
              onChange={() => handleModeChange("casual")}
              className="mr-3"
            />
            <div>
              <p className="font-medium">🍳 Casual Mode</p>
              <p className="text-sm text-gray-500">
                "2 chicken thighs" · "a pinch of salt" · Whole-item friendly
              </p>
            </div>
          </label>

          <label
            className={`flex items-center p-4 rounded-lg border-2 cursor-pointer transition-colors ${
              displayMode === "professional"
                ? "border-green-400 bg-green-50"
                : "border-gray-200 hover:bg-gray-50"
            }`}
          >
            <input
              type="radio"
              name="displayMode"
              value="professional"
              checked={displayMode === "professional"}
              onChange={() => handleModeChange("professional")}
              className="mr-3"
            />
            <div>
              <p className="font-medium">📊 Professional Mode</p>
              <p className="text-sm text-gray-500">
                "500g chicken thigh (100%)" · Metric normalized · Anchor
                percentages
              </p>
            </div>
          </label>
        </div>
      </div>

      {/* App Info */}
      <div className="bg-white border border-gray-200 rounded-lg shadow-sm p-6 max-w-lg mt-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-2">About</h2>
        <p className="text-sm text-gray-500">
          Smart Pantry & Meal Manager v1.5
        </p>
        <p className="text-sm text-gray-500">Built with Spring Boot + React</p>
        <p className="text-sm text-gray-500">By Benny & Xiaoyang</p>
      </div>
    </div>
  );
}
