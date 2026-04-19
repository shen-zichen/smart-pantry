import { NavLink } from "react-router-dom";

const links = [
  { to: "/pantry", label: "🥩 Pantry" },
  { to: "/recipes", label: "📖 Recipes" },
  { to: "/meal-plans", label: "📅 Meal Plans" },
  { to: "/grocery", label: "🛒 Grocery" },
  { to: "/settings", label: "⚙️ Settings" },
];

export default function Navbar() {
  return (
    <nav className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <NavLink to="/" className="text-xl font-bold text-green-700">
            🍳 Smart Pantry
          </NavLink>

          {/* Navigation Links */}
          <div className="flex space-x-1">
            {links.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                className={({ isActive }) =>
                  `px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                    isActive
                      ? "bg-green-100 text-green-800"
                      : "text-gray-600 hover:bg-gray-100 hover:text-gray-900"
                  }`
                }
              >
                {link.label}
              </NavLink>
            ))}
          </div>
        </div>
      </div>
    </nav>
  );
}
