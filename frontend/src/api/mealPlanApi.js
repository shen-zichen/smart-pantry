import api from "./client";

const mealPlanApi = {
  generate: (strategyName, days) =>
    api
      .post("/meal-plans/generate", { strategyName, days })
      .then((res) => res.data),

  getAll: () => api.get("/meal-plans").then((res) => res.data),

  getById: (id) => api.get(`/meal-plans/${id}`).then((res) => res.data),

  postMealConsume: (id) =>
    api.post(`/meal-plans/${id}/consume`).then((res) => res.data),

  getStrategies: () =>
    api.get("/meal-plans/strategies").then((res) => res.data),
};

export default mealPlanApi;
