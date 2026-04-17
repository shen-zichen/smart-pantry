import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Layout from "./components/Layout";
import PantryPage from "./pages/PantryPage";
import RecipesPage from "./pages/RecipesPage";
import TwinRecipesPage from "./pages/TwinRecipesPage";
import MealPlansPage from "./pages/MealPlansPage";
import GroceryPage from "./pages/GroceryPage";
import SettingsPage from "./pages/SettingsPage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/pantry" replace />} />
          <Route path="pantry" element={<PantryPage />} />
          <Route path="recipes" element={<RecipesPage />} />
          <Route path="twin-recipes" element={<TwinRecipesPage />} />
          <Route path="meal-plans" element={<MealPlansPage />} />
          <Route path="grocery" element={<GroceryPage />} />
          <Route path="settings" element={<SettingsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
