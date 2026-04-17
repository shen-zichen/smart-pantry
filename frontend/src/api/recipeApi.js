import api from "./client";

const recipeApi = {
  getAll: () => api.get("/recipes").then((res) => res.data),

  getById: (id) => api.get(`/recipes/${id}`).then((res) => res.data),

  create: (data) => api.post("/recipes", data).then((res) => res.data),

  delete: (id) => api.delete(`/recipes/${id}`),

  searchByName: (name) =>
    api.get(`/recipes/search?name=${name}`).then((res) => res.data),

  filterByTag: (tag) =>
    api.get(`/recipes/filter/tag?tag=${tag}`).then((res) => res.data),

  filterByCuisine: (cuisine) =>
    api
      .get(`/recipes/filter/cuisine?cuisine=${cuisine}`)
      .then((res) => res.data),
};

export default recipeApi;
